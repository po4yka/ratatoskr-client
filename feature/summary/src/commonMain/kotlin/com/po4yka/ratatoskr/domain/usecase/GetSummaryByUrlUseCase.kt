package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class GetSummaryByUrlUseCase(private val summaryRepository: SummaryRepository) {
    suspend operator fun invoke(url: String): Summary? {
        return summaryRepository.getSummaryByUrl(url)
    }
}
