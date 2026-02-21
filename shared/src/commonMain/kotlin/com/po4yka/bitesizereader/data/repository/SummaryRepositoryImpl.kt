package com.po4yka.bitesizereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.SummariesApi
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import com.po4yka.bitesizereader.domain.model.ReadFilter
import com.po4yka.bitesizereader.domain.model.SortOrder
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Clock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single(binds = [SummaryRepository::class])
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
            logger.debug { "Fetched ${entities.size} summaries from DB" }
            entities.map { it.toDomain() }
        }
    }

    override fun getSummariesFiltered(
        page: Int,
        pageSize: Int,
        readFilter: ReadFilter,
        sortOrder: SortOrder,
        selectedTag: String?,
    ): Flow<List<Summary>> {
        val isFavoritedFilter = readFilter == ReadFilter.FAVORITED
        return database.databaseQueries.selectSummariesFiltered(
            readFilterAll = if (readFilter == ReadFilter.ALL || isFavoritedFilter) 1L else 0L,
            isRead = readFilter == ReadFilter.READ,
            favoritedOnly = if (isFavoritedFilter) 1L else 0L,
            selectedTag = selectedTag,
            sortNewest = if (sortOrder == SortOrder.NEWEST) 1L else 0L,
            sortOldest = if (sortOrder == SortOrder.OLDEST) 1L else 0L,
            sortAlphabetical = if (sortOrder == SortOrder.ALPHABETICAL) 1L else 0L,
            limit = pageSize.toLong(),
            offset = ((page - 1) * pageSize).toLong(),
        ).asFlow().mapToList(Dispatchers.IO).map { entities ->
            logger.debug { "Fetched ${entities.size} filtered summaries from DB" }
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

    override suspend fun toggleFavorite(id: String) {
        val summary = database.databaseQueries.getSummaryById(id).executeAsOneOrNull() ?: return
        database.databaseQueries.updateSummaryFavoriteStatus(!summary.isFavorited, id)
    }

    override suspend fun deleteSummary(id: String) {
        database.databaseQueries.deleteSummary(id)
        val remoteId = id.toLongOrNull()
        if (remoteId != null) {
            try {
                api.deleteSummary(remoteId)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.warn(e) { "Failed to delete summary $remoteId from API, queuing for retry" }
                database.databaseQueries.insertPendingDelete(
                    id = id,
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                )
            }
        }
    }

    override suspend fun getFullContent(id: String): String? {
        val remoteId = id.toLongOrNull() ?: return null
        return try {
            val response = api.getContent(remoteId)
            if (response.success && response.data != null) {
                val articleContent = response.data.content.content
                database.databaseQueries.updateSummaryContent(articleContent, id)
                articleContent
            } else {
                null
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn(e) { "Failed to fetch full content for $id" }
            null
        }
    }

    override suspend fun getAllTags(): List<String> {
        return database.databaseQueries.getAllTags()
            .executeAsList()
            .flatMap { it }
            .distinct()
            .sorted()
    }
}
