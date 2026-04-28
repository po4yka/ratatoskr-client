package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.RequestDetailDto
import com.po4yka.ratatoskr.data.remote.dto.RequestStatusResponseDto
import com.po4yka.ratatoskr.data.remote.dto.RetryRequestResponseDto
import com.po4yka.ratatoskr.data.remote.dto.SubmitRequestResponseDto
import com.po4yka.ratatoskr.data.remote.dto.SubmitForwardRequestDto
import com.po4yka.ratatoskr.data.remote.dto.SubmitURLRequestDto

interface RequestsApi {
    suspend fun submitUrl(request: SubmitURLRequestDto): ApiResponseDto<SubmitRequestResponseDto>

    /** Submit a forwarded message for summarization. */
    suspend fun submitForward(request: SubmitForwardRequestDto): ApiResponseDto<SubmitRequestResponseDto>

    suspend fun getRequest(id: Long): ApiResponseDto<RequestDetailDto>

    suspend fun getRequestStatus(id: Long): ApiResponseDto<RequestStatusResponseDto>

    suspend fun retryRequest(id: Long): ApiResponseDto<RetryRequestResponseDto>
}
