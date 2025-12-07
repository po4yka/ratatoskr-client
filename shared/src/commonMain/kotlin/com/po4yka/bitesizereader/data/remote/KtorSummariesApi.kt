package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryDetailDataDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryListDataDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorSummariesApi(private val client: HttpClient) : SummariesApi {
    override suspend fun getSummaries(
        page: Int,
        pageSize: Int,
        tags: List<String>?
    ): ApiResponseDto<SummaryListDataDto> {
        // Backend uses limit/offset; translate page to offset.
        val offset = (page.coerceAtLeast(1) - 1) * pageSize
        return client.get("summaries") {
            parameter("limit", pageSize)
            parameter("offset", offset)
            tags?.forEach { parameter("tags", it) }
        }.body()
    }

    override suspend fun getSummaryById(id: String): ApiResponseDto<SummaryDetailDataDto> {
        return client.get("summaries/$id").body()
    }

    override suspend fun deleteSummary(id: String) {
        client.delete("summaries/$id")
    }
}
