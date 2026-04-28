package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.QuickSaveRequestDto
import com.po4yka.ratatoskr.data.remote.dto.QuickSaveResponseDto

interface QuickSaveApi {
    suspend fun quickSave(request: QuickSaveRequestDto): ApiResponseDto<QuickSaveResponseDto>
}
