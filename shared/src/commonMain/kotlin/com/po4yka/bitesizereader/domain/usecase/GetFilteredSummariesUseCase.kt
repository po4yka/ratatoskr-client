package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import com.po4yka.bitesizereader.domain.model.ReadFilter
import com.po4yka.bitesizereader.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetFilteredSummariesUseCase(private val repository: SummaryRepository) {
    operator fun invoke(
        page: Int,
        pageSize: Int,
        readFilter: ReadFilter,
        sortOrder: SortOrder,
        selectedTag: String? = null,
    ): Flow<List<Summary>> {
        return repository.getSummariesFiltered(page, pageSize, readFilter, sortOrder, selectedTag)
    }
}
