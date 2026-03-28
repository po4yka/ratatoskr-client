package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SummaryCompactDto(
    @SerialName("id") val id: Long,
    @SerialName("requestId") val requestId: Long,
    @SerialName("title") val title: String = "Untitled",
    @SerialName("domain") val domain: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("tldr") val tldr: String? = null,
    @SerialName("summary250") val summary250: String? = null,
    @SerialName("readingTimeMin") val readingTimeMin: Int? = null,
    @SerialName("topicTags") val topicTags: List<String> = emptyList(),
    @SerialName("isRead") val isRead: Boolean = false,
    @SerialName("lang") val lang: String = "auto",
    @SerialName("createdAt") val createdAt: String,
    @SerialName("confidence") val confidence: Double? = null,
    @SerialName("hallucinationRisk") val hallucinationRisk: String? = null,
    @SerialName("isFavorited") val isFavorited: Boolean = false,
    @SerialName("imageUrl") val imageUrl: String? = null,
)

@Serializable
data class SummaryStatsDto(
    @SerialName("totalSummaries") val totalSummaries: Int,
    @SerialName("unreadCount") val unreadCount: Int,
)

@Serializable
data class SummaryListDataDto(
    @SerialName("items") val items: List<SummaryCompactDto>,
    @SerialName("pagination") val pagination: PaginationDto,
    @SerialName("stats") val stats: SummaryStatsDto? = null,
)
