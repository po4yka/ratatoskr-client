package com.po4yka.ratatoskr.domain.model

data class ReadingGoal(
    val dailyTargetMin: Int,
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val lastCompletedDate: String?,
    val isEnabled: Boolean,
)

data class ReadingGoalProgress(
    val goal: ReadingGoal,
    val todayReadingSec: Int,
    val progressFraction: Float,
    val isCompletedToday: Boolean,
)
