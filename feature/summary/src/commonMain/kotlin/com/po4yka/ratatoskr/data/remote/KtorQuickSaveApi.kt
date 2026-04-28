package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.QuickSaveRequestDto
import com.po4yka.ratatoskr.data.remote.dto.QuickSaveResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single(binds = [QuickSaveApi::class])
class KtorQuickSaveApi(private val client: HttpClient) : QuickSaveApi {
    override suspend fun quickSave(request: QuickSaveRequestDto): ApiResponseDto<QuickSaveResponseDto> {
        return client.post("v1/quick-save") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
