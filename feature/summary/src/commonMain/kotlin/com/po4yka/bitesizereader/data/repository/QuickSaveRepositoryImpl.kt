package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.QuickSaveApi
import com.po4yka.bitesizereader.data.remote.dto.QuickSaveRequestDto
import com.po4yka.bitesizereader.domain.model.QuickSaveResult
import com.po4yka.bitesizereader.domain.repository.QuickSaveRepository
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
