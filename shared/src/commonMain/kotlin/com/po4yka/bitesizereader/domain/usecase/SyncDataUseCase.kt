package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.SyncProgress
import com.po4yka.bitesizereader.domain.repository.SyncRepository
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Factory

@Factory
class SyncDataUseCase(private val repository: SyncRepository) {
    /**
     * Observable progress of current sync operation (null if no sync in progress).
     */
    val syncProgress: StateFlow<SyncProgress?> = repository.syncProgress

    /**
     * Sync data with the backend.
     *
     * @param forceFull If true, performs a full sync (downloads all data).
     *                  If false, performs a delta sync (only changes since last sync).
     */
    suspend operator fun invoke(forceFull: Boolean = false) {
        repository.sync(forceFull = forceFull)
    }

    /**
     * Cancel the current sync operation if one is in progress.
     */
    fun cancelSync() {
        repository.cancelSync()
    }
}
