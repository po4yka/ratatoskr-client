package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Summary

/**
 * Repository interface for Search operations
 */
interface SearchRepository {
    /**
     * Search summaries by query
     * Combines local FTS and remote API search
     */
    suspend fun search(
        query: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<Summary>>

    /**
     * Search local summaries only (using FTS)
     */
    suspend fun searchLocal(query: String): Result<List<Summary>>

    /**
     * Get trending topic tags
     */
    suspend fun getTrendingTopics(limit: Int = 10): Result<List<String>>
}
