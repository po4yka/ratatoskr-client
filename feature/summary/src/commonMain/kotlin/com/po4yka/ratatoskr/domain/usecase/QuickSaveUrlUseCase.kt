package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.QuickSaveResult
import com.po4yka.ratatoskr.domain.repository.QuickSaveRepository
import org.koin.core.annotation.Factory

@Factory
class QuickSaveUrlUseCase(private val quickSaveRepository: QuickSaveRepository) {
    suspend operator fun invoke(
        url: String,
        title: String? = null,
        selectedText: String? = null,
        tagNames: List<String> = emptyList(),
        summarize: Boolean = true,
    ): QuickSaveResult =
        quickSaveRepository.quickSave(
            url = url,
            title = title,
            selectedText = selectedText,
            tagNames = tagNames,
            summarize = summarize,
        )
}
