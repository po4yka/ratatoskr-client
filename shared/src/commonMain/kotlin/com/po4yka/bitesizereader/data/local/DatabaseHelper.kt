package com.po4yka.bitesizereader.data.local

import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Helper class for database operations with JSON serialization
 */
class DatabaseHelper(private val database: Database) {
    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

    /**
     * Insert or update a summary in the database
     */
    fun insertSummary(summary: Summary) {
        database.summaryQueries.insert(
            id = summary.id.toLong(),
            requestId = summary.requestId.toLong(),
            title = summary.title,
            url = summary.url,
            domain = summary.domain,
            tldr = summary.tldr,
            summary250 = summary.summary250,
            summary1000 = summary.summary1000,
            keyIdeas = json.encodeToString(summary.keyIdeas),
            topicTags = json.encodeToString(summary.topicTags),
            answeredQuestions = json.encodeToString(summary.answeredQuestions),
            seoKeywords = json.encodeToString(summary.seoKeywords),
            readingTimeMin = summary.readingTimeMin.toLong(),
            lang = summary.lang,
            entities = summary.entities?.let { json.encodeToString(it) },
            keyStats = json.encodeToString(summary.keyStats),
            readability = summary.readability?.let { json.encodeToString(it) },
            isRead = if (summary.isRead) 1L else 0L,
            isFavorite = if (summary.isFavorite) 1L else 0L,
            createdAt = summary.createdAt.toString(),
            updatedAt = summary.updatedAt?.toString(),
            syncStatus = summary.syncStatus.name,
            locallyModified = if (summary.locallyModified) 1L else 0L,
        )
    }

    /**
     * Insert or update a request in the database
     */
    fun insertRequest(request: Request) {
        database.requestQueries.insert(
            id = request.id.toLong(),
            inputUrl = request.inputUrl,
            type = request.type.name,
            status = request.status.name,
            stage = request.stage?.name,
            progress = request.progress.toLong(),
            langPreference = request.langPreference,
            summaryId = request.summaryId?.toLong(),
            errorMessage = request.errorMessage,
            canRetry = if (request.canRetry) 1L else 0L,
            estimatedSecondsRemaining = request.estimatedSecondsRemaining?.toLong(),
            createdAt = request.createdAt.toString(),
            updatedAt = request.updatedAt?.toString(),
            completedAt = request.completedAt?.toString(),
        )
    }

    /**
     * Clear all summaries from the database
     */
    fun clearAllSummaries() {
        database.summaryQueries.deleteAll()
    }

    /**
     * Mark summaries as synced
     */
    fun markAsSynced(ids: List<Int>) {
        database.summaryQueries.markAsSynced(ids.map { it.toLong() })
    }

    /**
     * Get/set sync metadata
     */
    fun getSyncMetadata(key: String): String? {
        return database.syncMetadataQueries.get(key).executeAsOneOrNull()
    }

    fun setSyncMetadata(
        key: String,
        value: String,
    ) {
        database.syncMetadataQueries.set(key, value)
    }
}
