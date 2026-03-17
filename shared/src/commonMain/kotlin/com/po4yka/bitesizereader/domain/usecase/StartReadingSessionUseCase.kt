package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.ReadingTimeRepository
import org.koin.core.annotation.Factory

@Factory
class StartReadingSessionUseCase(
    private val readingTimeRepository: ReadingTimeRepository,
) {
    suspend operator fun invoke(summaryId: String): Long = readingTimeRepository.startSession(summaryId)
}
