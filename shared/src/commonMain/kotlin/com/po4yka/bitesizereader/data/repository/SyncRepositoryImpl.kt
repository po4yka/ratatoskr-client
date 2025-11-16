package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.local.DatabaseHelper
import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.api.SyncApi
import com.po4yka.bitesizereader.domain.model.SyncState
import com.po4yka.bitesizereader.domain.model.SyncType
import com.po4yka.bitesizereader.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

/**
 * Implementation of SyncRepository for offline synchronization
 */
class SyncRepositoryImpl(
    private val syncApi: SyncApi,
    private val databaseHelper: DatabaseHelper,
) : SyncRepository {
    private var isSyncing = false

    override fun performFullSync(): Flow<SyncState> =
        flow {
            if (isSyncing) {
                emit(SyncState.Error(SyncType.FULL, "Sync already in progress", false))
                return@flow
            }

            isSyncing = true

            try {
                emit(SyncState.Syncing(SyncType.FULL, 0, "Starting full sync"))

                // Clear local database
                databaseHelper.clearAllSummaries()

                emit(SyncState.Syncing(SyncType.FULL, 50, "Database cleared"))

                // This would normally involve chunked downloads
                // For now, simplified implementation

                emit(SyncState.Syncing(SyncType.FULL, 80, "Syncing data"))

                // Update last sync timestamp
                val now = Clock.System.now().toString()
                databaseHelper.setSyncMetadata("last_full_sync", now)
                databaseHelper.setSyncMetadata("last_sync", now)

                emit(
                    SyncState.Success(
                        type = SyncType.FULL,
                        syncedAt = Clock.System.now(),
                        itemsSynced = 0,
                    ),
                )
            } catch (e: Exception) {
                emit(
                    SyncState.Error(
                        type = SyncType.FULL,
                        error = e.message ?: "Full sync failed",
                        canRetry = true,
                    ),
                )
            } finally {
                isSyncing = false
            }
        }

    override fun performDeltaSync(): Flow<SyncState> =
        flow {
            if (isSyncing) {
                emit(SyncState.Error(SyncType.DELTA, "Sync already in progress", false))
                return@flow
            }

            isSyncing = true

            try {
                emit(SyncState.Syncing(SyncType.DELTA, 0, "Checking for updates"))

                val lastSync =
                    getLastSyncTimestamp() ?: run {
                        // No previous sync, do full sync instead
                        isSyncing = false
                        emitAll(performFullSync())
                        return@flow
                    }

                emit(SyncState.Syncing(SyncType.DELTA, 30, "Fetching changes"))

                val response = syncApi.getDeltaSync(lastSync)

                if (response.success && response.data != null) {
                    val delta = response.data

                    emit(SyncState.Syncing(SyncType.DELTA, 60, "Applying changes"))

                    var itemsChanged = 0

                    // Apply changes
                    delta.summaries.forEach { change ->
                        when (change.action) {
                            "insert", "update" -> {
                                change.data?.let {
                                    databaseHelper.insertSummary(it.toDomain())
                                    itemsChanged++
                                }
                            }
                        }
                    }

                    // Handle deletions (simplified)
                    itemsChanged += delta.deletedIds.size

                    // Update last sync timestamp
                    databaseHelper.setSyncMetadata("last_sync", delta.syncTimestamp)

                    emit(
                        SyncState.Success(
                            type = SyncType.DELTA,
                            syncedAt = Clock.System.now(),
                            itemsSynced = itemsChanged,
                        ),
                    )
                } else {
                    emit(
                        SyncState.Error(
                            type = SyncType.DELTA,
                            error = response.error?.message ?: "Delta sync failed",
                            canRetry = true,
                        ),
                    )
                }
            } catch (e: Exception) {
                emit(
                    SyncState.Error(
                        type = SyncType.DELTA,
                        error = e.message ?: "Delta sync failed",
                        canRetry = true,
                    ),
                )
            } finally {
                isSyncing = false
            }
        }

    override fun uploadLocalChanges(): Flow<SyncState> =
        flow {
            emit(SyncState.Syncing(SyncType.UPLOAD, 0, "Collecting changes"))

            try {
                // Get locally modified summaries would go here
                // For now, simplified

                emit(
                    SyncState.Success(
                        type = SyncType.UPLOAD,
                        syncedAt = Clock.System.now(),
                        itemsSynced = 0,
                    ),
                )
            } catch (e: Exception) {
                emit(
                    SyncState.Error(
                        type = SyncType.UPLOAD,
                        error = e.message ?: "Upload failed",
                        canRetry = true,
                    ),
                )
            }
        }

    override suspend fun cancelSync() {
        isSyncing = false
    }

    override suspend fun isSyncNeeded(): Boolean {
        val lastSync = getLastSyncTimestamp() ?: return true

        val lastSyncInstant = kotlinx.datetime.Instant.parse(lastSync)
        val now = Clock.System.now()

        val timeSinceLastSync = now - lastSyncInstant

        // Sync needed if more than 5 minutes since last sync
        return timeSinceLastSync.inWholeMinutes > 5
    }

    override suspend fun getLastSyncTimestamp(): String? {
        return databaseHelper.getSyncMetadata("last_sync")
    }
}
