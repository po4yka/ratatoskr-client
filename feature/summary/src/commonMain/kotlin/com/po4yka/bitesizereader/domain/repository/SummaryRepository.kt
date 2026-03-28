package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.FeedbackIssue
import com.po4yka.bitesizereader.domain.model.FeedbackRating
import com.po4yka.bitesizereader.domain.model.ReadFilter
import com.po4yka.bitesizereader.domain.model.SortOrder
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.model.SummaryFeedback
import kotlinx.coroutines.flow.Flow

interface SummaryRepository {
    fun getSummaries(
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

    suspend fun markAsRead(id: String)

    suspend fun deleteSummary(id: String)

    suspend fun toggleFavorite(id: String)

    /**
     * Toggle favorite status locally and sync with the server.
     * Reverts local change on server failure.
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

    suspend fun prefetchContent(maxItems: Int): Int

    suspend fun getCacheSize(): Long

    suspend fun clearContentCache()

    suspend fun evictContentCacheIfNeeded()

    suspend fun submitFeedback(
        summaryId: String,
        rating: FeedbackRating,
        issues: List<FeedbackIssue>,
        comment: String?,
    )

    fun getFeedback(summaryId: String): Flow<SummaryFeedback?>
}
