package com.po4yka.bitesizereader.data.remote

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

/**
 * Token provider interface for authentication
 */
interface TokenProvider {
    suspend fun getTokens(): Pair<String, String>? // (accessToken, refreshToken)

    suspend fun refreshToken(refreshToken: String): Pair<String, String>? // (newAccessToken, newRefreshToken)

    suspend fun clearTokens()
}

/**
 * Creates and configures the Ktor HttpClient for API communication
 */
fun createHttpClient(
    engine: HttpClientEngine,
    baseUrl: String,
    tokenProvider: TokenProvider,
    enableLogging: Boolean = false,
): HttpClient {
    return HttpClient(engine) {
        // Base URL configuration
        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
        }

        // JSON serialization
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                    prettyPrint = false
                    explicitNulls = false
                },
            )
        }

        // Authentication with JWT
        install(Auth) {
            bearer {
                loadTokens {
                    val tokens = tokenProvider.getTokens()
                    tokens?.let {
                        BearerTokens(
                            accessToken = it.first,
                            refreshToken = it.second,
                        )
                    }
                }

                refreshTokens {
                    val oldRefreshToken = oldTokens?.refreshToken
                    if (oldRefreshToken != null) {
                        val newTokens = tokenProvider.refreshToken(oldRefreshToken)
                        newTokens?.let {
                            BearerTokens(
                                accessToken = it.first,
                                refreshToken = it.second,
                            )
                        }
                    } else {
                        null
                    }
                }

                sendWithoutRequest { request ->
                    // Send authorization header for all requests except auth endpoints
                    !request.url.encodedPath.contains("/auth/telegram-login")
                }
            }
        }

        // Logging (conditional)
        if (enableLogging) {
            install(Logging) {
                logger =
                    object : io.ktor.client.plugins.logging.Logger {
                        override fun log(message: String) {
                            com.po4yka.bitesizereader.data.remote.logger.debug { "HTTP: $message" }
                        }
                    }
                level = LogLevel.INFO
            }
        }

        // Timeout configuration
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 30_000
        }

        // Content encoding (gzip, deflate)
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 2)
            exponentialDelay()
        }
    }
}
