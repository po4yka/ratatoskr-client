package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    @SerialName("request_id") val requestId: Long,
    @SerialName("summary_id") val summaryId: Long,
    @SerialName("url") val url: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("domain") val domain: String? = null,
    @SerialName("snippet") val snippet: String? = null,
    @SerialName("tldr") val tldr: String? = null,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("relevance_score") val relevanceScore: Double? = null,
    @SerialName("topic_tags") val topicTags: List<String> = emptyList(),
    @SerialName("is_read") val isRead: Boolean = false
)

@Serializable
data class SearchResponseDataDto(
    @SerialName("results") val results: List<SearchResultDto>,
    @SerialName("pagination") val pagination: PaginationDto,
    @SerialName("query") val query: String
)

@Serializable
data class TrendingTopicDto(
    @SerialName("tag") val tag: String,
    @SerialName("count") val count: Int
)

@Serializable
data class TrendingTopicsResponseDto(
    @SerialName("topics") val topics: List<TrendingTopicDto>,
    @SerialName("total") val total: Int
)

@Serializable
data class RelatedSummaryDto(
    @SerialName("summary_id") val summaryId: Long,
    @SerialName("title") val title: String,
    @SerialName("tldr") val tldr: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class RelatedSummariesResponseDto(
    @SerialName("tag") val tag: String,
    @SerialName("summaries") val summaries: List<RelatedSummaryDto>,
    @SerialName("pagination") val pagination: PaginationDto
)
