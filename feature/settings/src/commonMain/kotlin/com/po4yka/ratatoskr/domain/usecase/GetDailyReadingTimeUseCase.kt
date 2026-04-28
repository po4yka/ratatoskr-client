package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.DailyReadingTotal
import com.po4yka.ratatoskr.domain.repository.ReadingTimeRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetDailyReadingTimeUseCase(
    private val readingTimeRepository: ReadingTimeRepository,
) {
    fun todayTotal(): Flow<Int> = readingTimeRepository.getTotalReadingTimeToday()

    fun dailyTotals(days: Int = 30): Flow<List<DailyReadingTotal>> = readingTimeRepository.getDailyTotals(days)
}
