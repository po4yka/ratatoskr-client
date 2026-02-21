package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.SearchApi
import com.po4yka.bitesizereader.domain.model.Summary
import org.koin.core.annotation.Factory

@Factory
class SemanticSearchUseCase(private val searchApi: SearchApi) {
    suspend operator fun invoke(
        query: String,
        page: Int,
        pageSize: Int,
        language: String? = null,
        tags: List<String>? = null,
    ): List<Summary> {
        val response = searchApi.semanticSearch(
            query = query,
            page = page,
            pageSize = pageSize,
            language = language,
            tags = tags,
        )
        return response.data?.toDomain() ?: emptyList()
    }
}
