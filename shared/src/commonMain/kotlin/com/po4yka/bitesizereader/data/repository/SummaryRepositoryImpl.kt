package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.local.DatabaseHelper
import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.mappers.toUpdateRequestDto
import com.po4yka.bitesizereader.data.remote.api.SummariesApi
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.SearchFilters
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.model.SyncStatus
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * Implementation of SummaryRepository with offline-first architecture
 */
class SummaryRepositoryImpl(
    private val summariesApi: SummariesApi,
    private val database: Database,
    private val databaseHelper: DatabaseHelper,
) : SummaryRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override fun getSummaries(
        limit: Int,
        offset: Int,
        filters: SearchFilters,
    ): Flow<List<Summary>> =
        flow {
            // Emit cached data first (offline-first)
            val cachedSummaries = getCachedSummaries(limit, offset, filters)
            emit(cachedSummaries)

            // Then fetch from API and update cache
            try {
                val response =
                    summariesApi.getSummaries(
                        limit = limit,
                        offset = offset,
                        isRead = filters.isRead,
                        lang = filters.lang,
                        fromDate = filters.fromDate,
                        toDate = filters.toDate,
                        sortBy = filters.sortBy.name.lowercase(),
                        sortOrder = filters.sortOrder.name.lowercase(),
                    )

                if (response.success && response.data != null) {
                    val summaries = response.data.summaries.map { it.toDomain() }

                    // Update cache
                    summaries.forEach { databaseHelper.insertSummary(it) }

                    // Emit fresh data
                    emit(summaries)
                }
            } catch (e: Exception) {
                // On error, cached data is already emitted
                // Log error or handle it appropriately
            }
        }

    override suspend fun getSummaryById(id: Int): Result<Summary> {
        return try {
            // Try cache first
            val cached =
                database.summaryQueries.selectById(id.toLong())
                    .executeAsOneOrNull()
                    ?.let { mapDbSummaryToDomain(it) }

            if (cached != null) {
                // Return cached and fetch in background
                fetchAndCacheSummaryById(id)
                Result.success(cached)
            } else {
                // Not in cache, must fetch
                val response = summariesApi.getSummaryById(id)

                if (response.success && response.data != null) {
                    val summary = response.data.toDomain()
                    databaseHelper.insertSummary(summary)
                    Result.success(summary)
                } else {
                    Result.failure(Exception(response.error?.message ?: "Failed to fetch summary"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(
        id: Int,
        isRead: Boolean,
    ): Result<Unit> {
        return try {
            // Update locally first (optimistic update)
            database.summaryQueries.updateReadStatus(
                isRead = if (isRead) 1L else 0L,
                id = id.toLong(),
            )

            // Update on server
            val request =
                Summary(
                    id = id,
                    requestId = 0,
                    title = "",
                    url = "",
                    domain = null,
                    tldr = "",
                    summary250 = "",
                    summary1000 = null,
                    keyIdeas = emptyList(),
                    topicTags = emptyList(),
                    answeredQuestions = emptyList(),
                    seoKeywords = emptyList(),
                    readingTimeMin = 0,
                    lang = "",
                    entities = null,
                    keyStats = emptyList(),
                    readability = null,
                    isRead = isRead,
                    createdAt = kotlinx.datetime.Clock.System.now(),
                ).toUpdateRequestDto()

            val response = summariesApi.updateSummary(id, request)

            if (response.success) {
                // Mark as synced
                database.summaryQueries.markAsSynced(listOf(id.toLong()))
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error?.message ?: "Failed to update"))
            }
        } catch (e: Exception) {
            // Keep local change for later sync
            Result.failure(e)
        }
    }

    override suspend fun markAsFavorite(
        id: Int,
        isFavorite: Boolean,
    ): Result<Unit> {
        return try {
            database.summaryQueries.updateFavoriteStatus(
                isFavorite = if (isFavorite) 1L else 0L,
                id = id.toLong(),
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSummary(id: Int): Result<Unit> {
        return try {
            database.summaryQueries.deleteById(id.toLong())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUnreadCount(): Flow<Int> {
        return database.summaryQueries.countUnread()
            .asFlow()
            .map { it.executeAsOne().toInt() }
    }

    override suspend fun refresh(): Result<Unit> {
        return try {
            val response = summariesApi.getSummaries(limit = 100, offset = 0)

            if (response.success && response.data != null) {
                val summaries = response.data.summaries.map { it.toDomain() }
                summaries.forEach { databaseHelper.insertSummary(it) }
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error?.message ?: "Refresh failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Private helper methods

    private fun getCachedSummaries(
        limit: Int,
        offset: Int,
        filters: SearchFilters,
    ): List<Summary> {
        return when {
            filters.isRead == false -> {
                database.summaryQueries.selectUnread()
                    .executeAsList()
                    .map { mapDbSummaryToDomain(it) }
            }
            filters.lang != null -> {
                database.summaryQueries.selectByLang(filters.lang)
                    .executeAsList()
                    .map { mapDbSummaryToDomain(it) }
            }
            else -> {
                database.summaryQueries.selectPaginated(
                    limit.toLong(),
                    offset.toLong(),
                )
                    .executeAsList()
                    .map { mapDbSummaryToDomain(it) }
            }
        }
    }

    private suspend fun fetchAndCacheSummaryById(id: Int) {
        try {
            val response = summariesApi.getSummaryById(id)
            if (response.success && response.data != null) {
                val summary = response.data.toDomain()
                databaseHelper.insertSummary(summary)
            }
        } catch (e: Exception) {
            // Silently fail, cached data is already being used
        }
    }

    private fun mapDbSummaryToDomain(dbSummary: com.po4yka.bitesizereader.database.Summary): Summary {
        return Summary(
            id = dbSummary.id.toInt(),
            requestId = dbSummary.requestId.toInt(),
            title = dbSummary.title,
            url = dbSummary.url,
            domain = dbSummary.domain,
            tldr = dbSummary.tldr,
            summary250 = dbSummary.summary250,
            summary1000 = dbSummary.summary1000,
            keyIdeas = json.decodeFromString(dbSummary.keyIdeas),
            topicTags = json.decodeFromString(dbSummary.topicTags),
            answeredQuestions = json.decodeFromString(dbSummary.answeredQuestions),
            seoKeywords = json.decodeFromString(dbSummary.seoKeywords),
            readingTimeMin = dbSummary.readingTimeMin.toInt(),
            lang = dbSummary.lang,
            entities = dbSummary.entities?.let { json.decodeFromString(it) },
            keyStats = json.decodeFromString(dbSummary.keyStats),
            readability = dbSummary.readability?.let { json.decodeFromString(it) },
            isRead = dbSummary.isRead == 1L,
            isFavorite = dbSummary.isFavorite == 1L,
            createdAt = kotlinx.datetime.Instant.parse(dbSummary.createdAt),
            updatedAt = dbSummary.updatedAt?.let { kotlinx.datetime.Instant.parse(it) },
            syncStatus = SyncStatus.valueOf(dbSummary.syncStatus),
            locallyModified = dbSummary.locallyModified == 1L,
        )
    }
}
