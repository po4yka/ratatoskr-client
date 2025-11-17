package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.remote.TokenProvider
import com.po4yka.bitesizereader.data.remote.api.*
import com.po4yka.bitesizereader.data.remote.createHttpClient
import org.koin.dsl.module

/**
 * Koin module for network dependencies
 *
 * Uses lazy initialization (createdAtStart = false) to defer HTTP client and API creation
 * until network requests are needed, improving startup performance.
 */
val networkModule =
    module {
        // Lazy singleton - Token provider
        single<TokenProvider>(createdAtStart = false) {
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

        // Lazy singleton - HTTP Client (platform-specific engine provided separately)
        single(createdAtStart = false) {
            createHttpClient(
                engine = get(),
                baseUrl = getProperty("api.base.url"),
                tokenProvider = get(),
                enableLogging = getProperty("api.logging.enabled", true),
            )
        }

        // Lazy singletons - API implementations
        single<AuthApi>(createdAtStart = false) { AuthApiImpl(get()) }
        single<SummariesApi>(createdAtStart = false) { SummariesApiImpl(get()) }
        single<RequestsApi>(createdAtStart = false) { RequestsApiImpl(get()) }
        single<SearchApi>(createdAtStart = false) { SearchApiImpl(get()) }
        single<SyncApi>(createdAtStart = false) { SyncApiImpl(get()) }
    }
