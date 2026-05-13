package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.SyncApplyItem
import com.po4yka.ratatoskr.api.generated.models.SyncApplyRequest
import com.po4yka.ratatoskr.database.SummaryEntity
import com.po4yka.ratatoskr.feature.sync.domain.repository.LocalChange
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Clock
import kotlin.time.Instant

private val logger = KotlinLogging.logger {}

/**
 * Converts a sync summary JSON payload into a [SummaryEntity].
 *
 * Server sends summary data in this structure:
 * ```json
 * {
 *   "is_read": false,
 *   "is_favorited": true,
 *   "is_archived": false,
 *   "last_read_position": 42,
 *   "last_read_offset": 100,
 *   "json_payload": {
 *     "summary_1000": "Full summary text...",
 *     "topic_tags": ["#tag1", "#tag2"],
 *     "metadata": {
 *       "title": "Article Title",
 *       "canonical_url": "https://...",
 *       "domain": "example.com",
 *       "image_url": "https://..."
 *     }
 *   },
 *   "created_at": "2024-12-14T10:20:00Z"
 * }
 * ```
 *
 * @return [SummaryEntity] or null if parsing fails
 */
fun JsonObject.toSummaryEntity(id: Long): SummaryEntity? {
    return try {
        // Extract top-level fields
        val isRead = this["is_read"]?.jsonPrimitive?.booleanOrNull ?: false
        val isFavorited = this["is_favorited"]?.jsonPrimitive?.booleanOrNull ?: false
        val isArchived = this["is_archived"]?.jsonPrimitive?.booleanOrNull ?: false
        val lastReadPosition =
            this["last_read_position"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0
        val lastReadOffset =
            this["last_read_offset"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0
        val createdAtStr = this["created_at"]?.jsonPrimitive?.contentOrNull
        val jsonPayload = this["json_payload"]?.jsonObject

        // Parse created_at timestamp
        val createdAt =
            createdAtStr?.let {
                try {
                    Instant.parse(it)
                } catch (_: Exception) {
                    logger.warn { "Failed to parse created_at for item $id: $it" }
                    Clock.System.now()
                }
            } ?: run {
                logger.warn { "Missing created_at for item $id, using current time" }
                Clock.System.now()
            }

        // Extract from json_payload
        val metadata = jsonPayload?.get("metadata")?.jsonObject
        val title =
            metadata?.get("title")?.jsonPrimitive?.contentOrNull
                ?: "Untitled Summary"
        val sourceUrl =
            metadata?.get("canonical_url")?.jsonPrimitive?.contentOrNull
                ?: ""
        val content =
            jsonPayload?.get("summary_1000")?.jsonPrimitive?.contentOrNull
                ?: jsonPayload?.get("summary_250")?.jsonPrimitive?.contentOrNull
                ?: ""

        // Extract tags from topic_tags (they come as ["#tag1", "#tag2"])
        val topicTags = jsonPayload?.get("topic_tags")?.jsonArray
        val tags =
            topicTags?.mapNotNull { tag ->
                tag.jsonPrimitive.contentOrNull?.removePrefix("#")
            } ?: emptyList()

        // Parse imageUrl from metadata if available
        val imageUrl: String? = metadata?.get("image_url")?.jsonPrimitive?.contentOrNull

        // Estimate reading time from content length (~200 words/min, ~5 chars/word)
        val readingTimeMin: Int? =
            if (content.isNotEmpty()) {
                (content.length / 5 / 200).coerceAtLeast(1)
            } else {
                null
            }

        logger.debug {
            "Mapping summary $id: title='$title', sourceUrl='$sourceUrl', " +
                "contentLength=${content.length}, tags=$tags, isRead=$isRead"
        }

        SummaryEntity(
            id = id.toString(),
            title = title,
            content = content,
            sourceUrl = sourceUrl,
            imageUrl = imageUrl,
            createdAt = createdAt,
            isRead = isRead,
            tags = tags,
            readingTimeMin = readingTimeMin,
            isFavorited = isFavorited,
            fullContent = null,
            fullContentCachedAt = null,
            lastReadPosition = lastReadPosition,
            lastReadOffset = lastReadOffset,
            isArchived = isArchived,
        )
    } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
        logger.error(e) { "Failed to map sync summary item $id" }
        null
    }
}

/**
 * Maps a [LocalChange] to a generated [SyncApplyItem]. The generated DTO uses
 * `String` ids (the backend accepts `int | string`), so the numeric local id is
 * stringified here.
 */
fun LocalChange.toGeneratedApplyItem(): SyncApplyItem =
    SyncApplyItem(
        entityType = entityType.toGeneratedEntityType(),
        id = id.toString(),
        action = action.toGeneratedAction(),
        lastSeenVersion = lastSeenVersion,
        payload = payload?.let { map -> JsonObject(map.mapValues { (_, value) -> value.toJsonPrimitive() }) },
        clientTimestamp = clientTimestamp,
    )

/**
 * Builds a generated [SyncApplyRequest] body from a session id and a list of
 * local changes.
 */
fun buildSyncApplyRequest(
    sessionId: String,
    changes: List<LocalChange>,
): SyncApplyRequest =
    SyncApplyRequest(
        sessionId = sessionId,
        changes = changes.map { it.toGeneratedApplyItem() },
    )

private fun Any?.toJsonPrimitive(): JsonPrimitive =
    when (this) {
        is String -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        else -> JsonPrimitive(this?.toString())
    }

private fun String.toGeneratedEntityType(): SyncApplyItem.EntityType =
    when (this) {
        "summary" -> SyncApplyItem.EntityType.SUMMARY
        "request" -> SyncApplyItem.EntityType.REQUEST
        "preference" -> SyncApplyItem.EntityType.PREFERENCE
        "stat" -> SyncApplyItem.EntityType.STAT
        "crawl_result" -> SyncApplyItem.EntityType.CRAWL_RESULT
        "llm_call" -> SyncApplyItem.EntityType.LLM_CALL
        else -> throw IllegalArgumentException("Unsupported sync apply entity type: $this")
    }

private fun String.toGeneratedAction(): SyncApplyItem.Action =
    when (this.lowercase()) {
        "update" -> SyncApplyItem.Action.UPDATE
        "delete" -> SyncApplyItem.Action.DELETE
        else -> throw IllegalArgumentException("Unsupported sync apply action: $this")
    }
