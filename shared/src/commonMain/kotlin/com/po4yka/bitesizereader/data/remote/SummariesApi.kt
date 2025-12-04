package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.SummaryDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryListResponseDto

interface SummariesApi {
    suspend fun getSummaries(page: Int, pageSize: Int, tags: List<String>?): SummaryListResponseDto
    suspend fun getSummaryById(id: String): SummaryDto
    suspend fun deleteSummary(id: String)
}
