package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.local.SecureStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient(
    engine: io.ktor.client.engine.HttpClientEngine,
    private val baseUrl: String,
    private val secureStorage: SecureStorage
) {
    val client = HttpClient(engine) {
        expectSuccess = true
        
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }

        install(DefaultRequest) {
            url(this@ApiClient.baseUrl)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
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
                            // Manually create a temporary client to avoid infinite recursion if using the main client
                            // In a real Koin setup, we might inject AuthApi, but here we might need to construct it or use a separate client instance.
                            // Since AuthApi is not passed here, we'll construct a simple request.
                            // Or ideally, pass a provider for refreshing.
                            // For simplicity in this generated code:
                            
                            // WARNING: This block needs proper implementation with a dedicated AuthApi instance or similar mechanism.
                            // Below is a placeholder logic that assumes an injected refresh mechanism or manual request.
                            
                            // val response = client.post("${this@ApiClient.baseUrl}/auth/refresh") {
                            //     setBody(mapOf("refresh_token" to refreshToken))
                            // }.body<AuthResponseDto>()
                            
                            // For now, return null to force re-login until full refresh flow is wired.
                            // Implementing this fully requires breaking circular dependency if AuthApi depends on this Client.
                            // Typically we use a separate unauthenticated client for refresh.
                            null 
                        } catch (e: Exception) {
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