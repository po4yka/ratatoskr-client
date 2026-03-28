package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class GetSummaryByIdUseCase(private val repository: SummaryRepository) {
    suspend operator fun invoke(id: String): Summary? {
        return repository.getSummaryById(id)
    }
}
