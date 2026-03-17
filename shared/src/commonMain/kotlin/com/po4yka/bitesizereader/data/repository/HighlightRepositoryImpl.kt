package com.po4yka.bitesizereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.Highlight
import com.po4yka.bitesizereader.domain.model.HighlightColor
import com.po4yka.bitesizereader.domain.repository.HighlightRepository
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
            database.transaction {
                database.databaseQueries.insertHighlight(
                    id = id,
                    summaryId = summaryId,
                    text = text,
                    nodeOffset = nodeOffset,
                    color = color.colorName,
                    note = null,
                    createdAt = now,
                    syncStatus = "pending",
                )
                database.databaseQueries.insertPendingOperation(
                    entityId = id,
                    entityType = "highlight",
                    action = "create",
                    payload = null,
                    createdAt = now.toEpochMilliseconds(),
                )
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
            database.transaction {
                database.databaseQueries.deleteHighlight(id)
                database.databaseQueries.insertPendingOperation(
                    entityId = id,
                    entityType = "highlight",
                    action = "delete",
                    payload = null,
                    createdAt = now.toEpochMilliseconds(),
                )
            }
        }

    override suspend fun updateNote(
        highlightId: String,
        note: String?,
    ) {
        withContext(ioDispatcher) {
            val now = Clock.System.now()
            database.transaction {
                database.databaseQueries.updateHighlightNote(note = note, id = highlightId)
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
            database.transaction {
                database.databaseQueries.updateHighlightColor(color = color.colorName, id = highlightId)
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
}
