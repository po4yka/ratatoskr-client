package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.ReadingTimeRepository
import org.koin.core.annotation.Factory

@Factory
class EndReadingSessionUseCase(
    private val readingTimeRepository: ReadingTimeRepository,
) {
    suspend operator fun invoke(
        sessionId: Long,
        durationSec: Int,
    ) = readingTimeRepository.endSession(sessionId, durationSec)
}
