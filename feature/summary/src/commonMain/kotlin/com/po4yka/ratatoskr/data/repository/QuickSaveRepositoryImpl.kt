package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.QuickSaveApi
import com.po4yka.ratatoskr.data.remote.dto.QuickSaveRequestDto
import com.po4yka.ratatoskr.domain.model.QuickSaveResult
import com.po4yka.ratatoskr.domain.repository.QuickSaveRepository
import org.koin.core.annotation.Single

@Single(binds = [QuickSaveRepository::class])
class QuickSaveRepositoryImpl(
    private val quickSaveApi: QuickSaveApi,
) : QuickSaveRepository {
    override suspend fun quickSave(
        url: String,
        title: String?,
        selectedText: String?,
        tagNames: List<String>,
        summarize: Boolean,
    ): QuickSaveResult {
        val response =
            quickSaveApi.quickSave(
                QuickSaveRequestDto(
                    url = url,
                    title = title,
                    selectedText = selectedText,
                    tagNames = tagNames,
                    summarize = summarize,
                ),
            )
        return requireNotNull(response.data) { "Server returned no data for quick save" }.toDomain()
    }
}
