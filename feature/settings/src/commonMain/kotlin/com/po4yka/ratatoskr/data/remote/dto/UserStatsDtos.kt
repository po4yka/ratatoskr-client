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

