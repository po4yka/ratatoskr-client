package com.po4yka.bitesizereader.data.remote.api

import com.po4yka.bitesizereader.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Authentication API interface
 */
interface AuthApi {
    suspend fun loginWithTelegram(request: TelegramLoginRequestDto): ApiResponse<AuthResponseDto>
    suspend fun refreshToken(request: TokenRefreshRequestDto): ApiResponse<TokenRefreshResponseDto>
    suspend fun getCurrentUser(): ApiResponse<UserDto>
}

/**
 * Authentication API implementation
 */
class AuthApiImpl(
    private val client: HttpClient
) : AuthApi {

    override suspend fun loginWithTelegram(
        request: TelegramLoginRequestDto
    ): ApiResponse<AuthResponseDto> {
        return client.post("/v1/auth/telegram-login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun refreshToken(
        request: TokenRefreshRequestDto
    ): ApiResponse<TokenRefreshResponseDto> {
        return client.post("/v1/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getCurrentUser(): ApiResponse<UserDto> {
        return client.get("/v1/auth/me").body()
    }
}
