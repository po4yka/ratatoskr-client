package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.QuickSaveRequestDto
import com.po4yka.bitesizereader.data.remote.dto.QuickSaveResponseDto

interface QuickSaveApi {
    suspend fun quickSave(request: QuickSaveRequestDto): ApiResponseDto<QuickSaveResponseDto>
}
