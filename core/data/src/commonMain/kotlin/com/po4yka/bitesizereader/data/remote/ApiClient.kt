package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.util.config.AppConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.ResponseException
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
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

private const val REDACTED_LOG_VALUE = "<redacted>"
private const val REDACTED_QUERY_LOG_VALUE = "<query-redacted>"
private const val SENSITIVE_ENDPOINT_BODY_MESSAGE = "<body suppressed for sensitive endpoint>"

private val sensitiveBodyFieldNames =
    listOf(
        "access[_-]?token",
        "refresh[_-]?token",
        "id[_-]?token",
        "token",
        "password",
        "passwd",
        "secret",
        "api[_-]?key",
        "apikey",
        "authorization",
        "cookie",
        "session[_-]?id",
        "client[_-]?secret",
    )

private val sensitiveBodyFieldRegex =
    Regex(
        pattern =
            """(?i)(["']?(?:${sensitiveBodyFieldNames.joinToString("|")})["']?\s*[:=]\s*)""" +
                """(["'][^"']*["']|[^,\s}\]]+)""",
    )

private val bearerTokenRegex = Regex("""(?i)\bBearer\s+[A-Za-z0-9._~+/\-=]+""")

private val sensitiveEndpointPathRegex =
    Regex("""(?i)(^|[/_-])(auth|login|token|secret|password|credential|db[-_]dump)([/_.-]|$)""")

internal fun sanitizedRequestTargetForLog(url: Url): String {
    val querySuffix =
        if (url.parameters.isEmpty()) {
            ""
        } else {
            "?$REDACTED_QUERY_LOG_VALUE"
        }
    return "${url.encodedPath}$querySuffix"
}

internal fun shouldSuppressErrorBodyForLog(encodedPath: String): Boolean =
    sensitiveEndpointPathRegex.containsMatchIn(encodedPath)

internal fun shouldClearTokensAfterRefreshFailure(status: HttpStatusCode): Boolean =
    status == HttpStatusCode.BadRequest ||
        status == HttpStatusCode.Unauthorized ||
        status == HttpStatusCode.Forbidden

internal fun redactSensitiveBodyForLog(body: String): String {
    val redactedFields =
        sensitiveBodyFieldRegex.replace(body) { match ->
            val prefix = match.groupValues[1]
            val value = match.groupValues[2]
            val redactedValue =
                when {
                    value.startsWith("\"") -> "\"$REDACTED_LOG_VALUE\""
                    value.startsWith("'") -> "'$REDACTED_LOG_VALUE'"
                    else -> REDACTED_LOG_VALUE
                }
            "$prefix$redactedValue"
        }

    return bearerTokenRegex.replace(redactedFields, "Bearer $REDACTED_LOG_VALUE")
}

class ApiClient(
    engine: io.ktor.client.engine.HttpClientEngine,
    private val baseUrl: String,
    private val secureStorage: SecureStorage,
) {
    private val tokenRefreshMutex = Mutex()

    val client =
        HttpClient(engine) {
            expectSuccess = true

            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = false
                        isLenient = false
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
                requestTimeoutMillis = AppConfig.Api.REQUEST_TIMEOUT_MS
                connectTimeoutMillis = AppConfig.Api.CONNECT_TIMEOUT_MS
                socketTimeoutMillis = AppConfig.Api.REQUEST_TIMEOUT_MS
            }

            if (AppConfig.Api.loggingEnabled) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                    sanitizeHeader { header -> header == HttpHeaders.Authorization }
                    filter { request ->
                        val path = request.url.encodedPath
                        !shouldSuppressErrorBodyForLog(path)
                    }
                }
            }

            install(DefaultRequest) {
                url(this@ApiClient.baseUrl)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
            }

            install(HttpCallValidator) {
                handleResponseExceptionWithRequest { cause: Throwable, request: io.ktor.client.request.HttpRequest ->
                    val requestTarget = sanitizedRequestTargetForLog(request.url)

                    // Don't log cancellation exceptions as errors - they're expected during navigation
                    if (cause is kotlin.coroutines.cancellation.CancellationException) {
                        logger.debug {
                            "Request cancelled: ${request.method.value} $requestTarget"
                        }
                        return@handleResponseExceptionWithRequest
                    }

                    val response = (cause as? ResponseException)?.response
                    val bodySnippet =
                        when {
                            response == null -> "<no response body>"
                            shouldSuppressErrorBodyForLog(request.url.encodedPath) ->
                                SENSITIVE_ENDPOINT_BODY_MESSAGE
                            else ->
                                runCatching { redactSensitiveBodyForLog(response.bodyAsText().take(2_000)) }
                                    .getOrElse { "<body unavailable: ${it.message}>" }
                        }
                    val statusPart = response?.status?.toString() ?: "<no status>"
                    val statusCode = response?.status?.value ?: 0

                    val message =
                        """
                        HTTP error while handling ${request.method.value} $requestTarget
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
                        tokenRefreshMutex.withLock {
                            // After acquiring the lock, check if another coroutine already refreshed
                            val currentAccessToken = secureStorage.getAccessToken()
                            if (currentAccessToken != null && currentAccessToken != oldTokens?.accessToken) {
                                // Another coroutine already refreshed the tokens
                                val currentRefreshToken = secureStorage.getRefreshToken()
                                if (currentRefreshToken != null) {
                                    return@withLock BearerTokens(currentAccessToken, currentRefreshToken)
                                }
                            }

                            val refreshToken = secureStorage.getRefreshToken()
                            if (refreshToken != null) {
                                try {
                                    val response =
                                        client.post("v1/auth/refresh") {
                                            setBody(mapOf("refresh_token" to refreshToken))
                                        }
                                    val responseText = response.bodyAsText()
                                    val parsed: ApiResponseDto<ApiTokenRefreshResponseDto> =
                                        Json.decodeFromString(responseText)

                                    if (parsed.success && parsed.data != null) {
                                        val refreshedAccessToken = parsed.data.tokens.accessToken
                                        val refreshedRefreshToken = parsed.data.tokens.refreshToken ?: refreshToken
                                        secureStorage.saveAccessToken(refreshedAccessToken)
                                        secureStorage.saveRefreshToken(refreshedRefreshToken)
                                        BearerTokens(refreshedAccessToken, refreshedRefreshToken)
                                    } else {
                                        logger.error {
                                            "Token refresh failed (success=${parsed.success})"
                                        }
                                        secureStorage.clearTokens()
                                        null
                                    }
                                } catch (e: kotlin.coroutines.cancellation.CancellationException) {
                                    throw e
                                } catch (e: ResponseException) {
                                    if (shouldClearTokensAfterRefreshFailure(e.response.status)) {
                                        logger.warn(e) {
                                            "Refresh token rejected with ${e.response.status}; clearing stored tokens"
                                        }
                                        secureStorage.clearTokens()
                                    } else {
                                        logger.warn(e) {
                                            "Token refresh failed with ${e.response.status}; " +
                                                "preserving tokens for retry"
                                        }
                                    }
                                    null
                                } catch (e: Exception) {
                                    logger.warn(e) { "Failed to refresh tokens; preserving tokens for retry" }
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
}

@Serializable
private data class ApiTokenRefreshResponseDto(
    @SerialName("tokens") val tokens: ApiRefreshedTokensDto,
)

@Serializable
private data class ApiRefreshedTokensDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String? = null,
)
