package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.RequestDto
import com.po4yka.bitesizereader.data.remote.dto.RequestStatusDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class KtorRequestsApi(private val client: HttpClient) : RequestsApi {
    override suspend fun submitUrl(url: String): RequestDto {
        return client.post("requests") {
            setBody(mapOf("url" to url))
        }.body()
    }

    override suspend fun getRequestStatus(id: String): RequestStatusDto {
        return client.get("requests/$id/status").body()
    }
}
