package com.po4yka.bitesizereader.di

import android.content.Context
import com.po4yka.bitesizereader.data.local.AndroidSecureStorage
import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.util.FileSaver
import com.po4yka.bitesizereader.util.network.AndroidNetworkMonitor
import com.po4yka.bitesizereader.util.network.NetworkMonitor
import com.po4yka.bitesizereader.util.share.AndroidShareManager
import com.po4yka.bitesizereader.util.share.ShareManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class AndroidModule {
    @Single
    fun provideDatabaseDriverFactory(context: Context): DatabaseDriverFactory = DatabaseDriverFactory(context)

    @Single
    fun provideSecureStorage(context: Context): SecureStorage = AndroidSecureStorage(context)

    @Single
    fun provideHttpClientEngine(): HttpClientEngine = OkHttp.create()

    @Single
    fun provideShareManager(context: Context): ShareManager = AndroidShareManager(context)

    @Single
    fun provideNetworkMonitor(context: Context): NetworkMonitor = AndroidNetworkMonitor(context)

    @Single
    fun provideFileSaver(context: Context): FileSaver = FileSaver(context)

    @Single
    fun provideObservableSettings(context: Context): ObservableSettings {
        val sharedPrefs = context.getSharedPreferences("reading_preferences", Context.MODE_PRIVATE)
        return SharedPreferencesSettings(sharedPrefs)
    }

    @Single
    fun providePlatform(): com.po4yka.bitesizereader.Platform = com.po4yka.bitesizereader.AndroidPlatform()

    @Single
    fun provideAudioPlayer(): com.po4yka.bitesizereader.util.audio.AudioPlayer =
        com.po4yka.bitesizereader.util.audio.AudioPlayer()
}
