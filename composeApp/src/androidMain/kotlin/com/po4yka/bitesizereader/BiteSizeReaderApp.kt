package com.po4yka.bitesizereader

import android.app.Application
import com.po4yka.bitesizereader.di.imageLoaderModule
import com.po4yka.bitesizereader.di.initKoin
import com.po4yka.bitesizereader.worker.WorkManagerInitializer
import org.koin.android.ext.koin.androidContext

/**
 * Application class for initializing dependencies and background work
 */
class BiteSizeReaderApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin with annotation-based modules and extra DSL modules
        initKoin(
            appDeclaration = {
                androidContext(this@BiteSizeReaderApp)
            },
            extraModules = listOf(imageLoaderModule),
        )

        // Schedule periodic background sync
        WorkManagerInitializer.schedulePeriodicSync(this)
    }
}
