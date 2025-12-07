package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RequestStatusResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SubmitRequestResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SubmitURLRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorRequestsApi(private val client: HttpClient) : RequestsApi {
    override suspend fun submitUrl(request: SubmitURLRequestDto): ApiResponseDto<SubmitRequestResponseDto> {
        return client.post("v1/requests") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getRequestStatus(id: String): ApiResponseDto<RequestStatusResponseDto> {
        return client.get("v1/requests/$id/status").body()
    }
}
