package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryDetailDataDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryListDataDto
import com.po4yka.bitesizereader.data.remote.dto.UpdateSummaryRequestDto
import com.po4yka.bitesizereader.data.remote.dto.UpdateSummaryResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorSummariesApi(private val client: HttpClient) : SummariesApi {
    override suspend fun getSummaries(
        page: Int,
        pageSize: Int,
        isRead: Boolean?,
        lang: String?,
        sort: String?
    ): ApiResponseDto<SummaryListDataDto> {
        // Backend uses limit/offset; translate page to offset.
        val offset = (page.coerceAtLeast(1) - 1) * pageSize
        return client.get("v1/summaries") {
            parameter("limit", pageSize)
            parameter("offset", offset)
            if (isRead != null) parameter("is_read", isRead)
            if (lang != null) parameter("lang", lang)
            if (sort != null) parameter("sort", sort)
        }.body()
    }

    override suspend fun getSummaryById(id: Long): ApiResponseDto<SummaryDetailDataDto> {
        return client.get("v1/summaries/$id").body()
    }

    override suspend fun updateSummary(id: Long, isRead: Boolean): ApiResponseDto<UpdateSummaryResponseDto> {
        return client.patch("v1/summaries/$id") {
            contentType(ContentType.Application.Json)
            setBody(UpdateSummaryRequestDto(isRead = isRead))
        }.body()
    }

    override suspend fun deleteSummary(id: Long) {
        client.delete("v1/summaries/$id")
    }
}
