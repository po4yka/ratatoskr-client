package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SearchRepository

/**
 * Use case for searching summaries
 */
class SearchSummariesUseCase(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(
        query: String,
        limit: Int = 20,
        offset: Int = 0,
    ): Result<List<Summary>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }

        return searchRepository.search(query, limit, offset)
    }
}
