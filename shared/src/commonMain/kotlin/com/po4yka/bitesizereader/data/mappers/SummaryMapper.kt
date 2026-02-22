package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.SummaryCompactDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryDetailDataDto
import com.po4yka.bitesizereader.database.SummaryEntity
import com.po4yka.bitesizereader.domain.model.Summary
import kotlin.time.Instant

fun SummaryCompactDto.toDomain(): Summary {
    return Summary(
        id = id.toString(),
        title = title,
        content = summary250 ?: tldr.orEmpty(),
        sourceUrl = url,
        imageUrl = imageUrl,
        createdAt = Instant.parse(createdAt),
        isRead = isRead,
        tags = topicTags,
        readingTimeMin = readingTimeMin,
        isFavorited = isFavorited,
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
        readingTimeMin = readingTimeMin,
        isFavorited = isFavorited,
        fullContent = fullContent,
        isFullContentCached = fullContent != null,
        lastReadPosition = lastReadPosition,
        lastReadOffset = lastReadOffset,
        isArchived = isArchived,
    )
}

fun SummaryCompactDto.toEntity(isReadOverride: Boolean? = null): SummaryEntity {
    return SummaryEntity(
        id = id.toString(),
        title = title,
        content = summary250 ?: tldr.orEmpty(),
        sourceUrl = url,
        imageUrl = imageUrl,
        createdAt = Instant.parse(createdAt),
        isRead = isReadOverride ?: isRead,
        tags = topicTags,
        readingTimeMin = readingTimeMin,
        isFavorited = isFavorited,
        fullContent = null,
        fullContentCachedAt = null,
        lastReadPosition = 0,
        lastReadOffset = 0,
        isArchived = false,
    )
}

fun SummaryDetailDataDto.toDomain(): Summary {
    val s = summary
    return Summary(
        id = s.id.toString(),
        title = source?.title ?: "Untitled",
        content = "",
        sourceUrl = source?.url.orEmpty(),
        imageUrl = source?.imageUrl,
        createdAt = Instant.parse(s.createdAt),
        isRead = s.isRead,
        tags = emptyList(),
        isFavorited = s.isFavorited,
    )
}

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
        readingTimeMin = readingTimeMin,
        isFavorited = isFavorited,
        fullContent = fullContent,
        fullContentCachedAt = null,
        lastReadPosition = lastReadPosition,
        lastReadOffset = lastReadOffset,
        isArchived = isArchived,
    )
}
