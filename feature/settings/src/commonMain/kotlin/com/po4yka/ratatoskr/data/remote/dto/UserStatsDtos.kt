package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User reading streak data from GET /v1/user/streak.
 */
@Serializable
data class StreakDto(
    @SerialName("currentStreak") val currentStreak: Int,
    @SerialName("longestStreak") val longestStreak: Int,
    @SerialName("lastActivityDate") val lastActivityDate: String? = null,
    @SerialName("todayCount") val todayCount: Int = 0,
    @SerialName("weekCount") val weekCount: Int = 0,
    @SerialName("monthCount") val monthCount: Int = 0,
)

/**
 * A server-side reading goal from GET /v1/user/goals.
 */
@Serializable
data class GoalDto(
    @SerialName("id") val id: String,
    @SerialName("goalType") val goalType: String,
    @SerialName("targetCount") val targetCount: Int,
    @SerialName("createdAt") val createdAt: String,
)

/**
 * Progress towards a reading goal from GET /v1/user/goals/progress.
 */
@Serializable
data class GoalProgressDto(
    @SerialName("goalType") val goalType: String,
    @SerialName("targetCount") val targetCount: Int,
    @SerialName("currentCount") val currentCount: Int,
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
    @SerialName("totalSummaries") val totalSummaries: Int,
    /** Number of unread summaries */
    @SerialName("unreadCount") val unreadCount: Int,
    /** Number of read summaries */
    @SerialName("readCount") val readCount: Int,
    /** Total reading time in minutes */
    @SerialName("totalReadingTimeMin") val totalReadingTimeMin: Int? = null,
    /** Average reading time per summary in minutes */
    @SerialName("averageReadingTimeMin") val averageReadingTimeMin: Float? = null,
    /** Most common topics in user's summaries with counts */
    @SerialName("favoriteTopics") val favoriteTopics: List<TopicStatDto>? = null,
    /** Most common source domains with counts */
    @SerialName("favoriteDomains") val favoriteDomains: List<DomainStatDto>? = null,
    /** Distribution of summaries by language */
    @SerialName("languageDistribution") val languageDistribution: Map<String, Int>? = null,
    /** When the user joined */
    @SerialName("joinedAt") val joinedAt: String? = null,
    /** When the last summary was created */
    @SerialName("lastSummaryAt") val lastSummaryAt: String? = null,
)
