package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.SearchResponseDataDto
import com.po4yka.bitesizereader.domain.model.Summary
import kotlin.time.Instant

fun SearchResponseDataDto.toDomain(): List<Summary> {
    return results.map { result ->
        Summary(
            id = result.summaryId.toString(),
            title = result.title ?: "Untitled",
            content = result.snippet ?: result.tldr.orEmpty(),
            sourceUrl = result.url ?: "",
            imageUrl = null,
            createdAt = result.createdAt?.let { Instant.parse(it) } ?: Instant.DISTANT_PAST,
            isRead = result.isRead,
            tags = result.topicTags,
        )
    }
}
