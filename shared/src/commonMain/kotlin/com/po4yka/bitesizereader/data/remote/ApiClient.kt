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
                    // TODO: Implement actual token refresh logic calling AuthApi
                    // For now, if unauthorized, we might need to logout or depend on AuthRepository to handle it
                    null
                }
            }
        }
    }
}