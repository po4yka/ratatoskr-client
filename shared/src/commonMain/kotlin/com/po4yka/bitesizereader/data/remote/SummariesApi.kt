package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryContentDataDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryDetailDataDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryListDataDto
import com.po4yka.bitesizereader.data.remote.dto.SuccessResponse
import com.po4yka.bitesizereader.data.remote.dto.UpdateSummaryResponseDto

/**
 * Summaries API matching OpenAPI spec.
 */
interface SummariesApi {
    /**
     * Get paginated list of summaries with optional filters.
     *
     * @param page Page number (1-indexed)
     * @param pageSize Items per page (1-100)
     * @param isRead Filter by read status
     * @param isFavorited Filter by favorite status (new)
     * @param lang Filter by language (en/ru/auto)
     * @param startDate Filter by creation date (ISO 8601)
     * @param endDate Filter by creation date (ISO 8601)
     * @param sort Sort order (created_at_desc/created_at_asc)
     */
    suspend fun getSummaries(
        page: Int,
        pageSize: Int,
        isRead: Boolean? = null,
        isFavorited: Boolean? = null,
        lang: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        sort: String? = null,
    ): ApiResponseDto<SummaryListDataDto>

    suspend fun getSummaryById(id: Long): ApiResponseDto<SummaryDetailDataDto>

    /** Toggle favorite status for a summary */
    suspend fun toggleFavorite(id: Long): ApiResponseDto<SuccessResponse>

    suspend fun updateSummary(
        id: Long,
        isRead: Boolean,
    ): ApiResponseDto<UpdateSummaryResponseDto>

    suspend fun deleteSummary(id: Long)

    /**
     * Get full article content for offline reading.
     *
     * @param id Summary ID
     * @param format Content format: "markdown" (default) or "text"
     */
    suspend fun getContent(
        id: Long,
        format: String? = null,
    ): ApiResponseDto<SummaryContentDataDto>

    /** Look up a summary by original article URL. */
    suspend fun getSummaryByUrl(url: String): ApiResponseDto<SummaryDetailDataDto>
}
