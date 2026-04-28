package com.po4yka.ratatoskr.di

import com.po4yka.ratatoskr.data.local.SecureStorage
import com.po4yka.ratatoskr.data.remote.ApiClient
import com.po4yka.ratatoskr.util.config.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.po4yka.ratatoskr.data")
class NetworkModule {
    @Single
    fun provideHttpClient(
        engine: HttpClientEngine,
        secureStorage: SecureStorage,
    ): HttpClient {
        // Base URL should point to the API root (without /v1) because individual API calls already
        // include the "v1/..." prefix in their paths.
        val baseUrl = AppConfig.Api.baseUrl.trimEnd('/')
        return ApiClient(
            engine = engine,
            baseUrl = baseUrl,
            secureStorage = secureStorage,
        ).client
    }
}
