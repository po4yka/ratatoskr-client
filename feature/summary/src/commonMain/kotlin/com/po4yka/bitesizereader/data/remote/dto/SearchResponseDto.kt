package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    @SerialName("requestId") val requestId: Long,
    @SerialName("summaryId") val summaryId: Long,
    @SerialName("url") val url: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("domain") val domain: String? = null,
    @SerialName("snippet") val snippet: String? = null,
    @SerialName("tldr") val tldr: String? = null,
    @SerialName("publishedAt") val publishedAt: String? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("relevanceScore") val relevanceScore: Double? = null,
    @SerialName("topicTags") val topicTags: List<String> = emptyList(),
    @SerialName("isRead") val isRead: Boolean = false,
)

@Serializable
data class SearchResponseDataDto(
    @SerialName("results") val results: List<SearchResultDto>,
    @SerialName("pagination") val pagination: PaginationDto,
    @SerialName("query") val query: String,
)

@Serializable
data class TrendingTopicDto(
    @SerialName("tag") val tag: String,
    @SerialName("count") val count: Int,
    @SerialName("trend") val trend: String? = null,
    @SerialName("percentageChange") val percentageChange: Double? = null,
)

@Serializable
data class TrendingTopicsDataDto(
    @SerialName("tags") val tags: List<TrendingTopicDto>,
    @SerialName("timeRange") val timeRange: TimeRangeDto? = null,
)

@Serializable
data class TimeRangeDto(
    @SerialName("start") val start: String,
    @SerialName("end") val end: String,
)

@Serializable
data class RelatedSummaryDto(
    @SerialName("summaryId") val summaryId: Long,
    @SerialName("title") val title: String,
    @SerialName("tldr") val tldr: String,
    @SerialName("createdAt") val createdAt: String,
)

@Serializable
data class RelatedSummariesResponseDto(
    @SerialName("tag") val tag: String,
    @SerialName("summaries") val summaries: List<RelatedSummaryDto>,
    @SerialName("pagination") val pagination: PaginationDto,
)
