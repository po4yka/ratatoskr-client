package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetSummariesUseCase(private val repository: SummaryRepository) {
    operator fun invoke(
        page: Int,
        pageSize: Int,
        tags: List<String>? = null,
    ): Flow<List<Summary>> {
        return repository.getSummaries(page, pageSize, tags)
    }
}
