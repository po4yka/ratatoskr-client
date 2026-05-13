package com.po4yka.ratatoskr.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.po4yka.ratatoskr.api.generated.api.HighlightsApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.V1SummariesSummaryIdHighlightsHighlightIdRequest
import com.po4yka.ratatoskr.api.generated.models.V1SummariesSummaryIdHighlightsRequest
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.domain.model.Highlight
import com.po4yka.ratatoskr.domain.model.HighlightColor
import com.po4yka.ratatoskr.domain.repository.HighlightRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalUuidApi::class)
@Single(binds = [HighlightRepository::class])
class HighlightRepositoryImpl(
    private val database: Database,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : HighlightRepository {
    override fun getHighlightsForSummary(summaryId: String): Flow<List<Highlight>> =
        database.databaseQueries.getHighlightsForSummary(summaryId)
            .asFlow()
            .mapToList(ioDispatcher)
            .map { entities ->
                entities.map { entity ->
                    Highlight(
                        id = entity.id,
                        summaryId = entity.summaryId,
                        text = entity.text,
                        nodeOffset = entity.nodeOffset,
                        color =
                            HighlightColor.entries.find { it.colorName == entity.color }
                                ?: HighlightColor.YELLOW,
                        note = entity.note,
                        createdAt = entity.createdAt,
                    )
                }
            }

    override suspend fun addHighlight(
        summaryId: String,
        text: String,
        nodeOffset: Int,
        color: HighlightColor,
    ): Highlight =
        withContext(ioDispatcher) {
            val id = Uuid.random().toString()
            val now = Clock.System.now()
            val syncStatus = trySyncCreate(summaryId, text, nodeOffset, color)
            database.transaction {
                database.databaseQueries.insertHighlight(
                    id = id,
                    summaryId = summaryId,
                    text = text,
                    nodeOffset = nodeOffset,
                    color = color.colorName,
                    note = null,
                    createdAt = now,
                    syncStatus = syncStatus,
                )
                if (syncStatus == "pending") {
                    database.databaseQueries.insertPendingOperation(
                        entityId = id,
                        entityType = "highlight",
                        action = "create",
                        payload = null,
                        createdAt = now.toEpochMilliseconds(),
                    )
                }
            }
            Highlight(
                id = id,
                summaryId = summaryId,
                text = text,
                nodeOffset = nodeOffset,
                color = color,
                note = null,
                createdAt = now,
            )
        }

    override suspend fun removeHighlight(id: String) =
        withContext(ioDispatcher) {
            val now = Clock.System.now()
            // Look up summaryId before deleting -- needed for the API path during sync
            val entity = database.databaseQueries.getHighlightById(id).executeAsOneOrNull()
            val summaryId = entity?.summaryId
            val synced = summaryId?.let { trySyncDelete(it, id) } ?: false
            database.transaction {
                database.databaseQueries.deleteHighlight(id)
                if (!synced) {
                    database.databaseQueries.insertPendingOperation(
                        entityId = id,
                        entityType = "highlight",
                        action = "delete",
                        payload = summaryId?.let { """{"summaryId":"$it"}""" },
                        createdAt = now.toEpochMilliseconds(),
                    )
                }
            }
        }

    override suspend fun updateNote(
        highlightId: String,
        note: String?,
    ) {
        withContext(ioDispatcher) {
            val now = Clock.System.now()
            database.databaseQueries.updateHighlightNote(note = note, id = highlightId)
            val synced = trySyncUpdate(highlightId)
            if (!synced) {
                database.databaseQueries.insertPendingOperation(
                    entityId = highlightId,
                    entityType = "highlight",
                    action = "update",
                    payload = null,
                    createdAt = now.toEpochMilliseconds(),
                )
            }
        }
    }

    override suspend fun updateColor(
        highlightId: String,
        color: HighlightColor,
    ) {
        withContext(ioDispatcher) {
            val now = Clock.System.now()
            database.databaseQueries.updateHighlightColor(color = color.colorName, id = highlightId)
            val synced = trySyncUpdate(highlightId)
            if (!synced) {
                database.databaseQueries.insertPendingOperation(
                    entityId = highlightId,
                    entityType = "highlight",
                    action = "update",
                    payload = null,
                    createdAt = now.toEpochMilliseconds(),
                )
            }
        }
    }

    /**
     * Attempt to create the highlight on the server immediately.
     * Returns "synced" on success, "pending" on failure (to queue for later sync).
     */
    private suspend fun trySyncCreate(
        summaryId: String,
        text: String,
        nodeOffset: Int,
        color: HighlightColor,
    ): String {
        val remoteId = summaryId.toLongOrNull() ?: return "pending"
        return try {
            HighlightsApi.createHighlightV1SummariesSummaryIdHighlightsPost(
                summaryId = remoteId,
                body = V1SummariesSummaryIdHighlightsRequest(
                    text = text,
                    startOffset = nodeOffset.toLong(),
                    color = color.colorName,
                ),
            ).unwrap()
            "synced"
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.debug { "Highlight create will be synced later: ${e.message}" }
            "pending"
        }
    }

    /**
     * Attempt to update the highlight on the server immediately.
     * Returns true on success, false to queue for later sync.
     */
    private suspend fun trySyncUpdate(highlightId: String): Boolean {
        val entity =
            database.databaseQueries.getHighlightById(highlightId).executeAsOneOrNull()
                ?: return false
        val remoteId = entity.summaryId.toLongOrNull() ?: return false
        return try {
            HighlightsApi.updateHighlightV1SummariesSummaryIdHighlightsHighlightIdPatch(
                summaryId = remoteId,
                highlightId = highlightId,
                body = V1SummariesSummaryIdHighlightsHighlightIdRequest(
                    color = entity.color,
                    note = entity.note,
                ),
            ).unwrap()
            database.databaseQueries.updateHighlightSyncStatus("synced", highlightId)
            true
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.debug { "Highlight update will be synced later: ${e.message}" }
            false
        }
    }

    /**
     * Attempt to delete the highlight on the server immediately.
     * Returns true on success, false to queue for later sync.
     */
    private suspend fun trySyncDelete(
        summaryId: String,
        highlightId: String,
    ): Boolean {
        val remoteId = summaryId.toLongOrNull() ?: return false
        return try {
            HighlightsApi.deleteHighlightV1SummariesSummaryIdHighlightsHighlightIdDelete(
                summaryId = remoteId,
                highlightId = highlightId,
            ).unwrap()
            true
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.debug { "Highlight delete will be synced later: ${e.message}" }
            false
        }
    }
}
