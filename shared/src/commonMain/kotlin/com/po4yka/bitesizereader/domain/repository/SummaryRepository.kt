package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.model.ReadFilter
import com.po4yka.bitesizereader.domain.model.SortOrder
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
        selectedTag: String? = null,
    ): Flow<List<Summary>>

    suspend fun getSummaryById(id: String): Summary?

    suspend fun getFullContent(id: String): String?

    suspend fun markAsRead(id: String)

    suspend fun deleteSummary(id: String)

    suspend fun getAllTags(): List<String>
}
