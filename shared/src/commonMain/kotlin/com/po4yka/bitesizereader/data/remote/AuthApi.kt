package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.AuthResponseDto
import com.po4yka.bitesizereader.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TokenRefreshRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TokenRefreshResponseDto
import com.po4yka.bitesizereader.data.remote.dto.UserDto

interface AuthApi {
    suspend fun loginWithTelegram(request: TelegramLoginRequestDto): ApiResponseDto<AuthResponseDto>
    suspend fun refreshToken(request: TokenRefreshRequestDto): ApiResponseDto<TokenRefreshResponseDto>
    suspend fun getCurrentUser(): ApiResponseDto<UserDto>
}