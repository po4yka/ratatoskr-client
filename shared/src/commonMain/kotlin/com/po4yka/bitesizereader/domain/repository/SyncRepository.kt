package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.SyncState
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    suspend fun sync()

    fun getSyncState(): Flow<SyncState>
}
