package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.FullSyncResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSessionRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSessionResponseDto
import com.po4yka.bitesizereader.util.retry.RetryPolicy
import com.po4yka.bitesizereader.util.retry.retryWithBackoff
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import org.koin.core.annotation.Single

@Single(binds = [SyncApi::class])
class KtorSyncApi(private val client: HttpClient) : SyncApi {
    // ========================================================================
    // Session-Based Sync (new OpenAPI spec)
    // ========================================================================

    override suspend fun createSession(request: SyncSessionRequestDto?): ApiResponseDto<SyncSessionResponseDto> =
        retryWithBackoff(RetryPolicy.CONSERVATIVE) {
            client.post("v1/sync/sessions") {
                request?.let { setBody(it) }
            }.body()
        }

    override suspend fun fullSync(
        sessionId: String,
        limit: Int?,
        cursor: Long?,
    ): ApiResponseDto<FullSyncResponseDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/sync/full") {
                parameter("session_id", sessionId)
                limit?.let { parameter("limit", it) }
                cursor?.let { parameter("cursor", it) }
            }.body()
        }

    override suspend fun deltaSync(
        sessionId: String,
        since: Long,
        limit: Int?,
        etag: String?,
    ): DeltaSyncResult =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            try {
                val response =
                    client.get("v1/sync/delta") {
                        parameter("session_id", sessionId)
                        parameter("since", since)
                        limit?.let { parameter("limit", it) }
                        etag?.let { header(HttpHeaders.IfNoneMatch, it) }
                    }
                val responseEtag = response.headers[HttpHeaders.ETag]
                DeltaSyncResult(response = response.body(), etag = responseEtag)
            } catch (e: ResponseException) {
                if (e.response.status == HttpStatusCode.NotModified) {
                    val responseEtag = e.response.headers[HttpHeaders.ETag]
                    DeltaSyncResult(response = null, etag = responseEtag)
                } else {
                    throw e
                }
            }
        }

    override suspend fun applyChanges(request: SyncApplyRequestDto): ApiResponseDto<SyncApplyResponseDto> =
        retryWithBackoff(RetryPolicy.CONSERVATIVE) {
            client.post("v1/sync/apply") {
                setBody(request)
            }.body()
        }
}
