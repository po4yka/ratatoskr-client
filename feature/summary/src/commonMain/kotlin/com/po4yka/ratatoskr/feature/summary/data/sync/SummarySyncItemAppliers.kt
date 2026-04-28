package com.po4yka.ratatoskr.feature.summary.data.sync

import com.po4yka.ratatoskr.data.mappers.toEntity
import com.po4yka.ratatoskr.data.remote.dto.HighlightDto
import com.po4yka.ratatoskr.data.remote.dto.SummaryCompactDto
import com.po4yka.ratatoskr.data.remote.dto.SyncItemDto
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.feature.sync.api.SyncItemApplier
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

internal class SummarySyncItemApplier(
    private val database: Database,
) : SyncItemApplier {
    override val entityType: String = "summary"

    override fun apply(item: SyncItemDto): Boolean =
        try {
            val summaryData = item.summary
            if (summaryData == null) {
                logger.warn { "Summary sync item ${item.idAsString} has no summary payload, skipping" }
                false
            } else {
                val dto = Json.decodeFromJsonElement(SummaryCompactDto.serializer(), summaryData)
                database.databaseQueries.insertSummary(dto.toEntity())
                true
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.error(e) { "Failed to process summary sync item ${item.idAsString}" }
            false
        }
}

internal class HighlightSyncItemApplier(
    private val database: Database,
) : SyncItemApplier {
    override val entityType: String = "highlight"

    override fun apply(item: SyncItemDto): Boolean =
        try {
            val highlightData = item.highlight
            if (highlightData == null) {
                logger.warn { "Highlight sync item ${item.idAsString} has no highlight payload, skipping" }
                false
            } else {
                val dto = Json.decodeFromJsonElement(HighlightDto.serializer(), highlightData)
                database.databaseQueries.upsertHighlight(
                    id = dto.id,
                    summary_id = dto.summaryId,
                    text = dto.text,
                    start_offset = dto.startOffset?.toLong(),
                    end_offset = dto.endOffset?.toLong(),
                    color = dto.color,
                    note = dto.note,
                    created_at = dto.createdAt,
                    updated_at = dto.updatedAt,
                )
                true
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.error(e) { "Failed to process highlight sync item ${item.idAsString}" }
            false
        }
}
