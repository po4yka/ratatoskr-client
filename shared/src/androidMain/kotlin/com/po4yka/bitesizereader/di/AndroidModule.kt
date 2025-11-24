package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.local.SecureStorageImpl
import com.po4yka.bitesizereader.util.share.AndroidShareManager
import com.po4yka.bitesizereader.util.share.ShareManager
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin module
 */
val androidModule =
    module {
        // HTTP Client Engine (OkHttp for Android)
        single<HttpClientEngine> {
            OkHttp.create()
        }

        // Database driver factory
        single { DatabaseDriverFactory(androidContext()) }

        // Secure storage
        single<SecureStorage> {
            SecureStorageImpl(androidContext())
        }

        // Share manager
        single<ShareManager> {
            AndroidShareManager(androidContext())
        }
    }
