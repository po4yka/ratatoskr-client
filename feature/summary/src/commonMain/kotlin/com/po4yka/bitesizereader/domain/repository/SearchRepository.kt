package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.DuplicateCheckResult
import com.po4yka.bitesizereader.domain.model.Summary

interface SearchRepository {
    suspend fun search(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<Summary>

    suspend fun semanticSearch(
        query: String,
        page: Int,
        pageSize: Int,
        language: String? = null,
        tags: List<String>? = null,
    ): List<Summary>

    suspend fun getTrendingTopics(): List<String>

    suspend fun getSearchInsights(
        days: Int = 30,
        limit: Int = 20,
    ): List<Summary>

    suspend fun checkDuplicateUrl(url: String): DuplicateCheckResult

    // Search history operations
    suspend fun getRecentSearches(limit: Int = 10): List<String>

    suspend fun saveSearchQuery(query: String)

    suspend fun deleteSearchQuery(query: String)

    suspend fun clearSearchHistory()
}
