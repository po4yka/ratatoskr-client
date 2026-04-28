package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Highlight
import com.po4yka.ratatoskr.domain.repository.HighlightRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetHighlightsUseCase(private val highlightRepository: HighlightRepository) {
    operator fun invoke(summaryId: String): Flow<List<Highlight>> =
        highlightRepository.getHighlightsForSummary(summaryId)
}
