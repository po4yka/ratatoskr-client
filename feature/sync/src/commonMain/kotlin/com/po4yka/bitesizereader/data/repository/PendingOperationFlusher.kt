package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.remote.HighlightsApi
import com.po4yka.bitesizereader.data.remote.SummariesApi
import com.po4yka.bitesizereader.data.remote.dto.CreateHighlightRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SubmitFeedbackRequestDto
import com.po4yka.bitesizereader.data.remote.dto.UpdateHighlightRequestDto
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.database.PendingOperationEntity
import com.po4yka.bitesizereader.domain.repository.ApplyResult
import com.po4yka.bitesizereader.domain.repository.LocalChange
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

internal class PendingOperationFlusher(
    private val database: Database,
    private val summariesApi: SummariesApi,
    private val highlightsApi: HighlightsApi,
    private val applyChanges: suspend (sessionId: String, changes: List<LocalChange>) -> ApplyResult,
    private val onConflictCount: (Int) -> Unit,
) {
    suspend fun flush(sessionId: String) {
        val pendingOps = database.databaseQueries.selectAllPendingOperations().executeAsList()
        if (pendingOps.isEmpty()) return

        logger.info { "Flushing ${pendingOps.size} pending operations" }

        flushHighlightOperations(pendingOps.filter { it.entityType == "highlight" })

        val nonFeedbackOps = buildPendingChanges(pendingOps)
        flushFeedbackOperations(pendingOps.filter { it.action == "submit_feedback" })

        val changes = nonFeedbackOps.map { it.second }
        if (changes.isEmpty()) return

        try {
            val result = applyChanges(sessionId, changes)
            nonFeedbackOps.forEach { (id, _) ->
                database.databaseQueries.deletePendingOperation(id)
            }
            database.databaseQueries.deleteAllPendingDeletes()
            if (result.conflicts.isNotEmpty()) {
                logger.warn {
                    "${result.conflicts.size} pending operation(s) rejected by server (server wins):"
                }
                result.conflicts.forEach { conflict ->
                    logger.warn {
                        "  conflict: entity=${conflict.entityType}#${conflict.id} " +
                            "reason=${conflict.reason} " +
                            "clientVersion=${conflict.clientVersion} serverVersion=${conflict.serverVersion}"
                    }
                }
                onConflictCount(result.conflicts.size)
            }
            logger.info {
                "Successfully flushed ${changes.size} pending operations " +
                    "(applied=${result.appliedCount}, conflicts=${result.conflicts.size})"
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn(e) { "Failed to flush pending operations, will retry next sync" }
        }
    }

    private suspend fun flushHighlightOperations(highlightOps: List<PendingOperationEntity>) {
        highlightOps.forEach { highlightOp ->
            try {
                flushHighlightOperation(highlightOp)
                database.databaseQueries.deletePendingOperation(highlightOp.id)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.warn { "Failed to sync highlight ${highlightOp.entityId}: ${e.message}" }
            }
        }
    }

    private suspend fun flushFeedbackOperations(feedbackOps: List<PendingOperationEntity>) {
        feedbackOps.forEach { feedbackOp ->
            try {
                val payload = PendingOperationPayloadParser.parseFeedbackPayload(feedbackOp.payload) ?: return@forEach
                val remoteId = feedbackOp.entityId.toLongOrNull() ?: return@forEach
                val response =
                    summariesApi.submitFeedback(
                        remoteId,
                        SubmitFeedbackRequestDto(
                            rating = payload.rating,
                            issues = payload.issues,
                            comment = payload.comment,
                        ),
                    )
                if (!response.success) {
                    logger.warn { "Server rejected feedback for ${feedbackOp.entityId}: ${response.error}" }
                    return@forEach
                }
                database.databaseQueries.updateFeedbackSyncStatus(
                    syncStatus = "synced",
                    summaryId = feedbackOp.entityId,
                )
                database.databaseQueries.deletePendingOperation(feedbackOp.id)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.warn { "Failed to sync feedback for ${feedbackOp.entityId}: ${e.message}" }
            }
        }
    }

    private fun buildPendingChanges(pendingOps: List<PendingOperationEntity>): List<Pair<Long, LocalChange>> =
        buildList {
            pendingOps.forEach { op ->
                if (op.entityType == "highlight" || op.action == "submit_feedback") return@forEach
                val remoteId = op.entityId.toLongOrNull() ?: return@forEach
                val change =
                    when (op.action) {
                        "delete" ->
                            LocalChange(
                                entityType = op.entityType,
                                id = remoteId,
                                action = "delete",
                                lastSeenVersion = 0,
                                payload = null,
                                clientTimestamp = null,
                            )
                        "update_read" -> {
                            val payload =
                                PendingOperationPayloadParser.parseReadUpdatePayload(op.payload)
                                    ?: return@forEach
                            LocalChange(
                                entityType = op.entityType,
                                id = remoteId,
                                action = "update",
                                lastSeenVersion = 0,
                                payload = payload.toSyncPayload(),
                                clientTimestamp = null,
                            )
                        }
                        "toggle_favorite" ->
                            LocalChange(
                                entityType = op.entityType,
                                id = remoteId,
                                action = "update",
                                lastSeenVersion = 0,
                                payload = FavoriteTogglePayload.toSyncPayload(),
                                clientTimestamp = null,
                            )
                        else -> {
                            logger.warn { "Unknown pending operation action: ${op.action}" }
                            null
                        }
                    }
                if (change != null) {
                    add(op.id to change)
                }
            }
        }

    private suspend fun flushHighlightOperation(op: PendingOperationEntity) {
        val highlightId = op.entityId
        when (op.action) {
            "create" -> {
                val entity =
                    database.databaseQueries.getHighlightById(highlightId).executeAsOneOrNull()
                        ?: run {
                            logger.warn { "Highlight $highlightId not found locally, skipping create sync" }
                            return
                        }
                val summaryId =
                    entity.summaryId.toLongOrNull()
                        ?: run {
                            logger.warn { "Invalid summaryId '${entity.summaryId}' for highlight $highlightId" }
                            return
                        }
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
                    database.databaseQueries.updateHighlightSyncStatus("synced", highlightId)
                } else {
                    logger.warn { "Server rejected highlight create for $highlightId: ${response.error}" }
                }
            }
            "update" -> {
                val entity =
                    database.databaseQueries.getHighlightById(highlightId).executeAsOneOrNull()
                        ?: run {
                            logger.warn { "Highlight $highlightId not found locally, skipping update sync" }
                            return
                        }
                val summaryId =
                    entity.summaryId.toLongOrNull()
                        ?: run {
                            logger.warn { "Invalid summaryId '${entity.summaryId}' for highlight $highlightId" }
                            return
                        }
                val response =
                    highlightsApi.updateHighlight(
                        summaryId = summaryId,
                        highlightId = highlightId,
                        request =
                            UpdateHighlightRequestDto(
                                color = entity.color,
                                note = entity.note,
                            ),
                    )
                if (response.success) {
                    database.databaseQueries.updateHighlightSyncStatus("synced", highlightId)
                } else {
                    logger.warn { "Server rejected highlight update for $highlightId: ${response.error}" }
                }
            }
            "delete" -> {
                val payload =
                    PendingOperationPayloadParser.parseHighlightDeletePayload(op.payload)
                        ?: run {
                            logger.warn {
                                "Cannot sync highlight delete for $highlightId: missing summaryId in payload"
                            }
                            return
                        }
                val summaryId =
                    payload.summaryIdAsLong()
                        ?: run {
                            logger.warn {
                                "Cannot sync highlight delete for $highlightId: " +
                                    "invalid summaryId '${payload.summaryId}'"
                            }
                            return
                        }
                val response = highlightsApi.deleteHighlight(summaryId, highlightId)
                if (!response.success) {
                    logger.warn { "Server rejected highlight delete for $highlightId: ${response.error}" }
                }
            }
            else -> logger.warn { "Unknown highlight action: ${op.action}" }
        }
    }
}

@Serializable
private data class ReadUpdatePayload(
    val is_read: Boolean,
)

@Serializable
private data class FeedbackPayload(
    val rating: String,
    val issues: List<String> = emptyList(),
    val comment: String? = null,
)

@Serializable
private data class HighlightDeletePayload(
    val summaryId: String,
)

private object FavoriteTogglePayload

private object PendingOperationPayloadParser {
    fun parseReadUpdatePayload(raw: String?): ReadUpdatePayload? =
        raw?.let { Json.decodeFromString<ReadUpdatePayload>(it) }

    fun parseFeedbackPayload(raw: String?): FeedbackPayload? = raw?.let { Json.decodeFromString<FeedbackPayload>(it) }

    fun parseHighlightDeletePayload(raw: String?): HighlightDeletePayload? =
        raw?.let { Json.decodeFromString<HighlightDeletePayload>(it) }
}

private fun ReadUpdatePayload.toSyncPayload(): Map<String, Any?> = mapOf("is_read" to is_read)

private fun FavoriteTogglePayload.toSyncPayload(): Map<String, Any?> = mapOf("toggle_favorite" to true)

private fun HighlightDeletePayload.summaryIdAsLong(): Long? = summaryId.toLongOrNull()
