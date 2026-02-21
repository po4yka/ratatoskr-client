package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SuccessResponse
import com.po4yka.bitesizereader.data.remote.dto.SummaryContentDataDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryDetailDataDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryListDataDto
import com.po4yka.bitesizereader.data.remote.dto.UpdateSummaryRequestDto
import com.po4yka.bitesizereader.data.remote.dto.UpdateSummaryResponseDto
import com.po4yka.bitesizereader.util.retry.RetryPolicy
import com.po4yka.bitesizereader.util.retry.retryWithBackoff
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single(binds = [SummariesApi::class])
class KtorSummariesApi(private val client: HttpClient) : SummariesApi {
    override suspend fun getSummaries(
        page: Int,
        pageSize: Int,
        isRead: Boolean?,
        isFavorited: Boolean?,
        lang: String?,
        startDate: String?,
        endDate: String?,
        sort: String?,
    ): ApiResponseDto<SummaryListDataDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            // Backend uses limit/offset; translate page to offset.
            val offset = (page.coerceAtLeast(1) - 1) * pageSize
            client.get("v1/summaries") {
                parameter("limit", pageSize)
                parameter("offset", offset)
                isRead?.let { parameter("is_read", it) }
                isFavorited?.let { parameter("is_favorited", it) }
                lang?.let { parameter("lang", it) }
                startDate?.let { parameter("start_date", it) }
                endDate?.let { parameter("end_date", it) }
                sort?.let { parameter("sort", it) }
            }.body()
        }

    override suspend fun getSummaryById(id: Long): ApiResponseDto<SummaryDetailDataDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/summaries/$id").body()
        }

    override suspend fun toggleFavorite(id: Long): ApiResponseDto<SuccessResponse> {
        return client.post("v1/summaries/$id/favorite").body()
    }

    override suspend fun updateSummary(
        id: Long,
        isRead: Boolean,
    ): ApiResponseDto<UpdateSummaryResponseDto> {
        return client.patch("v1/summaries/$id") {
            contentType(ContentType.Application.Json)
            setBody(UpdateSummaryRequestDto(isRead = isRead))
        }.body()
    }

    override suspend fun deleteSummary(id: Long) {
        client.delete("v1/summaries/$id")
    }

    override suspend fun getContent(
        id: Long,
        format: String?,
    ): ApiResponseDto<SummaryContentDataDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/summaries/$id/content") {
                format?.let { parameter("format", it) }
            }.body()
        }

    override suspend fun getSummaryByUrl(url: String): ApiResponseDto<SummaryDetailDataDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/summaries/by-url") {
                parameter("url", url)
            }.body()
        }
}
