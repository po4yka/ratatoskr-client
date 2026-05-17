package com.po4yka.ratatoskr.feature.collections.data.sync

import com.po4yka.ratatoskr.data.remote.dto.SyncSummaryTagDto
import com.po4yka.ratatoskr.data.remote.dto.SyncTagDto
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.feature.sync.api.SyncEntity
import com.po4yka.ratatoskr.feature.sync.api.SyncItemApplier
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

internal class TagSyncItemApplier(
    private val database: Database,
) : SyncItemApplier {
    override val entityType: String = SyncEntity.ENTITY_TYPE_TAG

    override fun apply(entity: SyncEntity): Boolean {
        val tag = entity as? SyncEntity.Tag ?: return false
        return try {
            val payload = tag.payload
            if (payload == null) {
                logger.warn { "Tag sync item ${tag.id} has no tag payload, skipping" }
                false
            } else {
                val dto = Json.decodeFromJsonElement(SyncTagDto.serializer(), payload)
                if (dto.isDeleted) {
                    database.databaseQueries.deleteTag(dto.id.toLong())
                } else {
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
            logger.error(e) { "Failed to process tag sync item ${tag.id}" }
            false
        }
    }
}

internal class SummaryTagSyncItemApplier(
    private val database: Database,
) : SyncItemApplier {
    override val entityType: String = SyncEntity.ENTITY_TYPE_SUMMARY_TAG

    override fun apply(entity: SyncEntity): Boolean {
        val summaryTag = entity as? SyncEntity.SummaryTag ?: return false
        return try {
            val payload = summaryTag.payload
            if (payload == null) {
                logger.warn { "SummaryTag sync item ${summaryTag.id} has no payload, skipping" }
                false
            } else {
                val dto = Json.decodeFromJsonElement(SyncSummaryTagDto.serializer(), payload)
                database.databaseQueries.insertSummaryTag(
                    summaryId = dto.summaryId.toString(),
                    tagId = dto.tagId.toLong(),
                )
                true
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.error(e) { "Failed to process summary_tag sync item ${summaryTag.id}" }
            false
        }
    }
}
