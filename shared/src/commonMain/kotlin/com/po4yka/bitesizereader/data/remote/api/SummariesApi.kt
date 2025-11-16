package com.po4yka.bitesizereader.data.remote.api

import com.po4yka.bitesizereader.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Summaries API interface
 */
interface SummariesApi {
    suspend fun getSummaries(
        limit: Int = 20,
        offset: Int = 0,
        isRead: Boolean? = null,
        lang: String? = null,
        fromDate: String? = null,
        toDate: String? = null,
        sortBy: String? = null,
        sortOrder: String? = null
    ): ApiResponse<SummaryListResponseDto>

    suspend fun getSummaryById(id: Int): ApiResponse<SummaryDetailDto>

    suspend fun updateSummary(id: Int, request: SummaryUpdateRequestDto): ApiResponse<SummaryUpdateResponseDto>
}

/**
 * Summaries API implementation
 */
class SummariesApiImpl(
    private val client: HttpClient
) : SummariesApi {

    override suspend fun getSummaries(
        limit: Int,
        offset: Int,
        isRead: Boolean?,
        lang: String?,
        fromDate: String?,
        toDate: String?,
        sortBy: String?,
        sortOrder: String?
    ): ApiResponse<SummaryListResponseDto> {
        return client.get("/v1/summaries") {
            parameter("limit", limit)
            parameter("offset", offset)
            isRead?.let { parameter("is_read", it) }
            lang?.let { parameter("lang", it) }
            fromDate?.let { parameter("from_date", it) }
            toDate?.let { parameter("to_date", it) }
            sortBy?.let { parameter("sort_by", it) }
            sortOrder?.let { parameter("sort_order", it) }
        }.body()
    }

    override suspend fun getSummaryById(id: Int): ApiResponse<SummaryDetailDto> {
        return client.get("/v1/summaries/$id").body()
    }

    override suspend fun updateSummary(
        id: Int,
        request: SummaryUpdateRequestDto
    ): ApiResponse<SummaryUpdateResponseDto> {
        return client.patch("/v1/summaries/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
