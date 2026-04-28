package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.SummaryFeedback
import com.po4yka.ratatoskr.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetSummaryFeedbackUseCase(private val repository: SummaryRepository) {
    operator fun invoke(summaryId: String): Flow<SummaryFeedback?> = repository.getFeedback(summaryId)
}
