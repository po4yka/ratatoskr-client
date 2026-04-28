package com.po4yka.ratatoskr.di

import com.po4yka.ratatoskr.data.repository.SyncRepositoryImpl
import com.po4yka.ratatoskr.feature.sync.api.PendingOperationHandler
import com.po4yka.ratatoskr.feature.sync.api.SyncItemApplier
import com.po4yka.ratatoskr.feature.sync.domain.repository.SyncRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val syncFeatureBindingsModule =
    module {
        single {
            val koin = getKoin()
            SyncRepositoryImpl(
                database = get(),
                api = get(),
                networkMonitor = get(),
                syncItemAppliers = koin.getAll<SyncItemApplier>(),
                pendingOperationHandlers = koin.getAll<PendingOperationHandler>(),
            )
        } bind SyncRepository::class
    }
