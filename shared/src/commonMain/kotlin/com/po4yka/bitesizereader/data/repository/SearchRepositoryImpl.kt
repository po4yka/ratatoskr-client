package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toSummaries
import com.po4yka.bitesizereader.data.remote.api.SearchApi
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SearchRepository
import kotlinx.serialization.json.Json

/**
 * Implementation of SearchRepository combining local FTS and remote search
 */
class SearchRepositoryImpl(
    private val searchApi: SearchApi,
    private val database: Database
) : SearchRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun search(
        query: String,
        limit: Int,
        offset: Int
    ): Result<List<Summary>> {
        return try {
            // Search locally first for instant results
            val localResults = searchLocal(query).getOrNull() ?: emptyList()

            // Then search remotely for comprehensive results
            val response = searchApi.search(query, limit, offset)

            if (response.success && response.data != null) {
                val remoteResults = response.data.results.toSummaries()

                // Combine results, preferring remote (more comprehensive)
                val combined = (remoteResults + localResults)
                    .distinctBy { it.id }
                    .take(limit)

                Result.success(combined)
            } else {
                // Fall back to local results on error
                Result.success(localResults)
            }
        } catch (e: Exception) {
            // Fall back to local search on exception
            searchLocal(query)
        }
    }

    override suspend fun searchLocal(query: String): Result<List<Summary>> {
        return try {
            val results = database.summaryQueries.search(query)
                .executeAsList()
                .map { mapDbSummaryToDomain(it) }

            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrendingTopics(limit: Int): Result<List<String>> {
        return try {
            // Extract trending topics from local summaries
            val allSummaries = database.summaryQueries.selectAll().executeAsList()

            val topicFrequency = mutableMapOf<String, Int>()

            allSummaries.forEach { summary ->
                val topics: List<String> = json.decodeFromString(summary.topicTags)
                topics.forEach { topic ->
                    topicFrequency[topic] = (topicFrequency[topic] ?: 0) + 1
                }
            }

            val trending = topicFrequency
                .entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { it.key }

            Result.success(trending)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapDbSummaryToDomain(dbSummary: com.po4yka.bitesizereader.database.Summary): Summary {
        return Summary(
            id = dbSummary.id.toInt(),
            requestId = dbSummary.requestId.toInt(),
            title = dbSummary.title,
            url = dbSummary.url,
            domain = dbSummary.domain,
            tldr = dbSummary.tldr,
            summary250 = dbSummary.summary250,
            summary1000 = dbSummary.summary1000,
            keyIdeas = json.decodeFromString(dbSummary.keyIdeas),
            topicTags = json.decodeFromString(dbSummary.topicTags),
            answeredQuestions = json.decodeFromString(dbSummary.answeredQuestions),
            seoKeywords = json.decodeFromString(dbSummary.seoKeywords),
            readingTimeMin = dbSummary.readingTimeMin.toInt(),
            lang = dbSummary.lang,
            entities = dbSummary.entities?.let { json.decodeFromString(it) },
            keyStats = json.decodeFromString(dbSummary.keyStats),
            readability = dbSummary.readability?.let { json.decodeFromString(it) },
            isRead = dbSummary.isRead == 1L,
            isFavorite = dbSummary.isFavorite == 1L,
            createdAt = kotlinx.datetime.Instant.parse(dbSummary.createdAt),
            updatedAt = dbSummary.updatedAt?.let { kotlinx.datetime.Instant.parse(it) },
            syncStatus = com.po4yka.bitesizereader.domain.model.SyncStatus.valueOf(dbSummary.syncStatus),
            locallyModified = dbSummary.locallyModified == 1L
        )
    }
}
