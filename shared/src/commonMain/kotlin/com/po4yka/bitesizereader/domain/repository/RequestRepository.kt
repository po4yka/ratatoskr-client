package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Request
import kotlinx.coroutines.flow.Flow

interface RequestRepository {
    suspend fun submitUrl(url: String): Request
    suspend fun getRequestStatus(id: String): Request
    fun getRequests(): Flow<List<Request>>
}