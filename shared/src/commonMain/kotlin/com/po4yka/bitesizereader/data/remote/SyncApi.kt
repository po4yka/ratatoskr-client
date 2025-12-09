package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SyncDeltaResponseDto

interface SyncApi {
    suspend fun sync(
        sessionId: Long,
        sinceTimestamp: Long,
    ): ApiResponseDto<SyncDeltaResponseDto>
}
