package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SearchRepository
import org.koin.core.annotation.Factory

@Factory
class SemanticSearchUseCase(private val searchRepository: SearchRepository) {
    suspend operator fun invoke(
        query: String,
        page: Int,
        pageSize: Int,
        language: String? = null,
        tags: List<String>? = null,
    ): List<Summary> {
        return searchRepository.semanticSearch(
            query = query,
            page = page,
            pageSize = pageSize,
            language = language,
            tags = tags,
        )
    }
}
