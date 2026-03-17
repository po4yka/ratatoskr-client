package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.RecommendationRepository
import org.koin.core.annotation.Factory

@Factory
class RefreshRecommendationsUseCase(private val recommendationRepository: RecommendationRepository) {
    suspend operator fun invoke() = recommendationRepository.refreshRecommendations()
}
