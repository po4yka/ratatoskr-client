package com.po4yka.ratatoskr.feature.summary.api

import com.po4yka.ratatoskr.domain.model.Request
import kotlinx.coroutines.flow.Flow

interface RequestOpsPort {
    fun getRequests(): Flow<List<Request>>

    suspend fun retryRequest(request: Request): Request
}
