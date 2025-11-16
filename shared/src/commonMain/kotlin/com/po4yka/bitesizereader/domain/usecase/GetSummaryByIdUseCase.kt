package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SummaryRepository

/**
 * Use case for getting a single summary by ID
 */
class GetSummaryByIdUseCase(
    private val summaryRepository: SummaryRepository,
) {
    suspend operator fun invoke(id: Int): Result<Summary> {
        return summaryRepository.getSummaryById(id)
    }
}
