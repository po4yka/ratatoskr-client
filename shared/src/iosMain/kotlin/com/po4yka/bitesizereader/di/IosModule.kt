package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.IosSecureStorage
import com.po4yka.bitesizereader.data.local.SecureStorage
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DatabaseDriverFactory() }
    single<SecureStorage> { IosSecureStorage() }
    single<HttpClientEngine> { Darwin.create() }
}