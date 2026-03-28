package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.database.SummaryEntity
import com.po4yka.bitesizereader.domain.model.Summary

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
