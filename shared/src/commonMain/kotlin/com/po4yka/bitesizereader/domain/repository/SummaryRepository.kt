package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Summary
import kotlinx.coroutines.flow.Flow

interface SummaryRepository {
    fun getSummaries(
        page: Int,
        pageSize: Int,
        tags: List<String>?,
    ): Flow<List<Summary>>

    suspend fun getSummaryById(id: String): Summary?

    suspend fun markAsRead(id: String)

    suspend fun deleteSummary(id: String)
}
