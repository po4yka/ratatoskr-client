package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.CreateHighlightRequestDto
import com.po4yka.bitesizereader.data.remote.dto.HighlightDeleteResponseDto
import com.po4yka.bitesizereader.data.remote.dto.HighlightListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.HighlightResponseDto
import com.po4yka.bitesizereader.data.remote.dto.UpdateHighlightRequestDto

interface HighlightsApi {
    suspend fun listHighlights(summaryId: Long): ApiResponseDto<HighlightListResponseDto>

    suspend fun createHighlight(
        summaryId: Long,
        request: CreateHighlightRequestDto,
    ): ApiResponseDto<HighlightResponseDto>

    suspend fun updateHighlight(
        summaryId: Long,
        highlightId: String,
        request: UpdateHighlightRequestDto,
    ): ApiResponseDto<HighlightResponseDto>

    suspend fun deleteHighlight(
        summaryId: Long,
        highlightId: String,
    ): ApiResponseDto<HighlightDeleteResponseDto>
}
