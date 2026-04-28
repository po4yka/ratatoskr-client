package com.po4yka.ratatoskr

import android.app.Application
import com.po4yka.ratatoskr.di.PlatformConfiguration
import com.po4yka.ratatoskr.di.appModules
import com.po4yka.ratatoskr.di.imageLoaderModule
import com.po4yka.ratatoskr.di.setupKoin
import com.po4yka.ratatoskr.util.config.AppConfig
import com.po4yka.ratatoskr.worker.WorkManagerInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

/** Application class for initializing dependencies and background work */
@OptIn(KoinExperimentalAPI::class)
class RatatoskrApp : Application(), KoinStartup {
    override fun onKoinStartup(): KoinConfiguration =
        KoinConfiguration {
            initializeAppConfig()
            setupKoin(
                configuration = PlatformConfiguration(),
                modules = appModules(),
                extraModules = listOf(imageLoaderModule),
            )
            androidContext(this@RatatoskrApp)
        }

    override fun onCreate() {
        super.onCreate()

        // Schedule periodic background sync
        WorkManagerInitializer.schedulePeriodicSync(this)
    }

    private fun initializeAppConfig() {
        AppConfig.initializeFromProperties(
            mapOf(
                "api.base.url" to BuildConfig.API_BASE_URL,
                "api.logging.enabled" to BuildConfig.API_LOGGING_ENABLED,
                "client.id" to BuildConfig.CLIENT_ID,
            ),
        )
    }
}
