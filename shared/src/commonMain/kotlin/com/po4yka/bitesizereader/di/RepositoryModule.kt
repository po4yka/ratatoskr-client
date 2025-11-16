package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.repository.*
import com.po4yka.bitesizereader.domain.repository.*
import org.koin.dsl.module

/**
 * Koin module for repository dependencies
 */
val repositoryModule =
    module {
        single<AuthRepository> {
            AuthRepositoryImpl(
                authApi = get(),
                secureStorage = get(),
            )
        }

        single<SummaryRepository> {
            SummaryRepositoryImpl(
                summariesApi = get(),
                database = get(),
                databaseHelper = get(),
            )
        }

        single<RequestRepository> {
            RequestRepositoryImpl(
                requestsApi = get(),
                database = get(),
                databaseHelper = get(),
            )
        }

        single<SearchRepository> {
            SearchRepositoryImpl(
                searchApi = get(),
                database = get(),
            )
        }

        single<SyncRepository> {
            SyncRepositoryImpl(
                syncApi = get(),
                databaseHelper = get(),
            )
        }
    }
