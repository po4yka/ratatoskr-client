package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RequestDetailDto
import com.po4yka.bitesizereader.data.remote.dto.RequestStatusResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RetryRequestResponseDto
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
        return client.post("requests") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getRequest(id: Long): ApiResponseDto<RequestDetailDto> {
        return client.get("requests/$id").body()
    }

    override suspend fun getRequestStatus(id: Long): ApiResponseDto<RequestStatusResponseDto> {
        return client.get("requests/$id/status").body()
    }

    override suspend fun retryRequest(id: Long): ApiResponseDto<RetryRequestResponseDto> {
        return client.post("requests/$id/retry").body()
    }
}
