package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.SummaryDto
import com.po4yka.bitesizereader.database.SummaryEntity
import com.po4yka.bitesizereader.domain.model.Summary
import kotlin.time.Instant

fun SummaryDto.toDomain(): Summary {
    return Summary(
        id = id,
        title = title,
        content = content,
        sourceUrl = sourceUrl,
        imageUrl = imageUrl,
        createdAt = Instant.parse(createdAt),
        isRead = false,
        tags = tags
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
        tags = tags
    )
}

fun SummaryDto.toEntity(isRead: Boolean = false): SummaryEntity {
    return SummaryEntity(
        id = id,
        title = title,
        content = content,
        sourceUrl = sourceUrl,
        imageUrl = imageUrl,
        createdAt = Instant.parse(createdAt),
        isRead = isRead,
        tags = tags
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
        tags = tags
    )
}
