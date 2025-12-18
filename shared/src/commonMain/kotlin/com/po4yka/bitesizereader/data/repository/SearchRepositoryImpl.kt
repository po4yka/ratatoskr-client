package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.SearchApi
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SearchRepository
import kotlin.time.Clock
import org.koin.core.annotation.Single

@Single
class SearchRepositoryImpl(
    private val database: Database,
    private val api: SearchApi,
) : SearchRepository {
    override suspend fun search(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<Summary> {
        // Try local FTS first or API?
        // Usually hybrid: API for fresh results, DB for offline.
        // Here we simply call API and return results.
        return try {
            val response = api.search(query, page, pageSize)
            response.data?.toDomain() ?: emptyList()
        } catch (e: Exception) {
            // Fallback to local search
            database.databaseQueries.searchSummaries(
                query = query,
                limit = pageSize.toLong(),
                offset = ((page - 1) * pageSize).toLong(),
            )
                .executeAsList()
                .map { it.toDomain() }
        }
    }

    override suspend fun getTrendingTopics(): List<String> {
        // Mock implementation
        return listOf("Tech", "Science", "Health", "AI", "Space")
    }

    override suspend fun getRecentSearches(limit: Int): List<String> {
        return database.databaseQueries.selectRecentSearches(limit.toLong())
            .executeAsList()
    }

    override suspend fun saveSearchQuery(query: String) {
        if (query.isBlank()) return
        database.databaseQueries.insertSearchQuery(
            query = query.trim(),
            searchedAt = Clock.System.now(),
        )
    }

    override suspend fun deleteSearchQuery(query: String) {
        database.databaseQueries.deleteSearchQuery(query)
    }

    override suspend fun clearSearchHistory() {
        database.databaseQueries.clearSearchHistory()
    }
}
