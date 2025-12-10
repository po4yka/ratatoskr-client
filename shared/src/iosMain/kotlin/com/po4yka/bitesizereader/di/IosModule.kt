package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.IosSecureStorage
import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.util.FileSaver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class IosPlatformModule {
    @Single
    fun provideDatabaseDriverFactory(): DatabaseDriverFactory =
        DatabaseDriverFactory()

    @Single
    fun provideSecureStorage(): SecureStorage =
        IosSecureStorage()

    @Single
    fun provideHttpClientEngine(): HttpClientEngine = Darwin.create()

    @Single
    fun provideFileSaver(): FileSaver =
        FileSaver()

    @Single
    fun providePlatform(): com.po4yka.bitesizereader.Platform =
        com.po4yka.bitesizereader.IOSPlatform()
}
