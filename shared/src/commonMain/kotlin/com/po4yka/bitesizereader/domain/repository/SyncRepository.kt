package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.SyncState
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Sync operations
 */
interface SyncRepository {
    /**
     * Perform full synchronization
     */
    fun performFullSync(): Flow<SyncState>

    /**
     * Perform delta synchronization
     */
    fun performDeltaSync(): Flow<SyncState>

    /**
     * Upload local changes to server
     */
    fun uploadLocalChanges(): Flow<SyncState>

    /**
     * Cancel ongoing sync operation
     */
    suspend fun cancelSync()

    /**
     * Check if sync is needed
     */
    suspend fun isSyncNeeded(): Boolean

    /**
     * Get last sync timestamp
     */
    suspend fun getLastSyncTimestamp(): String?
}
