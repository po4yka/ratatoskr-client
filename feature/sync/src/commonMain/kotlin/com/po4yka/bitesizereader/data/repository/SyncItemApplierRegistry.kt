package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toSummaryEntity
import com.po4yka.bitesizereader.data.remote.dto.HighlightDto
import com.po4yka.bitesizereader.data.remote.dto.SyncItemDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSummaryTagDto
import com.po4yka.bitesizereader.data.remote.dto.SyncTagDto
import com.po4yka.bitesizereader.database.Database
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class SyncItemApplierRegistry(
    appliers: List<SyncItemApplier>,
) {
    private val appliersByEntityType = appliers.associateBy { it.entityType }

    fun apply(item: SyncItemDto): Boolean {
        val applier = appliersByEntityType[item.entityType]
        if (applier == null) {
            logger.debug { "Skipping entity type not needed on mobile: ${item.entityType}" }
            return true
        }
        return applier.apply(item)
    }
}

internal interface SyncItemApplier {
    val entityType: String

    fun apply(item: SyncItemDto): Boolean
}

internal class SummarySyncItemApplier(
    private val database: Database,
) : SyncItemApplier {
    override val entityType: String = "summary"

    override fun apply(item: SyncItemDto): Boolean {
        val entity = item.summary?.toSummaryEntity(item.idAsLong ?: 0L) ?: return false
        database.databaseQueries.insertSummary(entity)
        return true
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
                val dto =
                    kotlinx.serialization.json.Json.decodeFromJsonElement(
                        HighlightDto.serializer(),
                        highlightData,
                    )
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

internal class TagSyncItemApplier(
    private val database: Database,
) : SyncItemApplier {
    override val entityType: String = "tag"

    override fun apply(item: SyncItemDto): Boolean =
        try {
            val tagData = item.tag
            if (tagData == null) {
                logger.warn { "Tag sync item ${item.idAsString} has no tag payload, skipping" }
                false
            } else {
                val dto =
                    kotlinx.serialization.json.Json.decodeFromJsonElement(
                        SyncTagDto.serializer(),
                        tagData,
                    )
                if (!dto.isDeleted) {
                    database.databaseQueries.insertOrReplaceTag(
                        id = dto.id.toLong(),
                        name = dto.name,
                        color = dto.color,
                        summaryCount = 0,
                        createdAt = dto.createdAt,
                        updatedAt = dto.updatedAt,
                        syncStatus = "synced",
                    )
                }
                true
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.error(e) { "Failed to process tag sync item ${item.idAsString}" }
            false
        }
}

internal class SummaryTagSyncItemApplier(
    private val database: Database,
) : SyncItemApplier {
    override val entityType: String = "summary_tag"

    override fun apply(item: SyncItemDto): Boolean =
        try {
            val summaryTagData = item.summaryTag
            if (summaryTagData == null) {
                logger.warn { "SummaryTag sync item ${item.idAsString} has no payload, skipping" }
                false
            } else {
                val dto =
                    kotlinx.serialization.json.Json.decodeFromJsonElement(
                        SyncSummaryTagDto.serializer(),
                        summaryTagData,
                    )
                database.databaseQueries.insertSummaryTag(
                    summaryId = dto.summaryId.toString(),
                    tagId = dto.tagId.toLong(),
                )
                true
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.error(e) { "Failed to process summary_tag sync item ${item.idAsString}" }
            false
        }
}
