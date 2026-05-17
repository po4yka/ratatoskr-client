package com.po4yka.ratatoskr.feature.summary.data.sync

import com.po4yka.ratatoskr.data.mappers.toEntity
import com.po4yka.ratatoskr.data.remote.dto.HighlightDto
import com.po4yka.ratatoskr.data.remote.dto.SummaryCompactDto
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.feature.sync.api.SyncEntity
import com.po4yka.ratatoskr.feature.sync.api.SyncItemApplier
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

internal class SummarySyncItemApplier(
    private val database: Database,
) : SyncItemApplier {
    override val entityType: String = SyncEntity.ENTITY_TYPE_SUMMARY

    override fun apply(entity: SyncEntity): Boolean {
        val summary = entity as? SyncEntity.Summary ?: return false
        return try {
            val payload = summary.payload
            if (payload == null) {
                logger.warn { "Summary sync item ${summary.id} has no summary payload, skipping" }
                false
            } else {
                val dto = Json.decodeFromJsonElement(SummaryCompactDto.serializer(), payload)
                database.databaseQueries.insertSummary(dto.toEntity())
                true
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.error(e) { "Failed to process summary sync item ${summary.id}" }
            false
        }
    }
}

internal class HighlightSyncItemApplier(
    private val database: Database,
) : SyncItemApplier {
    override val entityType: String = SyncEntity.ENTITY_TYPE_HIGHLIGHT

    override fun apply(entity: SyncEntity): Boolean {
        val highlight = entity as? SyncEntity.Highlight ?: return false
        return try {
            val payload = highlight.payload
            if (payload == null) {
                logger.warn { "Highlight sync item ${highlight.id} has no highlight payload, skipping" }
                false
            } else {
                val dto = Json.decodeFromJsonElement(HighlightDto.serializer(), payload)
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
            logger.error(e) { "Failed to process highlight sync item ${highlight.id}" }
            false
        }
    }
}
