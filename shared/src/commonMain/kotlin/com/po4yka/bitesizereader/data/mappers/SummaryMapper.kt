package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.*
import com.po4yka.bitesizereader.domain.model.*
import kotlinx.datetime.Instant

/**
 * Maps Summary DTOs to domain models and vice versa
 */

fun SummaryCompactDto.toDomain(): Summary {
    return Summary(
        id = id,
        requestId = requestId,
        title = title,
        url = url,
        domain = domain,
        tldr = tldr,
        summary250 = summary250,
        summary1000 = null,
        keyIdeas = emptyList(),
        topicTags = topicTags,
        answeredQuestions = emptyList(),
        seoKeywords = emptyList(),
        readingTimeMin = readingTimeMin,
        lang = lang,
        entities = null,
        keyStats = emptyList(),
        readability = null,
        isRead = isRead,
        createdAt = Instant.parse(createdAt),
        updatedAt = null
    )
}

fun SummaryDetailDto.toDomain(): Summary {
    return Summary(
        id = id,
        requestId = requestId,
        title = title,
        url = url,
        domain = domain,
        tldr = tldr,
        summary250 = summary250,
        summary1000 = summary1000,
        keyIdeas = keyIdeas,
        topicTags = topicTags,
        answeredQuestions = answeredQuestions,
        seoKeywords = seoKeywords,
        readingTimeMin = readingTimeMin,
        lang = lang,
        entities = entities?.toDomain(),
        keyStats = keyStats.map { it.toDomain() },
        readability = readability?.toDomain(),
        isRead = isRead,
        createdAt = Instant.parse(createdAt),
        updatedAt = updatedAt?.let { Instant.parse(it) }
    )
}

fun EntitiesDto.toDomain(): Entities {
    return Entities(
        people = people,
        organizations = organizations,
        locations = locations
    )
}

fun KeyStatDto.toDomain(): KeyStat {
    return KeyStat(
        label = label,
        value = value,
        unit = unit,
        sourceExcerpt = sourceExcerpt
    )
}

fun ReadabilityDto.toDomain(): Readability {
    return Readability(
        method = method,
        score = score,
        level = level
    )
}

// Domain to DTO conversions (for upload)

fun Summary.toUpdateRequestDto(): SummaryUpdateRequestDto {
    return SummaryUpdateRequestDto(
        isRead = isRead
    )
}

// Batch mapping extensions

fun List<SummaryCompactDto>.toDomain(): List<Summary> {
    return map { it.toDomain() }
}

fun List<SummaryDetailDto>.toDomainDetailed(): List<Summary> {
    return map { it.toDomain() }
}
