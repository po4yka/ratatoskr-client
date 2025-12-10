package com.po4yka.bitesizereader.di

import android.content.Context
import com.po4yka.bitesizereader.data.local.AndroidSecureStorage
import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.util.FileSaver
import com.po4yka.bitesizereader.util.share.AndroidShareManager
import com.po4yka.bitesizereader.util.share.ShareManager
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class AndroidPlatformModule {
    @Single
    fun provideDatabaseDriverFactory(context: Context): DatabaseDriverFactory =
        DatabaseDriverFactory(context)

    @Single
    fun provideSecureStorage(context: Context): SecureStorage =
        AndroidSecureStorage(context)

    @Single
    fun provideHttpClientEngine(): HttpClientEngine = OkHttp.create()

    @Single
    fun provideShareManager(context: Context): ShareManager =
        AndroidShareManager(context)

    @Single
    fun provideFileSaver(context: Context): FileSaver =
        FileSaver(context)

    @Single
    fun providePlatform(): com.po4yka.bitesizereader.Platform =
        com.po4yka.bitesizereader.AndroidPlatform()
}
