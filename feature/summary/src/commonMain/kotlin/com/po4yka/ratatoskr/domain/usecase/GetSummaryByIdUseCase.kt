package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class GetSummaryByIdUseCase(private val repository: SummaryRepository) {
    suspend operator fun invoke(id: String): Summary? {
        return repository.getSummaryById(id)
    }
}
