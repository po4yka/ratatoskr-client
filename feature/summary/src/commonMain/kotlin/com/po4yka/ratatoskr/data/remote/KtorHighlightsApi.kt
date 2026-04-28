package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.CreateHighlightRequestDto
import com.po4yka.ratatoskr.data.remote.dto.HighlightDeleteResponseDto
import com.po4yka.ratatoskr.data.remote.dto.HighlightListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.HighlightResponseDto
import com.po4yka.ratatoskr.data.remote.dto.UpdateHighlightRequestDto
import com.po4yka.ratatoskr.util.retry.RetryPolicy
import com.po4yka.ratatoskr.util.retry.retryWithBackoff
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single(binds = [HighlightsApi::class])
class KtorHighlightsApi(private val client: HttpClient) : HighlightsApi {
    override suspend fun listHighlights(summaryId: Long): ApiResponseDto<HighlightListResponseDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/summaries/$summaryId/highlights").body()
        }

    override suspend fun createHighlight(
        summaryId: Long,
        request: CreateHighlightRequestDto,
    ): ApiResponseDto<HighlightResponseDto> {
        return client.post("v1/summaries/$summaryId/highlights") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun updateHighlight(
        summaryId: Long,
        highlightId: String,
        request: UpdateHighlightRequestDto,
    ): ApiResponseDto<HighlightResponseDto> {
        return client.patch("v1/summaries/$summaryId/highlights/$highlightId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteHighlight(
        summaryId: Long,
        highlightId: String,
    ): ApiResponseDto<HighlightDeleteResponseDto> {
        return client.delete("v1/summaries/$summaryId/highlights/$highlightId").body()
    }
}
