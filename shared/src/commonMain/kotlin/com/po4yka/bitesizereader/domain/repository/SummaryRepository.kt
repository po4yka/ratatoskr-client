package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.SearchFilters
import com.po4yka.bitesizereader.domain.model.Summary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Summary operations
 */
interface SummaryRepository {
    /**
     * Get all summaries with optional filters
     */
    fun getSummaries(
        limit: Int = 20,
        offset: Int = 0,
        filters: SearchFilters = SearchFilters()
    ): Flow<List<Summary>>

    /**
     * Get a single summary by ID
     */
    suspend fun getSummaryById(id: Int): Result<Summary>

    /**
     * Mark summary as read/unread
     */
    suspend fun markAsRead(id: Int, isRead: Boolean): Result<Unit>

    /**
     * Mark summary as favorite/unfavorite
     */
    suspend fun markAsFavorite(id: Int, isFavorite: Boolean): Result<Unit>

    /**
     * Delete a summary
     */
    suspend fun deleteSummary(id: Int): Result<Unit>

    /**
     * Get unread summaries count
     */
    fun getUnreadCount(): Flow<Int>

    /**
     * Refresh summaries from server
     */
    suspend fun refresh(): Result<Unit>
}
