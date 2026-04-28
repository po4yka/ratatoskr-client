package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.ReadingGoalProgress
import com.po4yka.ratatoskr.domain.repository.ReadingGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.annotation.Factory

@Factory
class GetReadingGoalProgressUseCase(
    private val readingGoalRepository: ReadingGoalRepository,
    private val getDailyReadingTimeUseCase: GetDailyReadingTimeUseCase,
) {
    operator fun invoke(): Flow<ReadingGoalProgress> =
        combine(
            readingGoalRepository.getGoal(),
            getDailyReadingTimeUseCase.todayTotal(),
        ) { goal, todayReadingSec ->
            val targetSec = goal.dailyTargetMin * 60
            ReadingGoalProgress(
                goal = goal,
                todayReadingSec = todayReadingSec,
                progressFraction =
                    if (targetSec > 0) {
                        (todayReadingSec.toFloat() / targetSec).coerceIn(0f, 1f)
                    } else {
                        0f
                    },
                isCompletedToday = todayReadingSec >= targetSec,
            )
        }
}
