package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RequestDetailDto
import com.po4yka.bitesizereader.data.remote.dto.RequestStatusResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RetryRequestResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SubmitRequestResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SubmitURLRequestDto

interface RequestsApi {
    suspend fun submitUrl(request: SubmitURLRequestDto): ApiResponseDto<SubmitRequestResponseDto>

    suspend fun getRequest(id: Long): ApiResponseDto<RequestDetailDto>

    suspend fun getRequestStatus(id: Long): ApiResponseDto<RequestStatusResponseDto>

    suspend fun retryRequest(id: Long): ApiResponseDto<RetryRequestResponseDto>
}
