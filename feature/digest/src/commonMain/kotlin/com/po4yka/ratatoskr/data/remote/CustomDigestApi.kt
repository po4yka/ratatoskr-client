package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.CreateCustomDigestRequestDto
import com.po4yka.ratatoskr.data.remote.dto.CustomDigestListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.CustomDigestResponseDto

interface CustomDigestApi {
    suspend fun createCustomDigest(request: CreateCustomDigestRequestDto): ApiResponseDto<CustomDigestResponseDto>

    suspend fun getCustomDigest(id: String): ApiResponseDto<CustomDigestResponseDto>

    suspend fun getCustomDigests(
        page: Int,
        pageSize: Int,
    ): ApiResponseDto<CustomDigestListResponseDto>
}
