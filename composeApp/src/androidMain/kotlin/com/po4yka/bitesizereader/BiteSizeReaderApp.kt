package com.po4yka.bitesizereader

import android.app.Application
import com.po4yka.bitesizereader.di.*
import com.po4yka.bitesizereader.util.config.AppConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Application class for initializing dependencies
 */
class BiteSizeReaderApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        startKoin {
            androidContext(this@BiteSizeReaderApp)
            modules(
                androidModule,
                networkModule,
                databaseModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
            properties(
                mapOf(
                    "api.base.url" to AppConfig.Api.baseUrl,
                    "api.logging.enabled" to AppConfig.Api.loggingEnabled.toString(),
                    "telegram.bot.username" to AppConfig.Telegram.botUsername,
                    "telegram.bot.id" to AppConfig.Telegram.botId
                )
            )
        }
    }
}
