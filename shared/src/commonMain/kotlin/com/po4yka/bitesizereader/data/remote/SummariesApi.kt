package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryDetailDataDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryListDataDto

interface SummariesApi {
    suspend fun getSummaries(page: Int, pageSize: Int, tags: List<String>?): ApiResponseDto<SummaryListDataDto>
    suspend fun getSummaryById(id: String): ApiResponseDto<SummaryDetailDataDto>
    suspend fun deleteSummary(id: String)
}
