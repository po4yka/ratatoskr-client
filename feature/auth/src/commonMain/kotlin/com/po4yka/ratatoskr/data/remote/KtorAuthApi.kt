package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.AppleLoginRequestDto
import com.po4yka.ratatoskr.data.remote.dto.AuthResponseDto
import com.po4yka.ratatoskr.data.remote.dto.GoogleLoginRequestDto
import com.po4yka.ratatoskr.data.remote.dto.LoginDataDto
import com.po4yka.ratatoskr.data.remote.dto.LogoutRequestDto
import com.po4yka.ratatoskr.data.remote.dto.SecretLoginRequestDto
import com.po4yka.ratatoskr.data.remote.dto.SessionListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.SuccessResponse
import com.po4yka.ratatoskr.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.ratatoskr.data.remote.dto.TokenRefreshRequestDto
import com.po4yka.ratatoskr.data.remote.dto.TokenRefreshResponseDto
import com.po4yka.ratatoskr.data.remote.dto.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.koin.core.annotation.Single

@Single(binds = [AuthApi::class])
class KtorAuthApi(private val client: HttpClient) : AuthApi {
    // ========================================================================
    // Login Methods
    // ========================================================================

    override suspend fun loginWithTelegram(request: TelegramLoginRequestDto): ApiResponseDto<AuthResponseDto> {
        return client.post("v1/auth/telegram-login") {
            setBody(request)
        }.body()
    }

    override suspend fun secretLogin(request: SecretLoginRequestDto): ApiResponseDto<AuthResponseDto> {
        return client.post("v1/auth/secret-login") {
            setBody(request)
        }.body()
    }

    override suspend fun loginWithApple(request: AppleLoginRequestDto): ApiResponseDto<LoginDataDto> {
        return client.post("v1/auth/apple-login") {
            setBody(request)
        }.body()
    }

    override suspend fun loginWithGoogle(request: GoogleLoginRequestDto): ApiResponseDto<LoginDataDto> {
        return client.post("v1/auth/google-login") {
            setBody(request)
        }.body()
    }

    // ========================================================================
    // Token Management
    // ========================================================================

    override suspend fun refreshToken(request: TokenRefreshRequestDto): ApiResponseDto<TokenRefreshResponseDto> {
        return client.post("v1/auth/refresh") {
            setBody(request)
        }.body()
    }

    override suspend fun logout(refreshToken: String): ApiResponseDto<SuccessResponse> {
        return client.post("v1/auth/logout") {
            setBody(LogoutRequestDto(refreshToken))
        }.body()
    }

    // ========================================================================
    // User & Session Management
    // ========================================================================

    override suspend fun getCurrentUser(): ApiResponseDto<UserDto> {
        return client.get("v1/auth/me").body()
    }

    override suspend fun deleteAccount(): ApiResponseDto<SuccessResponse> {
        return client.delete("v1/auth/me").body()
    }

    override suspend fun listSessions(): ApiResponseDto<SessionListResponseDto> {
        return client.get("v1/auth/sessions").body()
    }
}
