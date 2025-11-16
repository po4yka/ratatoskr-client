package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.remote.TokenProvider
import com.po4yka.bitesizereader.data.remote.api.*
import com.po4yka.bitesizereader.data.remote.createHttpClient
import org.koin.dsl.module

/**
 * Koin module for network dependencies
 */
val networkModule =
    module {
        // Token provider
        single<TokenProvider> {
            object : TokenProvider {
                private val secureStorage: SecureStorage by lazy { get() }

                override suspend fun getTokens(): Pair<String, String>? {
                    val accessToken = secureStorage.getAccessToken()
                    val refreshToken = secureStorage.getRefreshToken()

                    return if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                        accessToken to refreshToken
                    } else {
                        null
                    }
                }

                override suspend fun refreshToken(refreshToken: String): Pair<String, String>? {
                    // Refresh logic handled by AuthRepository
                    return null
                }

                override suspend fun clearTokens() {
                    secureStorage.clearTokens()
                }
            }
        }

        // HTTP Client (platform-specific engine provided separately)
        single {
            createHttpClient(
                engine = get(),
                baseUrl = getProperty("api.base.url"),
                tokenProvider = get(),
                enableLogging = getProperty("api.logging.enabled", true),
            )
        }

        // API implementations
        single<AuthApi> { AuthApiImpl(get()) }
        single<SummariesApi> { SummariesApiImpl(get()) }
        single<RequestsApi> { RequestsApiImpl(get()) }
        single<SearchApi> { SearchApiImpl(get()) }
        single<SyncApi> { SyncApiImpl(get()) }
    }
