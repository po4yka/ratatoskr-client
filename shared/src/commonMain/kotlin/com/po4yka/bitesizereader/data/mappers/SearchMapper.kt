package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.SearchResultDto
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.model.SyncStatus
import kotlinx.datetime.Clock

/**
 * Maps Search DTOs to domain models
 */

fun SearchResultDto.toSummary(): Summary {
    return Summary(
        id = id,
        requestId = 0, // Not provided in search results
        title = title,
        url = url,
        domain = null,
        tldr = snippet,
        summary250 = snippet,
        summary1000 = null,
        keyIdeas = emptyList(),
        topicTags = topicTags,
        answeredQuestions = emptyList(),
        seoKeywords = emptyList(),
        readingTimeMin = 0,
        lang = "en",
        entities = null,
        keyStats = emptyList(),
        readability = null,
        isRead = false,
        createdAt = Clock.System.now(),
        syncStatus = SyncStatus.SYNCED,
    )
}

fun List<SearchResultDto>.toSummaries(): List<Summary> {
    return map { it.toSummary() }
}
