package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Recommendation
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository {
    fun getRecommendations(limit: Int = 10): Flow<List<Recommendation>>

    suspend fun refreshRecommendations()

    suspend fun dismissRecommendation(id: String)
}
