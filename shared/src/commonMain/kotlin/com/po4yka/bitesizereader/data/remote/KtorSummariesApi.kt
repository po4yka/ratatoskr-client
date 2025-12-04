package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.SummaryDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryListResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorSummariesApi(private val client: HttpClient) : SummariesApi {
    override suspend fun getSummaries(page: Int, pageSize: Int, tags: List<String>?): SummaryListResponseDto {
        return client.get("summaries") {
            parameter("page", page)
            parameter("page_size", pageSize)
            tags?.forEach { parameter("tags", it) }
        }.body()
    }

    override suspend fun getSummaryById(id: String): SummaryDto {
        return client.get("summaries/$id").body()
    }

    override suspend fun deleteSummary(id: String) {
        client.delete("summaries/$id")
    }
}
