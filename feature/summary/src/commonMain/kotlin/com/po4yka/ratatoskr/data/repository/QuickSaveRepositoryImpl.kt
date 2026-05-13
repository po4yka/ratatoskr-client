package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.QuickSaveApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.V1QuickSaveRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.dto.QuickSaveResponseDto
import com.po4yka.ratatoskr.domain.model.QuickSaveResult
import com.po4yka.ratatoskr.domain.repository.QuickSaveRepository
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

private val lenientJson = Json { ignoreUnknownKeys = true }

@Single(binds = [QuickSaveRepository::class])
class QuickSaveRepositoryImpl : QuickSaveRepository {
    override suspend fun quickSave(
        url: String,
        title: String?,
        selectedText: String?,
        tagNames: List<String>,
        summarize: Boolean,
    ): QuickSaveResult {
        val jsonElement = QuickSaveApi.quickSaveV1QuickSavePost(
            body = V1QuickSaveRequest(
                url = url,
                title = title,
                selectedText = selectedText,
                tagNames = tagNames,
                summarize = summarize,
            ),
        ).unwrap()
        val responseDto = lenientJson.decodeFromJsonElement(QuickSaveResponseDto.serializer(), jsonElement)
        return responseDto.toDomain()
    }
}
