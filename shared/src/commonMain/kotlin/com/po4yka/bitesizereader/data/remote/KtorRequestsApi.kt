package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RequestCreatedDto
import com.po4yka.bitesizereader.data.remote.dto.RequestStatusDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class KtorRequestsApi(private val client: HttpClient) : RequestsApi {
    override suspend fun submitUrl(url: String, langPreference: String): ApiResponseDto<RequestCreatedDto> {
        return client.post("requests") {
            setBody(
                mapOf(
                    "type" to "url",
                    "input_url" to url,
                    "lang_preference" to langPreference
                )
            )
        }.body()
    }

    override suspend fun getRequestStatus(id: String): ApiResponseDto<RequestStatusDto> {
        return client.get("requests/$id/status").body()
    }
}
