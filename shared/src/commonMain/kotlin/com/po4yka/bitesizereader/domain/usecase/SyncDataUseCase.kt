package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.SyncState
import com.po4yka.bitesizereader.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for synchronizing data
 */
class SyncDataUseCase(
    private val syncRepository: SyncRepository,
) {
    operator fun invoke(forceFullSync: Boolean = false): Flow<SyncState> {
        return if (forceFullSync) {
            syncRepository.performFullSync()
        } else {
            syncRepository.performDeltaSync()
        }
    }
}
