package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SearchRepository
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
