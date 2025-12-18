package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.presentation.state.ReadFilter
import com.po4yka.bitesizereader.presentation.state.SortOrder
import kotlinx.coroutines.flow.Flow

interface SummaryRepository {
    fun getSummaries(
        page: Int,
        pageSize: Int,
        tags: List<String>?,
    ): Flow<List<Summary>>

    fun getSummariesFiltered(
        page: Int,
        pageSize: Int,
        readFilter: ReadFilter,
        sortOrder: SortOrder,
    ): Flow<List<Summary>>

    suspend fun getSummaryById(id: String): Summary?

    suspend fun markAsRead(id: String)

    suspend fun deleteSummary(id: String)

    suspend fun getAllTags(): List<String>
}
