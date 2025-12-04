package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.remote.ApiClient
import com.po4yka.bitesizereader.data.remote.AuthApi
import com.po4yka.bitesizereader.data.remote.KtorAuthApi
import com.po4yka.bitesizereader.data.remote.KtorRequestsApi
import com.po4yka.bitesizereader.data.remote.KtorSearchApi
import com.po4yka.bitesizereader.data.remote.KtorSummariesApi
import com.po4yka.bitesizereader.data.remote.KtorSyncApi
import com.po4yka.bitesizereader.data.remote.RequestsApi
import com.po4yka.bitesizereader.data.remote.SearchApi
import com.po4yka.bitesizereader.data.remote.SummariesApi
import com.po4yka.bitesizereader.data.remote.SyncApi
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single {
        ApiClient(
            engine = get(),
            baseUrl = "http://10.0.2.2:8000", // TODO: Use Build Config
            secureStorage = get()
        ).client
    }

    singleOf(::KtorAuthApi) bind AuthApi::class
    singleOf(::KtorSummariesApi) bind SummariesApi::class
    singleOf(::KtorRequestsApi) bind RequestsApi::class
    singleOf(::KtorSearchApi) bind SearchApi::class
    singleOf(::KtorSyncApi) bind SyncApi::class
}