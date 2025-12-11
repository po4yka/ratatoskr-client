package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User statistics matching OpenAPI UserStats schema.
 * Provides insights into user's reading activity and preferences.
 */
@Serializable
data class UserStatsDto(
    /** Total number of summaries */
    @SerialName("total_summaries") val totalSummaries: Int,
    /** Number of unread summaries */
    @SerialName("unread_count") val unreadCount: Int,
    /** Number of read summaries */
    @SerialName("read_count") val readCount: Int,
    /** Total reading time in minutes */
    @SerialName("total_reading_time_min") val totalReadingTimeMin: Int? = null,
    /** Average reading time per summary in minutes */
    @SerialName("average_reading_time_min") val averageReadingTimeMin: Float? = null,
    /** Most common topics in user's summaries */
    @SerialName("favorite_topics") val favoriteTopics: List<String>? = null,
    /** Most common source domains */
    @SerialName("favorite_domains") val favoriteDomains: List<String>? = null,
    /** Distribution of summaries by language */
    @SerialName("language_distribution") val languageDistribution: Map<String, Int>? = null,
    /** When the user joined */
    @SerialName("joined_at") val joinedAt: String? = null,
    /** When the last summary was created */
    @SerialName("last_summary_at") val lastSummaryAt: String? = null,
)
