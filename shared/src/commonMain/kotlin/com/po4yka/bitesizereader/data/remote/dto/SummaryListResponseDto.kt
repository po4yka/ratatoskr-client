package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SummaryCompactDto(
    @SerialName("id") val id: Long,
    @SerialName("request_id") val requestId: Long,
    @SerialName("title") val title: String = "Untitled",
    @SerialName("domain") val domain: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("tldr") val tldr: String? = null,
    @SerialName("summary_250") val summary250: String? = null,
    @SerialName("reading_time_min") val readingTimeMin: Int? = null,
    @SerialName("topic_tags") val topicTags: List<String> = emptyList(),
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("lang") val lang: String = "auto",
    @SerialName("created_at") val createdAt: String,
    @SerialName("confidence") val confidence: Double? = null,
    @SerialName("hallucination_risk") val hallucinationRisk: String? = null,
)

@Serializable
data class SummaryStatsDto(
    @SerialName("total_summaries") val totalSummaries: Int,
    @SerialName("unread_count") val unreadCount: Int,
)

@Serializable
data class SummaryListDataDto(
    @SerialName("summaries") val summaries: List<SummaryCompactDto>,
    @SerialName("pagination") val pagination: PaginationDto,
    @SerialName("stats") val stats: SummaryStatsDto,
)
