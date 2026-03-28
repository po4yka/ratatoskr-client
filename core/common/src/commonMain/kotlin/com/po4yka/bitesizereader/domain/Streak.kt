package com.po4yka.bitesizereader.domain.model

/**
 * User reading streak data from the backend.
 */
data class Streak(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActivityDate: String? = null,
    val todayCount: Int = 0,
    val weekCount: Int = 0,
    val monthCount: Int = 0,
)

/**
 * A server-side reading goal.
 */
data class Goal(
    val id: String,
    val goalType: String,
    val targetCount: Int,
    val createdAt: String,
)

/**
 * Progress towards a server-side reading goal.
 */
data class GoalProgress(
    val goalType: String,
    val targetCount: Int,
    val currentCount: Int,
    val achieved: Boolean,
)
