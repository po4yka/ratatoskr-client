package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.RecommendationRepository
import org.koin.core.annotation.Factory

@Factory
class RefreshRecommendationsUseCase(private val recommendationRepository: RecommendationRepository) {
    suspend operator fun invoke() = recommendationRepository.refreshRecommendations()
}
