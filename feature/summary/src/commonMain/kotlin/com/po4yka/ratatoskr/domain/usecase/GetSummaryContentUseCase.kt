package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class GetSummaryContentUseCase(
    private val repository: SummaryRepository,
) {
    suspend operator fun invoke(id: String): String? = repository.getFullContent(id)
}
