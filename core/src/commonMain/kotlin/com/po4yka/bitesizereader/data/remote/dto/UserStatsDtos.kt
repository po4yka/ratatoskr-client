package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User reading streak data from GET /v1/user/streak.
 */
@Serializable
data class StreakDto(
    @SerialName("current_streak") val currentStreak: Int,
    @SerialName("longest_streak") val longestStreak: Int,
    @SerialName("last_activity_date") val lastActivityDate: String? = null,
    @SerialName("today_count") val todayCount: Int = 0,
    @SerialName("week_count") val weekCount: Int = 0,
    @SerialName("month_count") val monthCount: Int = 0,
)

/**
 * A server-side reading goal from GET /v1/user/goals.
 */
@Serializable
data class GoalDto(
    @SerialName("id") val id: String,
    @SerialName("goal_type") val goalType: String,
    @SerialName("target_count") val targetCount: Int,
    @SerialName("created_at") val createdAt: String,
)

/**
 * Progress towards a reading goal from GET /v1/user/goals/progress.
 */
@Serializable
data class GoalProgressDto(
    @SerialName("goal_type") val goalType: String,
    @SerialName("target_count") val targetCount: Int,
    @SerialName("current_count") val currentCount: Int,
    @SerialName("achieved") val achieved: Boolean,
)

/**
 * Request body for POST /v1/user/goals.
 */
@Serializable
data class CreateGoalRequestDto(
    @SerialName("goal_type") val goalType: String,
    @SerialName("target_count") val targetCount: Int,
)

/**
 * Topic statistics entry with name and count.
 */
@Serializable
data class TopicStatDto(
    /** Topic name */
    @SerialName("topic") val topic: String,
    /** Number of summaries with this topic */
    @SerialName("count") val count: Int,
)

/**
 * Domain statistics entry with name and count.
 */
@Serializable
data class DomainStatDto(
    /** Domain name */
    @SerialName("domain") val domain: String,
    /** Number of summaries from this domain */
    @SerialName("count") val count: Int,
)

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
    /** Most common topics in user's summaries with counts */
    @SerialName("favorite_topics") val favoriteTopics: List<TopicStatDto>? = null,
    /** Most common source domains with counts */
    @SerialName("favorite_domains") val favoriteDomains: List<DomainStatDto>? = null,
    /** Distribution of summaries by language */
    @SerialName("language_distribution") val languageDistribution: Map<String, Int>? = null,
    /** When the user joined */
    @SerialName("joined_at") val joinedAt: String? = null,
    /** When the last summary was created */
    @SerialName("last_summary_at") val lastSummaryAt: String? = null,
)
