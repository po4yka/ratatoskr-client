package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncDeltaResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single
class KtorSyncApi(private val client: HttpClient) : SyncApi {
    override suspend fun sync(
        sessionId: Long,
        sinceTimestamp: Long,
    ): ApiResponseDto<SyncDeltaResponseDto> {
        return client.get("v1/sync/delta") {
            parameter("session_id", sessionId)
            parameter("since", sinceTimestamp)
        }.body()
    }
}
