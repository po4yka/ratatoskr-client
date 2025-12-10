package com.po4yka.bitesizereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.mappers.toEntity
import com.po4yka.bitesizereader.data.remote.SyncApi
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.SyncState
import com.po4yka.bitesizereader.domain.repository.SyncRepository
import com.po4yka.bitesizereader.util.error.AppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import org.koin.core.annotation.Single

@Single
class SyncRepositoryImpl(
    private val database: Database,
    private val api: SyncApi,
    private val secureStorage: SecureStorage,
) : SyncRepository {
    override suspend fun sync() {
        val sessionId = secureStorage.getSessionId()
        if (sessionId == null) {
            // If using secret login (debug), session ID might be missing.
            // Skip sync instead of throwing error to avoid infinite re-auth loop.
             println("SyncRepositoryImpl: Skipping sync: No session ID found")
            return
        }
        println("SyncRepositoryImpl: Starting sync with sessionId $sessionId")

        val metadata = database.databaseQueries.getSyncMetadata().executeAsOneOrNull()
        val sinceEpochSeconds = metadata?.lastSyncTime?.epochSeconds ?: 0L

        val response = api.sync(sessionId, sinceEpochSeconds)

        if (!response.success || response.data == null) {
            println("SyncRepositoryImpl: Sync failed or empty response: ${response.error}")
            return
        }

        val changes = response.data.changes
        val syncTimestamp = response.data.syncTimestamp

        println("SyncRepositoryImpl: Sync success. Created: ${changes.created.size}, Updated: ${changes.updated.size}, Deleted: ${changes.deleted.size}")

        database.transaction {
            changes.created.forEach { created ->
                val payload = created.data
                database.databaseQueries.insertSummary(
                    payload.toEntity(isReadOverride = payload.isRead, createdAt = created.createdAt),
                )
            }
            changes.updated.forEach { updated ->
                val payload = updated.data
                database.databaseQueries.insertSummary(
                    payload.toEntity(isReadOverride = payload.isRead, createdAt = updated.createdAt),
                )
            }
            changes.deleted.forEach { id ->
                database.databaseQueries.deleteSummary(id.toString())
            }

            database.databaseQueries.updateSyncMetadata(
                lastSyncTime = Clock.System.now(),
                syncToken = syncTimestamp,
            )
        }
    }

    override fun getSyncState(): Flow<SyncState> {
        return database.databaseQueries.getSyncMetadata()
            .asFlow().mapToOneOrNull(Dispatchers.IO).map { entity ->
                SyncState(
                    lastSyncTime = entity?.lastSyncTime,
                    lastSyncHash = entity?.syncToken,
                )
            }
    }
}
