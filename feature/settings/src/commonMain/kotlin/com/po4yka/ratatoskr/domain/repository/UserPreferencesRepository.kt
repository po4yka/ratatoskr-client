package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.Goal
import com.po4yka.ratatoskr.domain.model.GoalProgress
import com.po4yka.ratatoskr.domain.model.Streak
import com.po4yka.ratatoskr.domain.model.UserPreferences
import com.po4yka.ratatoskr.domain.model.UserStats

/**
 * Repository for managing user preferences and statistics.
 */
interface UserPreferencesRepository {
    /** Get current user preferences */
    suspend fun getPreferences(): UserPreferences

    /** Update user preferences */
    suspend fun updatePreferences(langPreference: String? = null): UserPreferences

    /** Get user statistics */
    suspend fun getStats(): UserStats

    /** Get user reading streak from backend */
    suspend fun getStreak(): Streak

    /** Get all user reading goals from backend */
    suspend fun getGoals(): List<Goal>

    /** Get progress for all reading goals from backend */
    suspend fun getGoalsProgress(): List<GoalProgress>

    /** Create a new reading goal */
    suspend fun createGoal(
        goalType: String,
        targetCount: Int,
    ): Goal
}
