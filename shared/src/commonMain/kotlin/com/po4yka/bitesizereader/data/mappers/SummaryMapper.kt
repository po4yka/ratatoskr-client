package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.SummaryCompactDto
import com.po4yka.bitesizereader.database.SummaryEntity
import com.po4yka.bitesizereader.domain.model.Summary
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
