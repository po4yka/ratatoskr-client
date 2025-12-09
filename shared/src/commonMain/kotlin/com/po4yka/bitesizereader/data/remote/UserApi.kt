package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.TelegramLinkBeginResponseDto
import com.po4yka.bitesizereader.data.remote.dto.TelegramLinkCompleteRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TelegramLinkStatusDto

interface UserApi {
    suspend fun getTelegramLinkStatus(): ApiResponseDto<TelegramLinkStatusDto>

    suspend fun unlinkTelegram(): ApiResponseDto<TelegramLinkStatusDto>

    suspend fun beginTelegramLink(): ApiResponseDto<TelegramLinkBeginResponseDto>

    suspend fun completeTelegramLink(request: TelegramLinkCompleteRequestDto): ApiResponseDto<TelegramLinkStatusDto>
}
