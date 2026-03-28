package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.SummaryCompactDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryDetailDataDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryPayloadDto
import com.po4yka.bitesizereader.database.SummaryEntity
import com.po4yka.bitesizereader.domain.model.InsightFact
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.model.SummaryInsights
import com.po4yka.bitesizereader.domain.model.SummaryQuality
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

private val lenientJson = Json { ignoreUnknownKeys = true }

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
        hallucinationRisk = hallucinationRisk,
        confidence = confidence,
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
    val payload = s.jsonPayload?.parsePayload()
    return Summary(
        id = s.id.toString(),
        title = payload?.metadata?.title ?: source?.title ?: "Untitled",
        content = payload?.summary250 ?: payload?.tldr.orEmpty(),
        sourceUrl = payload?.metadata?.canonicalUrl ?: source?.url.orEmpty(),
        imageUrl = source?.imageUrl,
        createdAt = Instant.parse(s.createdAt),
        isRead = s.isRead,
        tags = payload?.topicTags.orEmpty(),
        readingTimeMin = payload?.estimatedReadingTimeMin,
        isFavorited = s.isFavorited,
        sourceType = payload?.sourceType,
        temporalFreshness = payload?.temporalFreshness,
        hallucinationRisk = payload?.hallucinationRisk,
        confidence = payload?.confidence,
        quality = payload?.quality?.toDomain(),
        insights = payload?.insights?.toDomain(),
    )
}

private fun JsonElement.parsePayload(): SummaryPayloadDto? {
    return runCatching {
        lenientJson.decodeFromJsonElement(SummaryPayloadDto.serializer(), this)
    }.getOrNull()
}

private fun com.po4yka.bitesizereader.data.remote.dto.QualityAssessmentDto.toDomain(): SummaryQuality {
    return SummaryQuality(
        authorBias = authorBias,
        emotionalTone = emotionalTone,
        missingPerspectives = missingPerspectives,
        evidenceQuality = evidenceQuality,
    )
}

private fun com.po4yka.bitesizereader.data.remote.dto.InsightsDto.toDomain(): SummaryInsights {
    return SummaryInsights(
        topicOverview = topicOverview,
        caution = caution,
        critique = critique,
        newFacts = newFacts.map { it.toDomain() },
        openQuestions = openQuestions,
    )
}

private fun com.po4yka.bitesizereader.data.remote.dto.InsightFactDto.toDomain(): InsightFact {
    return InsightFact(
        fact = fact,
        whyItMatters = whyItMatters,
        confidence = confidence,
    )
}
