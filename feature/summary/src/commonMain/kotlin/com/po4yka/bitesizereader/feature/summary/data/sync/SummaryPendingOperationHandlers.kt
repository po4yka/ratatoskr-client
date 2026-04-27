package com.po4yka.bitesizereader.feature.summary.data.sync

import com.po4yka.bitesizereader.data.remote.HighlightsApi
import com.po4yka.bitesizereader.data.remote.SummariesApi
import com.po4yka.bitesizereader.data.remote.dto.CreateHighlightRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SubmitFeedbackRequestDto
import com.po4yka.bitesizereader.data.remote.dto.UpdateHighlightRequestDto
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.database.PendingOperationEntity
import com.po4yka.bitesizereader.feature.sync.api.PendingOperationHandler
import com.po4yka.bitesizereader.feature.sync.api.PendingOperationHandlingResult
import com.po4yka.bitesizereader.feature.sync.domain.repository.LocalChange
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

internal class SummaryPendingOperationHandler : PendingOperationHandler {
    override fun canHandle(operation: PendingOperationEntity): Boolean =
        operation.entityType == "summary" && operation.action in setOf("delete", "update_read", "toggle_favorite")

    override suspend fun handle(operation: PendingOperationEntity): PendingOperationHandlingResult {
        val remoteId = operation.entityId.toLongOrNull()
        if (remoteId == null) {
            logger.warn {
                "Dropping malformed summary pending operation ${operation.id}: bad entityId '${operation.entityId}'"
            }
            return PendingOperationHandlingResult.Completed()
        }

        val change =
            when (operation.action) {
                "delete" ->
                    LocalChange(
                        entityType = operation.entityType,
                        id = remoteId,
                        action = "delete",
                        lastSeenVersion = 0,
                        payload = null,
                        clientTimestamp = null,
                    )
                "update_read" -> {
                    val payload = operation.payload?.let { Json.decodeFromString<ReadUpdatePayload>(it) }
                    if (payload == null) {
                        logger.warn { "Dropping malformed read update payload for operation ${operation.id}" }
                        return PendingOperationHandlingResult.Completed()
                    }
                    LocalChange(
                        entityType = operation.entityType,
                        id = remoteId,
                        action = "update",
                        lastSeenVersion = 0,
                        payload = mapOf("is_read" to payload.is_read),
                        clientTimestamp = null,
                    )
                }
                "toggle_favorite" ->
                    LocalChange(
                        entityType = operation.entityType,
                        id = remoteId,
                        action = "update",
                        lastSeenVersion = 0,
                        payload = mapOf("toggle_favorite" to true),
                        clientTimestamp = null,
                    )
                else -> return PendingOperationHandlingResult.RetryLater
            }

        return PendingOperationHandlingResult.QueueChange(change)
    }
}

internal class SummaryFeedbackPendingOperationHandler(
    private val database: Database,
    private val summariesApi: SummariesApi,
) : PendingOperationHandler {
    override fun canHandle(operation: PendingOperationEntity): Boolean =
        operation.entityType == "summary" && operation.action == "submit_feedback"

    override suspend fun handle(operation: PendingOperationEntity): PendingOperationHandlingResult =
        try {
            val payload =
                operation.payload?.let { Json.decodeFromString<SubmitFeedbackRequestDto>(it) }
                    ?: run {
                        logger.warn { "Dropping malformed feedback payload for operation ${operation.id}" }
                        return PendingOperationHandlingResult.Completed()
                    }
            val remoteId = operation.entityId.toLongOrNull()
            if (remoteId == null) {
                logger.warn {
                    "Dropping malformed feedback operation ${operation.id}: bad entityId '${operation.entityId}'"
                }
                return PendingOperationHandlingResult.Completed()
            }

            val response = summariesApi.submitFeedback(remoteId, payload)
            if (!response.success) {
                logger.warn { "Server rejected feedback for ${operation.entityId}: ${response.error}" }
                PendingOperationHandlingResult.RetryLater
            } else {
                database.databaseQueries.updateFeedbackSyncStatus(
                    syncStatus = "synced",
                    summaryId = operation.entityId,
                )
                PendingOperationHandlingResult.Completed()
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn(e) { "Failed to sync feedback for ${operation.entityId}" }
            PendingOperationHandlingResult.RetryLater
        }
}

internal class HighlightPendingOperationHandler(
    private val database: Database,
    private val highlightsApi: HighlightsApi,
) : PendingOperationHandler {
    override fun canHandle(operation: PendingOperationEntity): Boolean = operation.entityType == "highlight"

    override suspend fun handle(operation: PendingOperationEntity): PendingOperationHandlingResult =
        when (operation.action) {
            "create" -> handleCreate(operation)
            "update" -> handleUpdate(operation)
            "delete" -> handleDelete(operation)
            else -> {
                logger.warn { "Unknown highlight action: ${operation.action}" }
                PendingOperationHandlingResult.Completed()
            }
        }

    private suspend fun handleCreate(operation: PendingOperationEntity): PendingOperationHandlingResult {
        val entity =
            database.databaseQueries.getHighlightById(operation.entityId).executeAsOneOrNull()
                ?: run {
                    logger.warn { "Highlight ${operation.entityId} not found locally, dropping create sync" }
                    return PendingOperationHandlingResult.Completed()
                }
        val summaryId =
            entity.summaryId.toLongOrNull()
                ?: run {
                    logger.warn { "Invalid summaryId '${entity.summaryId}' for highlight ${operation.entityId}" }
                    return PendingOperationHandlingResult.Completed()
                }
        return try {
            val response =
                highlightsApi.createHighlight(
                    summaryId = summaryId,
                    request =
                        CreateHighlightRequestDto(
                            text = entity.text,
                            startOffset = entity.nodeOffset,
                            color = entity.color,
                            note = entity.note,
                        ),
                )
            if (response.success) {
                database.databaseQueries.updateHighlightSyncStatus("synced", operation.entityId)
                PendingOperationHandlingResult.Completed()
            } else {
                logger.warn { "Server rejected highlight create for ${operation.entityId}: ${response.error}" }
                PendingOperationHandlingResult.RetryLater
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn(e) { "Failed to sync highlight create for ${operation.entityId}" }
            PendingOperationHandlingResult.RetryLater
        }
    }

    private suspend fun handleUpdate(operation: PendingOperationEntity): PendingOperationHandlingResult {
        val entity =
            database.databaseQueries.getHighlightById(operation.entityId).executeAsOneOrNull()
                ?: run {
                    logger.warn { "Highlight ${operation.entityId} not found locally, dropping update sync" }
                    return PendingOperationHandlingResult.Completed()
                }
        val summaryId =
            entity.summaryId.toLongOrNull()
                ?: run {
                    logger.warn { "Invalid summaryId '${entity.summaryId}' for highlight ${operation.entityId}" }
                    return PendingOperationHandlingResult.Completed()
                }
        return try {
            val response =
                highlightsApi.updateHighlight(
                    summaryId = summaryId,
                    highlightId = operation.entityId,
                    request =
                        UpdateHighlightRequestDto(
                            color = entity.color,
                            note = entity.note,
                        ),
                )
            if (response.success) {
                database.databaseQueries.updateHighlightSyncStatus("synced", operation.entityId)
                PendingOperationHandlingResult.Completed()
            } else {
                logger.warn { "Server rejected highlight update for ${operation.entityId}: ${response.error}" }
                PendingOperationHandlingResult.RetryLater
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn(e) { "Failed to sync highlight update for ${operation.entityId}" }
            PendingOperationHandlingResult.RetryLater
        }
    }

    private suspend fun handleDelete(operation: PendingOperationEntity): PendingOperationHandlingResult {
        val payload =
            operation.payload?.let { Json.decodeFromString<HighlightDeletePayload>(it) }
                ?: run {
                    logger.warn { "Dropping malformed highlight delete payload for operation ${operation.id}" }
                    return PendingOperationHandlingResult.Completed()
                }
        val summaryId =
            payload.summaryId.toLongOrNull()
                ?: run {
                    logger.warn {
                        "Dropping highlight delete ${operation.id}: invalid summaryId '${payload.summaryId}'"
                    }
                    return PendingOperationHandlingResult.Completed()
                }
        return try {
            val response = highlightsApi.deleteHighlight(summaryId, operation.entityId)
            if (response.success) {
                PendingOperationHandlingResult.Completed()
            } else {
                logger.warn { "Server rejected highlight delete for ${operation.entityId}: ${response.error}" }
                PendingOperationHandlingResult.RetryLater
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn(e) { "Failed to sync highlight delete for ${operation.entityId}" }
            PendingOperationHandlingResult.RetryLater
        }
    }
}

@Serializable
private data class ReadUpdatePayload(
    val is_read: Boolean,
)

@Serializable
private data class HighlightDeletePayload(
    val summaryId: String,
)
