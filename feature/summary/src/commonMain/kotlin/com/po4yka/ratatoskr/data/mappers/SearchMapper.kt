package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.SearchResponseData
import com.po4yka.ratatoskr.domain.model.Summary

fun SearchResponseData.toDomain(): List<Summary> {
    return results.map { result ->
        Summary(
            id = result.summaryId.toString(),
            title = result.title,
            content = result.snippet ?: result.tldr.orEmpty(),
            sourceUrl = result.url,
            imageUrl = null,
            createdAt = result.createdAt,
            isRead = result.isRead ?: false,
            tags = result.topicTags.orEmpty(),
        )
    }
}
