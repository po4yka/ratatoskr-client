package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.SyncDeltaResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorSyncApi(private val client: HttpClient) : SyncApi {
    override suspend fun sync(sinceToken: String?): SyncDeltaResponseDto {
        return client.get("sync") {
            sinceToken?.let { parameter("since", it) }
        }.body()
    }
}
