package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Summary

interface SearchRepository {
    suspend fun search(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<Summary>

    suspend fun getTrendingTopics(): List<String>

    // Search history operations
    suspend fun getRecentSearches(limit: Int = 10): List<String>

    suspend fun saveSearchQuery(query: String)

    suspend fun deleteSearchQuery(query: String)

    suspend fun clearSearchHistory()
}
