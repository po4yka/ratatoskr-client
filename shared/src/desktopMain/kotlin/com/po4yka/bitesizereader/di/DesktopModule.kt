package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.DesktopSecureStorage
import com.po4yka.bitesizereader.data.local.SecureStorage
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DesktopPlatformModule {
    @Single
    fun provideDatabaseDriverFactory(): DatabaseDriverFactory =
        DatabaseDriverFactory()

    @Single
    fun provideSecureStorage(): SecureStorage =
        DesktopSecureStorage()

    @Single
    fun provideHttpClientEngine(): HttpClientEngine = OkHttp.create()
}
