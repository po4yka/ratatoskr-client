package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.BaseResponse
import com.po4yka.bitesizereader.data.remote.dto.DeviceRegistrationPayload

interface NotificationsApi {
    suspend fun registerDevice(payload: DeviceRegistrationPayload): ApiResponseDto<BaseResponse>
}
