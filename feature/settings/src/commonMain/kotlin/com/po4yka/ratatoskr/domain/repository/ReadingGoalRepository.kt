package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.DailyReadingTotal
import com.po4yka.ratatoskr.domain.model.ReadingGoal
import kotlinx.coroutines.flow.Flow

interface ReadingGoalRepository {
    fun getGoal(): Flow<ReadingGoal>

    suspend fun updateDailyTarget(minutes: Int)

    suspend fun setEnabled(enabled: Boolean)

    suspend fun recalculateStreak(dailyTotals: List<DailyReadingTotal>)
}
