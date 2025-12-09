package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.SummaryCompactDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSummaryDataDto
import com.po4yka.bitesizereader.database.SummaryEntity
import com.po4yka.bitesizereader.domain.model.Summary
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Clock
import kotlin.time.Instant

fun SummaryCompactDto.toDomain(): Summary {
    return Summary(
        id = id.toString(),
        title = title,
        content = summary250 ?: tldr.orEmpty(),
        sourceUrl = url,
        imageUrl = null,
        createdAt = Instant.parse(createdAt),
        isRead = isRead,
        tags = topicTags,
    )
}

fun SummaryEntity.toDomain(): Summary {
    return Summary(
        id = id,
        title = title,
        content = content,
        sourceUrl = sourceUrl,
        imageUrl = imageUrl,
        createdAt = createdAt,
        isRead = isRead,
        tags = tags,
    )
}

fun SummaryCompactDto.toEntity(isReadOverride: Boolean? = null): SummaryEntity {
    return SummaryEntity(
        id = id.toString(),
        title = title,
        content = summary250 ?: tldr.orEmpty(),
        sourceUrl = url,
        imageUrl = null,
        createdAt = Instant.parse(createdAt),
        isRead = isReadOverride ?: isRead,
        tags = topicTags,
    )
}

fun SyncSummaryDataDto.toEntity(
    isReadOverride: Boolean? = null,
    createdAt: String? = null,
): SummaryEntity {
    val createdAtValue =
        createdAt
            ?: jsonPayload?.stringValue("created_at")
            ?: createdAtFallback()
    val tagsValue = jsonPayload?.get("topic_tags")
    val tags =
        when (tagsValue) {
            is JsonElement -> tagsValue.jsonArray.mapNotNull { it.jsonPrimitive.contentOrNull }
            else -> emptyList()
        }
    return SummaryEntity(
        id = id.toString(),
        title = jsonPayload?.stringValue("title") ?: "Untitled",
        content = jsonPayload?.stringValue("summary_250") ?: "",
        sourceUrl = jsonPayload?.stringValue("url") ?: "",
        imageUrl = null,
        createdAt = Instant.parse(createdAtValue),
        isRead = isReadOverride ?: isRead,
        tags = tags,
    )
}

private fun SyncSummaryDataDto.createdAtFallback(): String = Clock.System.now().toString()

private fun Map<String, JsonElement>.stringValue(key: String): String? = (this[key] as? JsonPrimitive)?.contentOrNull

fun Summary.toEntity(): SummaryEntity {
    return SummaryEntity(
        id = id,
        title = title,
        content = content,
        sourceUrl = sourceUrl,
        imageUrl = imageUrl,
        createdAt = createdAt,
        isRead = isRead,
        tags = tags,
    )
}
