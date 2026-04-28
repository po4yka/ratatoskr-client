package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.ReadingGoalRepository
import org.koin.core.annotation.Factory

@Factory
class UpdateReadingGoalUseCase(
    private val readingGoalRepository: ReadingGoalRepository,
) {
    suspend fun setTarget(minutes: Int) = readingGoalRepository.updateDailyTarget(minutes)

    suspend fun setEnabled(enabled: Boolean) = readingGoalRepository.setEnabled(enabled)
}
