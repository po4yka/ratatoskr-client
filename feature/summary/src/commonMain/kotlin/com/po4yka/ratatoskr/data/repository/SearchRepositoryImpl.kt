package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.SearchApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.domain.model.DuplicateCheckResult
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.repository.SearchRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single(binds = [SearchRepository::class])
class SearchRepositoryImpl(
    private val database: Database,
) : SearchRepository {
    override suspend fun search(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<Summary> {
        val offset = (page.coerceAtLeast(1) - 1) * pageSize
        return try {
            val envelope = SearchApi.searchSummariesV1SearchGet(
                q = query,
                limit = pageSize.toLong(),
                offset = offset.toLong(),
            ).unwrap()
            envelope.data?.toDomain() ?: emptyList()
        } catch (e: Exception) {
            // Fallback to local search
            database.databaseQueries.searchSummaries(
                query = query,
                limit = pageSize.toLong(),
                offset = offset.toLong(),
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
        val offset = (page.coerceAtLeast(1) - 1) * pageSize
        val envelope = SearchApi.semanticSearchSummariesV1SearchSemanticGet(
            q = query,
            limit = pageSize.toLong(),
            offset = offset.toLong(),
            language = language,
            tags = tags,
        ).unwrap()
        return envelope.data?.toDomain() ?: emptyList()
    }

    override suspend fun getTrendingTopics(): List<String> {
        return try {
            val envelope = SearchApi.getTrendingTopicsV1TopicsTrendingGet().unwrap()
            envelope.data?.tags?.map { it.tag } ?: emptyList()
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
        val envelope = SearchApi.getSearchInsightsV1SearchInsightsGet(
            days = days.toLong(),
            limit = limit.toLong(),
        ).unwrap()
        return envelope.data?.toDomain() ?: emptyList()
    }

    override suspend fun checkDuplicateUrl(url: String): DuplicateCheckResult {
        val envelope = SearchApi.checkDuplicateV1UrlsCheckDuplicateGet(
            reqUrl = url,
            includeSummary = false,
        ).unwrap()
        val data = envelope.data
        return if (data != null) {
            DuplicateCheckResult(
                isDuplicate = data.isDuplicate,
                existingSummaryId = data.summaryId?.toString(),
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
