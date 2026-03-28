package com.po4yka.bitesizereader.feature.collections.data.sync

import com.po4yka.bitesizereader.data.remote.dto.SyncItemDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSummaryTagDto
import com.po4yka.bitesizereader.data.remote.dto.SyncTagDto
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.feature.sync.api.SyncItemApplier
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

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
                val dto = Json.decodeFromJsonElement(SyncTagDto.serializer(), tagData)
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
                val dto = Json.decodeFromJsonElement(SyncSummaryTagDto.serializer(), summaryTagData)
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
