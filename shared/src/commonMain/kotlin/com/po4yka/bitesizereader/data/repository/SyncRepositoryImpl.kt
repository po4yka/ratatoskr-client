package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toEntity
import com.po4yka.bitesizereader.data.remote.SyncApi
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.SyncState
import com.po4yka.bitesizereader.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.time.Clock

class SyncRepositoryImpl(
    private val database: Database,
    private val api: SyncApi,
) : SyncRepository {
    override suspend fun sync() {
        val metadata = database.databaseQueries.getSyncMetadata().executeAsOneOrNull()
        val since = metadata?.lastSyncTime?.toString() ?: "1970-01-01T00:00:00Z"

        val response = api.sync(since)

        if (!response.success || response.data == null) return

        val changes = response.data.changes
        val syncTimestamp = response.data.syncTimestamp

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
