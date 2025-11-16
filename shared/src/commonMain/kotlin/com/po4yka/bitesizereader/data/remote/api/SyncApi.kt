package com.po4yka.bitesizereader.data.remote.api

import com.po4yka.bitesizereader.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Sync API interface
 */
interface SyncApi {
    suspend fun getDeltaSync(since: String): ApiResponse<SyncDeltaResponseDto>

    suspend fun uploadChanges(request: SyncUploadRequestDto): ApiResponse<Unit>
}

/**
 * Sync API implementation
 */
class SyncApiImpl(
    private val client: HttpClient,
) : SyncApi {
    override suspend fun getDeltaSync(since: String): ApiResponse<SyncDeltaResponseDto> {
        return client.get("/v1/sync/delta") {
            parameter("since", since)
        }.body()
    }

    override suspend fun uploadChanges(request: SyncUploadRequestDto): ApiResponse<Unit> {
        return client.post("/v1/sync/upload") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
