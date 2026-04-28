package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.repository.SearchRepository
import org.koin.core.annotation.Factory

@Factory
class SearchSummariesUseCase(private val repository: SearchRepository) {
    suspend operator fun invoke(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<Summary> {
        return repository.search(query, page, pageSize)
    }
}
