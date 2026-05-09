package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.FeedbackIssue
import com.po4yka.ratatoskr.domain.model.FeedbackRating
import com.po4yka.ratatoskr.domain.model.ReadFilter
import com.po4yka.ratatoskr.domain.model.SortOrder
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.model.SummaryFeedback
import com.po4yka.ratatoskr.feature.summary.api.ContentCachePort
import com.po4yka.ratatoskr.feature.summary.api.SummaryFeedPort
import kotlinx.coroutines.flow.Flow

interface SummaryRepository : SummaryFeedPort, ContentCachePort {
    override fun getSummaries(
        page: Int,
        pageSize: Int,
        tags: List<String>?,
    ): Flow<List<Summary>>

    fun getSummariesFiltered(
        page: Int,
        pageSize: Int,
        readFilter: ReadFilter,
        sortOrder: SortOrder,
        selectedTag: String? = null,
    ): Flow<List<Summary>>

    suspend fun getSummaryById(id: String): Summary?

    suspend fun getFullContent(id: String): String?

    /**
     * Returns fresh content if the cache is stale (or missing), null if already fresh.
     * Safe to call in the background after [getFullContent] has returned cached content.
     */
    suspend fun refreshFullContentIfStale(id: String): String?

    /**
     * Marks the summary as read locally and queues a server sync for remote-backed IDs.
     * Completes successfully even if the server sync is deferred or never reaches the server.
     */
    suspend fun markAsRead(id: String)

    suspend fun deleteSummary(id: String)

    suspend fun toggleFavorite(id: String)

    /**
     * Toggle favorite status locally and queue for server sync.
     * Local state is updated optimistically and not rolled back on failure — the sync worker retries.
     */
    suspend fun toggleFavoriteWithSync(id: String)

    suspend fun getSummaryByUrl(url: String): Summary?

    suspend fun getAllTags(): List<String>

    suspend fun saveReadPosition(
        id: String,
        position: Int,
        offset: Int,
    )

    suspend fun archiveSummary(id: String)

    suspend fun unarchiveSummary(id: String)

    suspend fun prefetchRecentContent(maxItems: Int): Int

    override suspend fun getCacheSize(): Long

    override suspend fun clearContentCache()

    suspend fun evictContentCacheIfNeeded()

    suspend fun submitFeedback(
        summaryId: String,
        rating: FeedbackRating,
        issues: List<FeedbackIssue>,
        comment: String?,
    )

    fun getFeedback(summaryId: String): Flow<SummaryFeedback?>
}
