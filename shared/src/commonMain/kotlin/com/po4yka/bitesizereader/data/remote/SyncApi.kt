package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.SyncDeltaResponseDto

interface SyncApi {
    suspend fun sync(sinceToken: String?): SyncDeltaResponseDto
}
