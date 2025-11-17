package com.po4yka.bitesizereader.data.remote.api

import com.po4yka.bitesizereader.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Search API interface
 */
interface SearchApi {
    suspend fun search(
        query: String,
        limit: Int = 20,
        offset: Int = 0,
    ): ApiResponse<SearchResponseDto>
}

/**
 * Search API implementation
 */
class SearchApiImpl(
    private val client: HttpClient,
) : SearchApi {
    override suspend fun search(
        query: String,
        limit: Int,
        offset: Int,
    ): ApiResponse<SearchResponseDto> {
        return client.get("/v1/search") {
            parameter("q", query)
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }
}
