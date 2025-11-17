package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.util.share.ShareManager
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

/**
 * Desktop-specific Koin module for Compose Hot Reload development
 */
val desktopModule =
    module {
        // HTTP Client Engine (OkHttp for Desktop)
        single<HttpClientEngine> {
            OkHttp.create()
        }

        // Database driver factory (Desktop JVM)
        single { DatabaseDriverFactory() }

        // Secure storage (Desktop stub implementation)
        single<SecureStorage> {
            com.po4yka.bitesizereader.data.local.DesktopSecureStorage()
        }

        // Share manager (Desktop stub implementation)
        single<ShareManager> {
            com.po4yka.bitesizereader.util.share.DesktopShareManager()
        }

        // Coroutine scope for ViewModels
        single<CoroutineScope> {
            CoroutineScope(SupervisorJob() + Dispatchers.Main)
        }
    }
