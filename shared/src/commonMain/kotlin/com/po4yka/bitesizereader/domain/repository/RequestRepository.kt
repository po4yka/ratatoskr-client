package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Request
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Request operations
 */
interface RequestRepository {
    /**
     * Submit a URL for summarization
     */
    suspend fun submitURL(
        url: String,
        langPreference: String = "auto",
    ): Result<Request>

    /**
     * Get request status by ID
     */
    suspend fun getRequestStatus(requestId: Int): Result<Request>

    /**
     * Get request with polling for status updates
     */
    fun pollRequestStatus(requestId: Int): Flow<Request>

    /**
     * Retry a failed request
     */
    suspend fun retryRequest(requestId: Int): Result<Request>

    /**
     * Get all requests
     */
    fun getAllRequests(): Flow<List<Request>>

    /**
     * Get pending/processing requests
     */
    fun getPendingRequests(): Flow<List<Request>>

    /**
     * Delete a request
     */
    suspend fun deleteRequest(requestId: Int): Result<Unit>
}
