package com.po4yka.bitesizereader

import android.app.Application
import com.po4yka.bitesizereader.di.*
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
                    "api.base.url" to "https://api.bitesizereader.example.com", // TODO: Update with actual API URL
                    "api.logging.enabled" to "true"
                )
            )
        }
    }
}
