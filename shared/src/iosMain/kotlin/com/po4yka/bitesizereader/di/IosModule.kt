package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.local.SecureStorageImpl
import com.po4yka.bitesizereader.util.share.IosShareManager
import com.po4yka.bitesizereader.util.share.ShareManager
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

/**
 * iOS-specific Koin module
 */
val iosModule =
    module {
        // HTTP Client Engine (Darwin for iOS)
        single<HttpClientEngine> {
            Darwin.create()
        }

        // Database driver factory
        single { DatabaseDriverFactory() }

        // Secure storage
        single<SecureStorage> {
            SecureStorageImpl()
        }

        // Share manager
        single<ShareManager> {
            IosShareManager()
        }

        // Coroutine scope for ViewModels
        single<CoroutineScope> {
            CoroutineScope(SupervisorJob() + Dispatchers.Main)
        }
    }
