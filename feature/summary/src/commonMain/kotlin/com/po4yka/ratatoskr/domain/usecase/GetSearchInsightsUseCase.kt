package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.repository.SearchRepository
import org.koin.core.annotation.Factory

@Factory
class GetSearchInsightsUseCase(private val searchRepository: SearchRepository) {
    suspend operator fun invoke(
        days: Int = 30,
        limit: Int = 20,
    ): List<Summary> {
        return searchRepository.getSearchInsights(days, limit)
    }
}
