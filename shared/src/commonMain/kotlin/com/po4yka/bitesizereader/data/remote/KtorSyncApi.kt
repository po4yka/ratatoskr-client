package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DeltaSyncResponseDto
import com.po4yka.bitesizereader.data.remote.dto.FullSyncResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncDeltaResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSessionRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSessionResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.koin.core.annotation.Single

@Single
class KtorSyncApi(private val client: HttpClient) : SyncApi {
    // ========================================================================
    // Session-Based Sync (new OpenAPI spec)
    // ========================================================================

    override suspend fun createSession(request: SyncSessionRequestDto?): ApiResponseDto<SyncSessionResponseDto> {
        return client.post("v1/sync/sessions") {
            request?.let { setBody(it) }
        }.body()
    }

    override suspend fun fullSync(
        sessionId: String,
        limit: Int?,
    ): ApiResponseDto<FullSyncResponseDto> {
        return client.get("v1/sync/full") {
            parameter("session_id", sessionId)
            limit?.let { parameter("limit", it) }
        }.body()
    }

    override suspend fun deltaSync(
        sessionId: String,
        since: Long,
        limit: Int?,
    ): ApiResponseDto<DeltaSyncResponseDto> {
        return client.get("v1/sync/delta") {
            parameter("session_id", sessionId)
            parameter("since", since)
            limit?.let { parameter("limit", it) }
        }.body()
    }

    override suspend fun applyChanges(request: SyncApplyRequestDto): ApiResponseDto<SyncApplyResponseDto> {
        return client.post("v1/sync/apply") {
            setBody(request)
        }.body()
    }
}
