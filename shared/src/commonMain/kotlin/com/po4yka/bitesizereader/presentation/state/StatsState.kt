package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.GoalProgress
import com.po4yka.bitesizereader.domain.model.Streak
import com.po4yka.bitesizereader.domain.model.UserStats

data class StatsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val stats: UserStats? = null,
    val streak: Streak? = null,
    val goalsProgress: List<GoalProgress> = emptyList(),
    val isStreakLoading: Boolean = false,
    val streakError: String? = null,
)
