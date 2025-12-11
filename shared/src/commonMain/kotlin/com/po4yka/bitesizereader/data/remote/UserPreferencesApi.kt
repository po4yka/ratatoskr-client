package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.UpdatePreferencesRequestDto
import com.po4yka.bitesizereader.data.remote.dto.UserPreferencesDto
import com.po4yka.bitesizereader.data.remote.dto.UserStatsDto

/**
 * User preferences and statistics API matching OpenAPI spec.
 */
interface UserPreferencesApi {
    /** Get current user preferences */
    suspend fun getPreferences(): ApiResponseDto<UserPreferencesDto>

    /** Update user preferences */
    suspend fun updatePreferences(request: UpdatePreferencesRequestDto): ApiResponseDto<UserPreferencesDto>

    /** Get user statistics */
    suspend fun getStats(): ApiResponseDto<UserStatsDto>
}
