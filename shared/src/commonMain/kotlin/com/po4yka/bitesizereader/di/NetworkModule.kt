package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.remote.ApiClient
import com.po4yka.bitesizereader.data.remote.AuthApi
import com.po4yka.bitesizereader.data.remote.KtorAuthApi
import com.po4yka.bitesizereader.data.remote.KtorRequestsApi
import com.po4yka.bitesizereader.data.remote.KtorSearchApi
import com.po4yka.bitesizereader.data.remote.KtorSummariesApi
import com.po4yka.bitesizereader.data.remote.KtorSyncApi
import com.po4yka.bitesizereader.data.remote.KtorSystemApi
import com.po4yka.bitesizereader.data.remote.KtorUserApi
import com.po4yka.bitesizereader.data.remote.RequestsApi
import com.po4yka.bitesizereader.data.remote.SearchApi
import com.po4yka.bitesizereader.data.remote.SummariesApi
import com.po4yka.bitesizereader.data.remote.SyncApi
import com.po4yka.bitesizereader.data.remote.SystemApi
import com.po4yka.bitesizereader.data.remote.UserApi
import com.po4yka.bitesizereader.util.config.AppConfig
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single {
        // Append /v1 so relative paths map to the Mobile API routes exposed by the backend
        val baseUrl = AppConfig.Api.baseUrl.trimEnd('/') + "/v1"
        ApiClient(
            engine = get(),
            baseUrl = baseUrl,
            secureStorage = get()
        ).client
    }

    single<AuthApi> { KtorAuthApi(get()) }
    single<UserApi> { KtorUserApi(get()) }
    singleOf(::KtorSummariesApi) bind SummariesApi::class
    singleOf(::KtorRequestsApi) bind RequestsApi::class
    singleOf(::KtorSearchApi) bind SearchApi::class
    singleOf(::KtorSyncApi) bind SyncApi::class
    singleOf(::KtorSystemApi) bind SystemApi::class
}
