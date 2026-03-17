package com.po4yka.bitesizereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.SummariesApi
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.FeedbackIssue
import com.po4yka.bitesizereader.domain.model.FeedbackRating
import com.po4yka.bitesizereader.domain.model.ReadFilter
import com.po4yka.bitesizereader.domain.model.SortOrder
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.model.SummaryFeedback
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import com.po4yka.bitesizereader.util.MarkdownSanitizer
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.coroutines.coroutineContext
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single(binds = [SummaryRepository::class])
class SummaryRepositoryImpl(
    private val database: Database,
    private val api: SummariesApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
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
        ).asFlow().mapToList(ioDispatcher).map { entities ->
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
        val isArchivedFilter = readFilter == ReadFilter.ARCHIVED
        val isFavoritedFilter = readFilter == ReadFilter.FAVORITED
        return database.databaseQueries.selectSummariesFiltered(
            readFilterAll = if (readFilter == ReadFilter.ALL || isFavoritedFilter || isArchivedFilter) 1L else 0L,
            isRead = readFilter == ReadFilter.READ,
            favoritedOnly = if (isFavoritedFilter) 1L else 0L,
            selectedTag = selectedTag,
            isArchived = isArchivedFilter,
            sortNewest = if (sortOrder == SortOrder.NEWEST) 1L else 0L,
            sortOldest = if (sortOrder == SortOrder.OLDEST) 1L else 0L,
            sortAlphabetical = if (sortOrder == SortOrder.ALPHABETICAL) 1L else 0L,
            limit = pageSize.toLong(),
            offset = ((page - 1) * pageSize).toLong(),
        ).asFlow().mapToList(ioDispatcher).map { entities ->
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
        // Queue for server sync
        val remoteId = id.toLongOrNull() ?: return
        database.databaseQueries.deletePendingOperationsForEntity(
            entityId = id,
            action = "update_read",
        )
        database.databaseQueries.insertPendingOperation(
            entityId = id,
            entityType = "summary",
            action = "update_read",
            payload = """{"is_read": true}""",
            createdAt = Clock.System.now().toEpochMilliseconds(),
        )
    }

    override suspend fun toggleFavorite(id: String) {
        val summary = database.databaseQueries.getSummaryById(id).executeAsOneOrNull() ?: return
        database.databaseQueries.updateSummaryFavoriteStatus(!summary.isFavorited, id)
    }

    override suspend fun toggleFavoriteWithSync(id: String) {
        // Optimistic local update
        toggleFavorite(id)

        // Queue for server sync instead of immediate API call
        val remoteId = id.toLongOrNull() ?: return
        val summary = database.databaseQueries.getSummaryById(id).executeAsOneOrNull() ?: return
        // Remove any existing pending favorite toggle for this entity
        database.databaseQueries.deletePendingOperationsForEntity(
            entityId = id,
            action = "toggle_favorite",
        )
        database.databaseQueries.insertPendingOperation(
            entityId = id,
            entityType = "summary",
            action = "toggle_favorite",
            payload = null,
            createdAt = Clock.System.now().toEpochMilliseconds(),
        )
    }

    override suspend fun getSummaryByUrl(url: String): Summary? {
        val response = api.getSummaryByUrl(url)
        return if (response.success && response.data != null) {
            response.data.toDomain()
        } else {
            null
        }
    }

    override suspend fun deleteSummary(id: String) {
        val remoteId = id.toLongOrNull()

        // 1. Atomically delete locally and queue for remote delete
        database.transaction {
            database.databaseQueries.deleteSummary(id)
            if (remoteId != null) {
                database.databaseQueries.insertPendingOperation(
                    entityId = id,
                    entityType = "summary",
                    action = "delete",
                    payload = null,
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                )
            }
        }

        // 2. Attempt remote delete immediately, clear from queue on success
        if (remoteId != null) {
            try {
                api.deleteSummary(remoteId)
                database.databaseQueries.deletePendingOperationsForEntity(
                    entityId = id,
                    action = "delete",
                )
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.warn(e) { "Remote delete failed for $remoteId, queued for retry" }
            }
        }
    }

    override suspend fun getFullContent(id: String): String? {
        val entity = database.databaseQueries.getSummaryById(id).executeAsOneOrNull()
        // Return any cached content immediately (even stale).
        // Callers that want fresh content should follow up with refreshFullContentIfStale().
        if (entity?.fullContent != null) {
            return entity.fullContent
        }
        return fetchAndCacheContent(id, entity?.fullContent)
    }

    override suspend fun refreshFullContentIfStale(id: String): String? {
        val entity = database.databaseQueries.getSummaryById(id).executeAsOneOrNull()
        if (entity?.fullContent != null) {
            val cachedAt = entity.fullContentCachedAt
            val isFresh = cachedAt != null && (Clock.System.now() - cachedAt) < CONTENT_CACHE_TTL
            if (isFresh) return null
        }
        return fetchAndCacheContent(id, entity?.fullContent)
    }

    private suspend fun fetchAndCacheContent(
        id: String,
        fallback: String?,
    ): String? {
        val remoteId = id.toLongOrNull()
        if (remoteId == null) {
            logger.warn { "Cannot parse remote ID from '$id', returning cached content" }
            return fallback
        }
        return try {
            val response = api.getContent(remoteId)
            if (response.success && response.data != null) {
                val articleContent = MarkdownSanitizer.sanitize(response.data.content.content)
                database.databaseQueries.updateSummaryFullContent(
                    fullContent = articleContent,
                    cachedAt = Clock.System.now(),
                    id = id,
                )
                evictContentCacheIfNeeded()
                articleContent
            } else {
                fallback
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn(e) { "Failed to fetch full content for $id" }
            fallback
        }
    }

    override suspend fun saveReadPosition(
        id: String,
        position: Int,
        offset: Int,
    ) {
        database.databaseQueries.updateReadPosition(
            position = position,
            offset = offset,
            id = id,
        )
    }

    override suspend fun archiveSummary(id: String) {
        database.databaseQueries.archiveSummary(id)
    }

    override suspend fun unarchiveSummary(id: String) {
        database.databaseQueries.unarchiveSummary(id)
    }

    override suspend fun prefetchContent(maxItems: Int): Int {
        val uncachedIds =
            database.databaseQueries
                .getUncachedUnreadSummaryIds(count = maxItems.toLong())
                .executeAsList()

        if (uncachedIds.isEmpty()) return 0

        var prefetched = 0
        for (id in uncachedIds) {
            coroutineContext.ensureActive()
            try {
                getFullContent(id)
                prefetched++
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.warn(e) { "Prefetch failed for $id, stopping" }
                break
            }
        }
        if (prefetched > 0) {
            logger.info { "Prefetched content for $prefetched/$maxItems summaries" }
        }
        return prefetched
    }

    override suspend fun getCacheSize(): Long {
        return database.databaseQueries.getCacheSize().executeAsOne().toLong()
    }

    override suspend fun clearContentCache() {
        database.databaseQueries.clearContentCache()
    }

    override suspend fun evictContentCacheIfNeeded() {
        val currentSize = getCacheSize()
        if (currentSize > MAX_CACHE_SIZE_BYTES) {
            logger.info { "Content cache size ${currentSize / 1024}KB exceeds limit, evicting oldest entries" }
            database.databaseQueries.evictOldestContentCache(count = EVICTION_BATCH_SIZE.toLong())
        }
    }

    companion object {
        private val CONTENT_CACHE_TTL = 7.days
        private const val MAX_CACHE_SIZE_BYTES = 50L * 1024 * 1024
        private const val EVICTION_BATCH_SIZE = 10
    }

    override suspend fun getAllTags(): List<String> {
        return database.databaseQueries.getAllTags()
            .executeAsList()
            .flatMap { it }
            .distinct()
            .sorted()
    }

    override suspend fun submitFeedback(
        summaryId: String,
        rating: FeedbackRating,
        issues: List<FeedbackIssue>,
        comment: String?,
    ) {
        val issuesStr = issues.joinToString(",") { it.name }
        database.databaseQueries.insertOrReplaceFeedback(
            summaryId = summaryId,
            rating = rating.name,
            issues = issuesStr.ifEmpty { null },
            comment = comment,
            createdAt = Clock.System.now(),
            syncStatus = "pending",
        )
        database.databaseQueries.insertPendingOperation(
            entityId = summaryId,
            entityType = "summary",
            action = "submit_feedback",
            payload = buildFeedbackPayload(rating, issues, comment),
            createdAt = Clock.System.now().toEpochMilliseconds(),
        )
    }

    private fun buildFeedbackPayload(
        rating: FeedbackRating,
        issues: List<FeedbackIssue>,
        comment: String?,
    ): String {
        val issuesStr = issues.joinToString(",") { it.name }
        val commentJson =
            if (comment != null) {
                "\"comment\":\"${comment.replace(
                    "\"",
                    "\\\"",
                )}\""
            } else {
                "\"comment\":null"
            }
        return "{\"rating\":\"${rating.name}\",\"issues\":\"$issuesStr\",$commentJson}"
    }

    override fun getFeedback(summaryId: String): Flow<SummaryFeedback?> {
        return database.databaseQueries.getFeedbackForSummary(summaryId)
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { entity ->
                entity?.let {
                    SummaryFeedback(
                        summaryId = it.summaryId,
                        rating = FeedbackRating.valueOf(it.rating),
                        issues =
                            it.issues
                                ?.split(",")
                                ?.filter { part -> part.isNotBlank() }
                                ?.mapNotNull { part ->
                                    runCatching { FeedbackIssue.valueOf(part) }.getOrNull()
                                }
                                ?: emptyList(),
                        comment = it.comment,
                        createdAt = it.createdAt,
                    )
                }
            }
    }
}
