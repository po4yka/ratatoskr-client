package com.po4yka.bitesizereader.data.remote.api

import com.po4yka.bitesizereader.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Requests API interface
 */
interface RequestsApi {
    suspend fun submitURL(request: SubmitURLRequestDto): ApiResponse<RequestResponseDto>

    suspend fun getRequestStatus(requestId: Int): ApiResponse<RequestStatusDto>

    suspend fun retryRequest(requestId: Int): ApiResponse<RequestResponseDto>

    suspend fun cancelRequest(requestId: Int): ApiResponse<RequestResponseDto>
}

/**
 * Requests API implementation
 */
class RequestsApiImpl(
    private val client: HttpClient,
) : RequestsApi {
    override suspend fun submitURL(request: SubmitURLRequestDto): ApiResponse<RequestResponseDto> {
        return client.post("/v1/requests") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getRequestStatus(requestId: Int): ApiResponse<RequestStatusDto> {
        return client.get("/v1/requests/$requestId/status").body()
    }

    override suspend fun retryRequest(requestId: Int): ApiResponse<RequestResponseDto> {
        return client.post("/v1/requests/$requestId/retry").body()
    }

    override suspend fun cancelRequest(requestId: Int): ApiResponse<RequestResponseDto> {
        return client.post("/v1/requests/$requestId/cancel").body()
    }
}
