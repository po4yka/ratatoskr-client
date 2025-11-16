package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.SearchFilters
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting summaries with filters
 */
class GetSummariesUseCase(
    private val summaryRepository: SummaryRepository,
) {
    operator fun invoke(
        limit: Int = 20,
        offset: Int = 0,
        filters: SearchFilters = SearchFilters(),
    ): Flow<List<Summary>> {
        return summaryRepository.getSummaries(limit, offset, filters)
    }
}
