package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RequestDetailDto
import com.po4yka.bitesizereader.data.remote.dto.RequestStatusResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RetryRequestResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SubmitRequestResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SubmitURLRequestDto
import com.po4yka.bitesizereader.util.retry.RetryPolicy
import com.po4yka.bitesizereader.util.retry.retryWithBackoff
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single
class KtorRequestsApi(private val client: HttpClient) : RequestsApi {
    // No retry for submit - avoid duplicate URL submissions
    override suspend fun submitUrl(request: SubmitURLRequestDto): ApiResponseDto<SubmitRequestResponseDto> {
        return client.post("v1/requests") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getRequest(id: Long): ApiResponseDto<RequestDetailDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/requests/$id").body()
        }

    override suspend fun getRequestStatus(id: Long): ApiResponseDto<RequestStatusResponseDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/requests/$id/status").body()
        }

    // No retry for user-initiated retry action
    override suspend fun retryRequest(id: Long): ApiResponseDto<RetryRequestResponseDto> {
        return client.post("v1/requests/$id/retry").body()
    }
}
