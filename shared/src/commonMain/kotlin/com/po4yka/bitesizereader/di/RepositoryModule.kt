package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.repository.*
import com.po4yka.bitesizereader.domain.repository.*
import org.koin.dsl.module

/**
 * Koin module for repository dependencies
 *
 * Uses lazy initialization (createdAtStart = false) to defer repository creation
 * until they're actually needed, improving startup performance.
 */
val repositoryModule =
    module {
        // Lazy singleton - created only when first accessed
        single<AuthRepository>(createdAtStart = false) {
            AuthRepositoryImpl(
                authApi = get(),
                secureStorage = get(),
            )
        }

        // Lazy singleton - created only when first accessed
        single<SummaryRepository>(createdAtStart = false) {
            SummaryRepositoryImpl(
                summariesApi = get(),
                database = get(),
                databaseHelper = get(),
            )
        }

        // Lazy singleton - created only when first accessed
        single<RequestRepository>(createdAtStart = false) {
            RequestRepositoryImpl(
                requestsApi = get(),
                database = get(),
                databaseHelper = get(),
            )
        }

        // Lazy singleton - created only when first accessed
        single<SearchRepository>(createdAtStart = false) {
            SearchRepositoryImpl(
                searchApi = get(),
                database = get(),
            )
        }

        // Lazy singleton - created only when first accessed
        single<SyncRepository>(createdAtStart = false) {
            SyncRepositoryImpl(
                syncApi = get(),
                databaseHelper = get(),
            )
        }
    }
