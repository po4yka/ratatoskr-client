package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.RecommendationRepository
import org.koin.core.annotation.Factory

@Factory
class DismissRecommendationUseCase(private val recommendationRepository: RecommendationRepository) {
    suspend operator fun invoke(id: String) = recommendationRepository.dismissRecommendation(id)
}
