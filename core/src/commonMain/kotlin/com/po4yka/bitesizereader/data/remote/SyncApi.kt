package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DeltaSyncResponseDto
import com.po4yka.bitesizereader.data.remote.dto.FullSyncResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSessionRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSessionResponseDto

/**
 * Result of a delta sync call, wrapping both the response body and ETag header.
 * When the server returns 304 Not Modified, [response] is null.
 */
data class DeltaSyncResult(
    val response: ApiResponseDto<DeltaSyncResponseDto>?,
    val etag: String?,
)

/**
 * Sync API matching OpenAPI spec with session-based sync.
 */
interface SyncApi {
    // ========================================================================
    // Session-Based Sync (new OpenAPI spec)
    // ========================================================================

    /** Create or resume a sync session */
    suspend fun createSession(request: SyncSessionRequestDto? = null): ApiResponseDto<SyncSessionResponseDto>

    /** Fetch full sync data in bounded chunks */
    suspend fun fullSync(
        sessionId: String,
        limit: Int? = null,
    ): ApiResponseDto<FullSyncResponseDto>

    /** Fetch delta sync (changes since cursor). Returns [DeltaSyncResult] with null response on 304. */
    suspend fun deltaSync(
        sessionId: String,
        since: Long,
        limit: Int? = null,
        etag: String? = null,
    ): DeltaSyncResult

    /** Apply client-side changes with conflict detection */
    suspend fun applyChanges(request: SyncApplyRequestDto): ApiResponseDto<SyncApplyResponseDto>
}
