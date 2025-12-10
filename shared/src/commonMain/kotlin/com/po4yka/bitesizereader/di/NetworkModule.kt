package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.remote.ApiClient
import com.po4yka.bitesizereader.util.config.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.po4yka.bitesizereader.data.remote")
class NetworkModule {
    @Single
    fun provideHttpClient(
        engine: HttpClientEngine,
        secureStorage: SecureStorage,
    ): HttpClient {
        val baseUrl = AppConfig.Api.baseUrl.trimEnd('/') + "/v1"
        return ApiClient(
            engine = engine,
            baseUrl = baseUrl,
            secureStorage = secureStorage,
        ).client
    }
}
