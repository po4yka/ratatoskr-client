package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class GetSummaryByUrlUseCase(private val summaryRepository: SummaryRepository) {
    suspend operator fun invoke(url: String): Summary? {
        return summaryRepository.getSummaryByUrl(url)
    }
}
