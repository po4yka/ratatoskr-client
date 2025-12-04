package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow

class GetSummariesUseCase(private val repository: SummaryRepository) {
    operator fun invoke(page: Int, pageSize: Int, tags: List<String>? = null): Flow<List<Summary>> {
        return repository.getSummaries(page, pageSize, tags)
    }
}