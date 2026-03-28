package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.SyncApplyAction
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyItemDto
import com.po4yka.bitesizereader.database.SummaryEntity
import com.po4yka.bitesizereader.domain.repository.LocalChange
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
 * Converts a [LocalChange] to a [SyncApplyItemDto] for the sync API.
 */
fun LocalChange.toDto(): SyncApplyItemDto {
    val jsonPayload =
        payload?.let { map ->
            JsonObject(
                map.mapValues { (_, value) ->
                    when (value) {
                        is String -> JsonPrimitive(value)
                        is Number -> JsonPrimitive(value)
                        is Boolean -> JsonPrimitive(value)
                        else -> JsonPrimitive(value?.toString())
                    }
                },
            )
        }

    return SyncApplyItemDto(
        entityType = entityType,
        id = id,
        action = SyncApplyAction.fromString(action),
        lastSeenVersion = lastSeenVersion,
        payload = jsonPayload,
        clientTimestamp = clientTimestamp,
    )
}
