package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.mappers.toAuthTokens
import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.TokenRefreshResponseDto
import com.po4yka.bitesizereader.util.config.AppConfig
import kotlin.time.Clock
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class ApiClient(
    engine: io.ktor.client.engine.HttpClientEngine,
    private val baseUrl: String,
    private val secureStorage: SecureStorage,
) {
    val client =
        HttpClient(engine) {
            expectSuccess = true

            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    },
                )
            }

            // Enable response compression for faster sync operations
            install(ContentEncoding) {
                gzip()
                deflate()
            }

            install(io.ktor.client.plugins.HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 60_000
                socketTimeoutMillis = 60_000
            }

            if (AppConfig.Api.loggingEnabled) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                    sanitizeHeader { header -> header == HttpHeaders.Authorization }
                    filter { request -> !request.url.encodedPath.contains("db-dump") }
                }
            }

            install(DefaultRequest) {
                url(this@ApiClient.baseUrl)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
            }

            install(HttpCallValidator) {
                handleResponseExceptionWithRequest { cause: Throwable, request: io.ktor.client.request.HttpRequest ->
                    // Don't log cancellation exceptions as errors - they're expected during navigation
                    if (cause is kotlin.coroutines.cancellation.CancellationException) {
                        logger.debug {
                            "Request cancelled: ${request.method.value} ${request.url}"
                        }
                        return@handleResponseExceptionWithRequest
                    }

                    val response = (cause as? io.ktor.client.plugins.ResponseException)?.response
                    val bodySnippet =
                        response?.let {
                            runCatching { response.bodyAsText().take(2_000) }
                                .getOrElse { "<body unavailable: ${it.message}>" }
                        } ?: "<no response body>"
                    val statusPart = response?.status?.toString() ?: "<no status>"
                    val statusCode = response?.status?.value ?: 0

                    val message =
                        """
                        HTTP error while handling ${request.method.value} ${request.url}
                        Status: $statusPart
                        Body (truncated): $bodySnippet
                        """.trimIndent()

                    // Log 4xx client errors as WARN (often expected: 401, 404, etc.)
                    // Log 5xx server errors as ERROR (unexpected server failures)
                    if (statusCode in 400..499) {
                        logger.warn(cause) { message }
                    } else {
                        logger.error(cause) { message }
                    }
                }
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = secureStorage.getAccessToken()
                        val refreshToken = secureStorage.getRefreshToken()
                        if (accessToken != null && refreshToken != null) {
                            BearerTokens(accessToken, refreshToken)
                        } else {
                            null
                        }
                    }
                    refreshTokens {
                        val refreshToken = secureStorage.getRefreshToken()
                        if (refreshToken != null) {
                            try {
                                val response =
                                    client.post("/v1/auth/refresh") {
                                        setBody(mapOf("refresh_token" to refreshToken))
                                    }
                                val responseText = response.bodyAsText()
                                val parsed: ApiResponseDto<TokenRefreshResponseDto> =
                                    Json.decodeFromString(responseText)

                                if (parsed.success && parsed.data != null) {
                                    val tokens =
                                        parsed.data.toAuthTokens(
                                            currentTime = Clock.System.now(),
                                            refreshToken = refreshToken,
                                        )
                                    secureStorage.saveAccessToken(tokens.accessToken)
                                    secureStorage.saveRefreshToken(tokens.refreshToken)
                                    BearerTokens(tokens.accessToken, tokens.refreshToken)
                                } else {
                                    logger.error {
                                        "Failed to refresh tokens: " +
                                            "success=${parsed.success}, error=${parsed.error}"
                                    }
                                    secureStorage.clearTokens()
                                    null
                                }
                            } catch (e: Exception) {
                                logger.error(e) { "Failed to refresh tokens" }
                                null
                            }
                        } else {
                            null
                        }
                    }
                }
            }
        }
}
