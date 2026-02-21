package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.SummariesApi
import com.po4yka.bitesizereader.domain.model.Summary
import org.koin.core.annotation.Factory

@Factory
class GetSummaryByUrlUseCase(private val api: SummariesApi) {
    suspend operator fun invoke(url: String): Summary? {
        val response = api.getSummaryByUrl(url)
        return if (response.success && response.data != null) {
            response.data.toDomain()
        } else {
            null
        }
    }
}
