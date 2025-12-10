package com.po4yka.bitesizereader

import android.app.Application
import com.po4yka.bitesizereader.di.PlatformConfiguration
import com.po4yka.bitesizereader.di.imageLoaderModule
import com.po4yka.bitesizereader.di.setupKoin
import com.po4yka.bitesizereader.worker.WorkManagerInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

/** Application class for initializing dependencies and background work */
@OptIn(KoinExperimentalAPI::class)
class BiteSizeReaderApp : Application(), KoinStartup {
    override fun onKoinStartup(): KoinConfiguration =
        KoinConfiguration {
            setupKoin(
                configuration = PlatformConfiguration(),
                extraModules = listOf(imageLoaderModule),
            )
            androidContext(this@BiteSizeReaderApp)
        }

    override fun onCreate() {
        super.onCreate()

        // Schedule periodic background sync
        WorkManagerInitializer.schedulePeriodicSync(this)
    }
}
