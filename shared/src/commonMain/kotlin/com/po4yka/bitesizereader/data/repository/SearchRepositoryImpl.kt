package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.SearchApi
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SearchRepository
import com.po4yka.bitesizereader.domain.usecase.DuplicateCheckResult
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single(binds = [SearchRepository::class])
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

    override suspend fun semanticSearch(
        query: String,
        page: Int,
        pageSize: Int,
        language: String?,
        tags: List<String>?,
    ): List<Summary> {
        val response = api.semanticSearch(
            query = query,
            page = page,
            pageSize = pageSize,
            language = language,
            tags = tags,
        )
        return response.data?.toDomain() ?: emptyList()
    }

    override suspend fun getTrendingTopics(): List<String> {
        return try {
            val response = api.getTrendingTopics()
            if (response.success && response.data != null) {
                response.data.tags.map { it.tag }
            } else {
                logger.error { "Failed to fetch trending topics: ${response.error}" }
                emptyList()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.error(e) { "Failed to fetch trending topics" }
            emptyList()
        }
    }

    override suspend fun getSearchInsights(
        days: Int,
        limit: Int,
    ): List<Summary> {
        val response = api.getSearchInsights(days, limit)
        return response.data?.toDomain() ?: emptyList()
    }

    override suspend fun checkDuplicateUrl(url: String): DuplicateCheckResult {
        val response = api.checkDuplicateUrl(url, includeSummary = false)
        val data = response.data
        return if (response.success && data != null) {
            DuplicateCheckResult(
                isDuplicate = data.data.isDuplicate,
                existingSummaryId = data.data.summaryId?.toString(),
            )
        } else {
            DuplicateCheckResult(isDuplicate = false, existingSummaryId = null)
        }
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
