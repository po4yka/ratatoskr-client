package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.SummariesApi
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.Single

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

@Single
class SummaryRepositoryImpl(
    private val database: Database,
    private val api: SummariesApi,
) : SummaryRepository {
    override fun getSummaries(
        page: Int,
        pageSize: Int,
        tags: List<String>?,
    ): Flow<List<Summary>> {
        // Current implementation: Direct database access with reactive Flow.
        // Data synchronization is handled separately by SyncDataUseCase which fetches
        // from the API and updates the local database. This repository observes
        // database changes and emits updates automatically via SQLDelight's asFlow().

        return database.databaseQueries.selectAllSummaries(
            limit = pageSize.toLong(),
            offset = ((page - 1) * pageSize).toLong(),
        ).asFlow().mapToList(Dispatchers.IO).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSummaryById(id: String): Summary? {
        return database.databaseQueries.getSummaryById(id)
            .executeAsOneOrNull()?.toDomain()
    }

    override suspend fun markAsRead(id: String) {
        database.databaseQueries.updateSummaryReadStatus(true, id)
    }

    override suspend fun deleteSummary(id: String) {
        database.databaseQueries.deleteSummary(id)
        val remoteId = id.toLongOrNull()
        if (remoteId != null) {
            try {
                api.deleteSummary(remoteId)
            } catch (e: Exception) {
                logger.error(e) { "Failed to delete summary from API for id: $remoteId" }
                // Handle offline delete or queue it
            }
        }
    }
}
