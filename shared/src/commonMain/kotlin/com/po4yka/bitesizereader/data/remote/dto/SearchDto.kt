package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Search result DTO
 */
@Serializable
data class SearchResultDto(
    val id: Int,
    val title: String,
    val url: String,
    val snippet: String,
    @SerialName("relevance_score") val relevanceScore: Double,
    @SerialName("topic_tags") val topicTags: List<String>
)

/**
 * Search response DTO
 */
@Serializable
data class SearchResponseDto(
    val results: List<SearchResultDto>,
    val total: Int,
    val query: String
)
