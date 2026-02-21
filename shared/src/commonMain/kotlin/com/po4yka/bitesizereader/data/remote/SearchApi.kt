package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DuplicateUrlCheckResponseEnvelope
import com.po4yka.bitesizereader.data.remote.dto.RelatedSummariesResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SearchResponseDataDto
import com.po4yka.bitesizereader.data.remote.dto.TrendingTopicsDataDto

/**
 * Search API matching OpenAPI spec with semantic search enhancements.
 */
interface SearchApi {
    /** Full-text search using FTS5 */
    suspend fun search(
        query: String,
        page: Int,
        pageSize: Int,
    ): ApiResponseDto<SearchResponseDataDto>

    /**
     * Semantic search using embeddings.
     *
     * @param query Search query (2-200 chars)
     * @param page Page number
     * @param pageSize Items per page
     * @param language Filter by language (optional)
     * @param tags Filter by tags (optional)
     * @param userScope Scope for user filtering (optional)
     */
    suspend fun semanticSearch(
        query: String,
        page: Int,
        pageSize: Int,
        language: String? = null,
        tags: List<String>? = null,
        userScope: String? = null,
    ): ApiResponseDto<SearchResponseDataDto>

    suspend fun getTrendingTopics(
        limit: Int = 20,
        days: Int = 30,
    ): ApiResponseDto<TrendingTopicsDataDto>

    suspend fun getRelatedSummaries(
        tag: String,
        page: Int,
        pageSize: Int,
    ): ApiResponseDto<RelatedSummariesResponseDto>

    /** Get search insights (popular topics, reading patterns). */
    suspend fun getSearchInsights(
        days: Int = 30,
        limit: Int = 20,
    ): ApiResponseDto<SearchResponseDataDto>

    /**
     * Check if URL has already been summarized.
     *
     * @param url URL to check
     * @param includeSummary If true, include summary data in response
     */
    suspend fun checkDuplicateUrl(
        url: String,
        includeSummary: Boolean = false,
    ): ApiResponseDto<DuplicateUrlCheckResponseEnvelope>
}
