package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RequestCreatedDto
import com.po4yka.bitesizereader.data.remote.dto.RequestStatusDto

interface RequestsApi {
    suspend fun submitUrl(url: String, langPreference: String = "auto"): ApiResponseDto<RequestCreatedDto>
    suspend fun getRequestStatus(id: String): ApiResponseDto<RequestStatusDto>
}
