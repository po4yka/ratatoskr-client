package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.AuthResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SecretLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TokenRefreshRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TokenRefreshResponseDto
import com.po4yka.bitesizereader.data.remote.dto.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class KtorAuthApi(private val client: HttpClient) : AuthApi {
    override suspend fun loginWithTelegram(request: TelegramLoginRequestDto): ApiResponseDto<AuthResponseDto> {
        return client.post("auth/telegram-login") {
            setBody(request)
        }.body()
    }

    override suspend fun secretLogin(request: SecretLoginRequestDto): ApiResponseDto<AuthResponseDto> {
        return client.post("auth/secret-login") {
            setBody(request)
        }.body()
    }

    override suspend fun refreshToken(request: TokenRefreshRequestDto): ApiResponseDto<TokenRefreshResponseDto> {
        return client.post("auth/refresh") {
            setBody(request)
        }.body()
    }

    override suspend fun getCurrentUser(): ApiResponseDto<UserDto> {
        return client.get("auth/me").body()
    }
}
