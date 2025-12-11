package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DeltaSyncResponseDto
import com.po4yka.bitesizereader.data.remote.dto.FullSyncResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncDeltaResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSessionRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSessionResponseDto

/**
 * Sync API matching OpenAPI spec with session-based sync.
 */
interface SyncApi {
    // ========================================================================
    // Legacy Sync (for backward compatibility)
    // ========================================================================

    /** Legacy delta sync using session_id as user identifier */
    suspend fun sync(
        sessionId: Long,
        sinceTimestamp: Long,
    ): ApiResponseDto<SyncDeltaResponseDto>

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

    /** Fetch delta sync (changes since cursor) */
    suspend fun deltaSync(
        sessionId: String,
        since: Long,
        limit: Int? = null,
    ): ApiResponseDto<DeltaSyncResponseDto>

    /** Apply client-side changes with conflict detection */
    suspend fun applyChanges(request: SyncApplyRequestDto): ApiResponseDto<SyncApplyResponseDto>
}
