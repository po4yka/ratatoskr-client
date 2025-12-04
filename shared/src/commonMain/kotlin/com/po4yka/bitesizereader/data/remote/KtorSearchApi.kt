package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.SearchResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorSearchApi(private val client: HttpClient) : SearchApi {
    override suspend fun search(query: String, page: Int, pageSize: Int): SearchResponseDto {
        return client.get("search") {
            parameter("q", query)
            parameter("page", page)
            parameter("page_size", pageSize)
        }.body()
    }
}
