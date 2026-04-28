package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.feature.summary.api.RequestOpsPort
import kotlinx.coroutines.flow.Flow

interface RequestRepository : RequestOpsPort {
    suspend fun submitUrl(url: String): Request

    /** Submit a forwarded message for summarization. */
    suspend fun submitForward(
        contentText: String,
        langPreference: String = "auto",
    ): Request

    suspend fun getRequestStatus(id: String): Request

    override fun getRequests(): Flow<List<Request>>

    override suspend fun retryRequest(request: Request): Request = submitUrl(request.url)
}
