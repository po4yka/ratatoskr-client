package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.ReadingGoalRepository
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class RecalculateStreakUseCase(
    private val readingGoalRepository: ReadingGoalRepository,
    private val getDailyReadingTimeUseCase: GetDailyReadingTimeUseCase,
) {
    suspend operator fun invoke() {
        getDailyReadingTimeUseCase.dailyTotals(days = 90).first().let { dailyTotals ->
            readingGoalRepository.recalculateStreak(dailyTotals)
        }
    }
}
