package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryDetailDataDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryListDataDto
import com.po4yka.bitesizereader.data.remote.dto.SuccessResponse
import com.po4yka.bitesizereader.data.remote.dto.UpdateSummaryResponseDto

interface SummariesApi {
    suspend fun getSummaries(
        page: Int,
        pageSize: Int,
        isRead: Boolean? = null,
        lang: String? = null,
        sort: String? = null
    ): ApiResponseDto<SummaryListDataDto>

    suspend fun getSummaryById(id: Long): ApiResponseDto<SummaryDetailDataDto>

    suspend fun toggleFavorite(id: Long): ApiResponseDto<SuccessResponse>

    suspend fun updateSummary(id: Long, isRead: Boolean): ApiResponseDto<UpdateSummaryResponseDto>

    suspend fun deleteSummary(id: Long)
}
