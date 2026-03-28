package com.po4yka.bitesizereader.feature.summary.api

import com.po4yka.bitesizereader.domain.model.Request
import kotlinx.coroutines.flow.Flow

interface RequestOpsPort {
    fun getRequests(): Flow<List<Request>>

    suspend fun retryRequest(request: Request): Request
}
