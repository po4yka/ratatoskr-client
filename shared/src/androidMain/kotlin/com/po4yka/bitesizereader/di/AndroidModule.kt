package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.AndroidSecureStorage
import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.util.share.AndroidShareManager
import com.po4yka.bitesizereader.util.share.ShareManager
import com.po4yka.bitesizereader.util.FileSaver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module =
    module {
        single { DatabaseDriverFactory(get()) }
        single<SecureStorage> { AndroidSecureStorage(get()) }
        single<HttpClientEngine> { OkHttp.create() }
        single<ShareManager> { AndroidShareManager(get()) }
        single { FileSaver(get()) }
    }
