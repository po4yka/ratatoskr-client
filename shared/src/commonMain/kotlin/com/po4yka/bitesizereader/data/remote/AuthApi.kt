package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.data.remote.dto.AuthResponseDto

interface AuthApi {
    suspend fun login(authRequest: AuthRequestDto): AuthResponseDto
    suspend fun refreshToken(refreshToken: String): AuthResponseDto
}
