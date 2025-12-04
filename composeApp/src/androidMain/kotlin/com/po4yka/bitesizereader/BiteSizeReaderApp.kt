package com.po4yka.bitesizereader

import android.app.Application
import com.po4yka.bitesizereader.di.initKoin
import org.koin.android.ext.koin.androidContext

/**
 * Application class for initializing dependencies and background work
 */
class BiteSizeReaderApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        initKoin {
            androidContext(this@BiteSizeReaderApp)
            // properties(...) if needed
        }

        // Schedule periodic background sync
        // WorkManagerInitializer.schedulePeriodicSync(this) // Commented out as I haven't implemented WorkManagerInitializer yet
    }
}
