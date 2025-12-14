package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SyncRepository
import org.koin.core.annotation.Factory

@Factory
class SyncDataUseCase(private val repository: SyncRepository) {
    /**
     * Sync data with the backend.
     *
     * @param forceFull If true, performs a full sync (downloads all data).
     *                  If false, performs a delta sync (only changes since last sync).
     */
    suspend operator fun invoke(forceFull: Boolean = false) {
        repository.sync(forceFull = forceFull)
    }
}
