package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Highlight
import com.po4yka.bitesizereader.domain.model.HighlightColor
import com.po4yka.bitesizereader.domain.repository.HighlightRepository
import org.koin.core.annotation.Factory

@Factory
class ToggleHighlightUseCase(private val highlightRepository: HighlightRepository) {
    suspend operator fun invoke(
        summaryId: String,
        nodeOffset: Int,
        text: String,
        existingHighlights: List<Highlight>,
        color: HighlightColor = HighlightColor.YELLOW,
    ): Boolean {
        val existing = existingHighlights.find { it.nodeOffset == nodeOffset }
        return if (existing != null) {
            highlightRepository.removeHighlight(existing.id)
            false
        } else {
            highlightRepository.addHighlight(summaryId, text, nodeOffset, color)
            true
        }
    }
}
