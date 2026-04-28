package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.TelegramLinkBeginResponseDto
import com.po4yka.ratatoskr.data.remote.dto.TelegramLinkCompleteRequestDto
import com.po4yka.ratatoskr.data.remote.dto.TelegramLinkStatusDto

interface TelegramLinkApi {
    suspend fun getTelegramLinkStatus(): ApiResponseDto<TelegramLinkStatusDto>

    suspend fun unlinkTelegram(): ApiResponseDto<TelegramLinkStatusDto>

    suspend fun beginTelegramLink(): ApiResponseDto<TelegramLinkBeginResponseDto>

    suspend fun completeTelegramLink(request: TelegramLinkCompleteRequestDto): ApiResponseDto<TelegramLinkStatusDto>
}
