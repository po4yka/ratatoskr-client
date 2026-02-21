package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.SearchApi
import com.po4yka.bitesizereader.domain.model.Summary
import org.koin.core.annotation.Factory

@Factory
class GetSearchInsightsUseCase(private val api: SearchApi) {
    suspend operator fun invoke(
        days: Int = 30,
        limit: Int = 20,
    ): List<Summary> {
        val response = api.getSearchInsights(days, limit)
        return response.data?.toDomain() ?: emptyList()
    }
}
