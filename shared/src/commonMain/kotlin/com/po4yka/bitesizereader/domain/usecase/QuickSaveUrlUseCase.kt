package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.QuickSaveApi
import com.po4yka.bitesizereader.data.remote.dto.QuickSaveRequestDto
import com.po4yka.bitesizereader.data.remote.dto.QuickSaveResponseDto
import org.koin.core.annotation.Factory

@Factory
class QuickSaveUrlUseCase(private val quickSaveApi: QuickSaveApi) {
    suspend operator fun invoke(
        url: String,
        title: String? = null,
        selectedText: String? = null,
        tagNames: List<String> = emptyList(),
        summarize: Boolean = true,
    ): QuickSaveResponseDto {
        val response = quickSaveApi.quickSave(
            QuickSaveRequestDto(
                url = url,
                title = title,
                selectedText = selectedText,
                tagNames = tagNames,
                summarize = summarize,
            ),
        )
        return requireNotNull(response.data) { "Server returned no data for quick save" }
    }
}
