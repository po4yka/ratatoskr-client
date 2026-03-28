package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.HighlightRepository
import org.koin.core.annotation.Factory

@Factory
class UpdateAnnotationUseCase(private val highlightRepository: HighlightRepository) {
    suspend operator fun invoke(
        highlightId: String,
        note: String?,
    ) = highlightRepository.updateNote(highlightId, note)
}
