package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.SuccessResponse

/**
 * Push notification device registration API.
 */
interface NotificationsApi {
    /** Register or update a device push token. */
    suspend fun registerDevice(
        token: String,
        platform: String,
        deviceId: String? = null,
    ): ApiResponseDto<SuccessResponse>
}
