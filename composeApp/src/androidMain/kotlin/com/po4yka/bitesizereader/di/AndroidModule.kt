package com.po4yka.bitesizereader.di

import android.content.Context
import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.SecureStorage
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin module
 */
val androidModule = module {
    // HTTP Client Engine (OkHttp for Android)
    single<HttpClientEngine> {
        OkHttp.create()
    }

    // Database driver factory
    single { DatabaseDriverFactory(androidContext()) }

    // Secure storage
    single<SecureStorage> {
        com.po4yka.bitesizereader.data.local.SecureStorageImpl(androidContext())
    }

    // Coroutine scope for ViewModels
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }
}
