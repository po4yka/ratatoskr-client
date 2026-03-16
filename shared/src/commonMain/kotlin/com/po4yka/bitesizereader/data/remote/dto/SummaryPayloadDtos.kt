package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Typed representation of the summary json_payload from backend.
 * Matches backend's SummaryModel in app/core/summary_schema.py.
 * All fields are nullable/defaulted for backward compatibility.
 */
@Serializable
data class SummaryPayloadDto(
    @SerialName("summary_250") val summary250: String = "",
    @SerialName("summary_1000") val summary1000: String = "",
    @SerialName("tldr") val tldr: String = "",
    @SerialName("key_ideas") val keyIdeas: List<String> = emptyList(),
    @SerialName("topic_tags") val topicTags: List<String> = emptyList(),
    @SerialName("entities") val entities: EntitiesDto? = null,
    @SerialName("estimated_reading_time_min") val estimatedReadingTimeMin: Int = 0,
    @SerialName("key_stats") val keyStats: List<KeyStatDto> = emptyList(),
    @SerialName("answered_questions") val answeredQuestions: List<String> = emptyList(),
    @SerialName("seo_keywords") val seoKeywords: List<String> = emptyList(),
    @SerialName("query_expansion_keywords") val queryExpansionKeywords: List<String> = emptyList(),
    @SerialName("highlights") val highlights: List<String> = emptyList(),
    @SerialName("key_points_to_remember") val keyPointsToRemember: List<String> = emptyList(),
    @SerialName("extractive_quotes") val extractiveQuotes: List<ExtractiveQuoteDto> = emptyList(),
    @SerialName("source_type") val sourceType: String? = null,
    @SerialName("temporal_freshness") val temporalFreshness: String? = null,
    @SerialName("hallucination_risk") val hallucinationRisk: String? = null,
    @SerialName("confidence") val confidence: Double? = null,
    @SerialName("quality") val quality: QualityAssessmentDto? = null,
    @SerialName("insights") val insights: InsightsDto? = null,
    @SerialName("metadata") val metadata: SummaryMetadataDto? = null,
)

@Serializable
data class QualityAssessmentDto(
    @SerialName("author_bias") val authorBias: String? = null,
    @SerialName("emotional_tone") val emotionalTone: String? = null,
    @SerialName("missing_perspectives") val missingPerspectives: List<String> = emptyList(),
    @SerialName("evidence_quality") val evidenceQuality: String? = null,
)

@Serializable
data class InsightsDto(
    @SerialName("topic_overview") val topicOverview: String? = null,
    @SerialName("new_facts") val newFacts: List<InsightFactDto> = emptyList(),
    @SerialName("open_questions") val openQuestions: List<String> = emptyList(),
    @SerialName("suggested_sources") val suggestedSources: List<String> = emptyList(),
    @SerialName("expansion_topics") val expansionTopics: List<String> = emptyList(),
    @SerialName("caution") val caution: String? = null,
    @SerialName("critique") val critique: List<String> = emptyList(),
)

@Serializable
data class InsightFactDto(
    @SerialName("fact") val fact: String,
    @SerialName("why_it_matters") val whyItMatters: String? = null,
    @SerialName("source_hint") val sourceHint: String? = null,
    @SerialName("confidence") val confidence: Double? = null,
)

@Serializable
data class SummaryMetadataDto(
    @SerialName("title") val title: String? = null,
    @SerialName("canonical_url") val canonicalUrl: String? = null,
    @SerialName("domain") val domain: String? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("last_updated") val lastUpdated: String? = null,
)

@Serializable
data class EntitiesDto(
    @SerialName("people") val people: List<String> = emptyList(),
    @SerialName("organizations") val organizations: List<String> = emptyList(),
    @SerialName("locations") val locations: List<String> = emptyList(),
)

@Serializable
data class ExtractiveQuoteDto(
    @SerialName("text") val text: String,
    @SerialName("source_span") val sourceSpan: String? = null,
)

@Serializable
data class KeyStatDto(
    @SerialName("label") val label: String,
    @SerialName("value") val value: Double,
    @SerialName("unit") val unit: String? = null,
    @SerialName("source_excerpt") val sourceExcerpt: String? = null,
)
