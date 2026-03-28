package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.repository.SyncRepositoryImpl
import com.po4yka.bitesizereader.domain.repository.SyncRepository
import com.po4yka.bitesizereader.sync.PendingOperationHandler
import com.po4yka.bitesizereader.sync.SyncItemApplier
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
