package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SearchResponseDataDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorSearchApi(private val client: HttpClient) : SearchApi {
    override suspend fun search(query: String, page: Int, pageSize: Int): ApiResponseDto<SearchResponseDataDto> {
        val offset = (page.coerceAtLeast(1) - 1) * pageSize
        return client.get("search") {
            parameter("q", query)
            parameter("limit", pageSize)
            parameter("offset", offset)
        }.body()
    }
}
