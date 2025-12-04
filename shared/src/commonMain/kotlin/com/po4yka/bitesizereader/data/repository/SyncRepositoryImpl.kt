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
    private val api: SyncApi
) : SyncRepository {

    override suspend fun sync() {
        val metadata = database.databaseQueries.getSyncMetadata().executeAsOneOrNull()
        val token = metadata?.syncToken

        val response = api.sync(token)

        database.transaction {
            response.upsertedSummaries.forEach { dto ->
                database.databaseQueries.insertSummary(dto.toEntity())
            }
            response.deletedSummaryIds.forEach { id ->
                database.databaseQueries.deleteSummary(id)
            }
            
            database.databaseQueries.updateSyncMetadata(
                lastSyncTime = Clock.System.now(),
                syncToken = response.syncToken
            )
        }
    }

    override fun getSyncState(): Flow<SyncState> {
        return database.databaseQueries.getSyncMetadata()
            .asFlow().mapToOneOrNull(Dispatchers.IO).map { entity ->
                SyncState(
                    lastSyncTime = entity?.lastSyncTime,
                    lastSyncHash = entity?.syncToken
                )
            }
    }
}
