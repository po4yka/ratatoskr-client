package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SuccessResponse

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
