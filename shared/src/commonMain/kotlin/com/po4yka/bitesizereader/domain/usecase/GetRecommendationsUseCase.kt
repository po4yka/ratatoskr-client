package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Recommendation
import com.po4yka.bitesizereader.domain.repository.RecommendationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetRecommendationsUseCase(private val recommendationRepository: RecommendationRepository) {
    operator fun invoke(limit: Int = 10): Flow<List<Recommendation>> =
        recommendationRepository.getRecommendations(limit)
}
