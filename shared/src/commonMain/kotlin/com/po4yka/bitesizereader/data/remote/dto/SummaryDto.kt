package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Compact summary DTO for list views
 */
@Serializable
data class SummaryCompactDto(
    val id: Int,
    @SerialName("request_id") val requestId: Int,
    val title: String,
    val domain: String? = null,
    val url: String,
    val tldr: String,
    @SerialName("summary_250") val summary250: String,
    @SerialName("reading_time_min") val readingTimeMin: Int,
    @SerialName("topic_tags") val topicTags: List<String>,
    @SerialName("is_read") val isRead: Boolean,
    val lang: String,
    @SerialName("created_at") val createdAt: String,
)

/**
 * Detailed summary DTO
 */
@Serializable
data class SummaryDetailDto(
    val id: Int,
    @SerialName("request_id") val requestId: Int,
    val title: String,
    val domain: String? = null,
    val url: String,
    @SerialName("summary_250") val summary250: String,
    @SerialName("summary_1000") val summary1000: String? = null,
    val tldr: String,
    @SerialName("key_ideas") val keyIdeas: List<String>,
    @SerialName("topic_tags") val topicTags: List<String>,
    val entities: EntitiesDto? = null,
    @SerialName("estimated_reading_time_min") val readingTimeMin: Int,
    @SerialName("key_stats") val keyStats: List<KeyStatDto>,
    @SerialName("answered_questions") val answeredQuestions: List<String>,
    val readability: ReadabilityDto? = null,
    @SerialName("seo_keywords") val seoKeywords: List<String>,
    @SerialName("is_read") val isRead: Boolean,
    val lang: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String? = null,
)

/**
 * Entities DTO
 */
@Serializable
data class EntitiesDto(
    val people: List<String> = emptyList(),
    val organizations: List<String> = emptyList(),
    val locations: List<String> = emptyList(),
)

/**
 * Key statistic DTO
 */
@Serializable
data class KeyStatDto(
    val label: String,
    val value: Double,
    val unit: String? = null,
    @SerialName("source_excerpt") val sourceExcerpt: String? = null,
)

/**
 * Readability DTO
 */
@Serializable
data class ReadabilityDto(
    val method: String,
    val score: Double,
    val level: String,
)

/**
 * Pagination info DTO
 */
@Serializable
data class PaginationInfoDto(
    val total: Int,
    val limit: Int,
    val offset: Int,
    @SerialName("has_more") val hasMore: Boolean,
)

/**
 * Summary list response DTO
 */
@Serializable
data class SummaryListResponseDto(
    val summaries: List<SummaryCompactDto>,
    val pagination: PaginationInfoDto,
)

/**
 * Summary update request DTO
 */
@Serializable
data class SummaryUpdateRequestDto(
    @SerialName("is_read") val isRead: Boolean? = null,
)

/**
 * Summary update response DTO
 */
@Serializable
data class SummaryUpdateResponseDto(
    val id: Int,
    @SerialName("is_read") val isRead: Boolean,
    @SerialName("updated_at") val updatedAt: String,
)
