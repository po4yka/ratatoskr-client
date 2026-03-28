package com.po4yka.bitesizereader.domain.model

import kotlin.time.Instant

data class Summary(
    val id: String,
    val title: String,
    val content: String,
    val sourceUrl: String,
    val imageUrl: String?,
    val createdAt: Instant,
    val isRead: Boolean,
    val tags: List<String>,
    val readingTimeMin: Int? = null,
    val isFavorited: Boolean = false,
    val fullContent: String? = null,
    val isFullContentCached: Boolean = false,
    val lastReadPosition: Int = 0,
    val lastReadOffset: Int = 0,
    val isArchived: Boolean = false,
    // Enhanced schema fields from backend json_payload
    val sourceType: String? = null,
    val temporalFreshness: String? = null,
    val hallucinationRisk: String? = null,
    val confidence: Double? = null,
    val quality: SummaryQuality? = null,
    val insights: SummaryInsights? = null,
)

data class SummaryQuality(
    val authorBias: String? = null,
    val emotionalTone: String? = null,
    val missingPerspectives: List<String> = emptyList(),
    val evidenceQuality: String? = null,
)

data class SummaryInsights(
    val topicOverview: String? = null,
    val caution: String? = null,
    val critique: List<String> = emptyList(),
    val newFacts: List<InsightFact> = emptyList(),
    val openQuestions: List<String> = emptyList(),
)

data class InsightFact(
    val fact: String,
    val whyItMatters: String? = null,
    val confidence: Double? = null,
)
