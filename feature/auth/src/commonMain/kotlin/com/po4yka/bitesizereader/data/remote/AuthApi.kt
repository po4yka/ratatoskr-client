package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.AppleLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.AuthResponseDto
import com.po4yka.bitesizereader.data.remote.dto.GoogleLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.LoginDataDto
import com.po4yka.bitesizereader.data.remote.dto.SecretLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SessionListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SuccessResponse
import com.po4yka.bitesizereader.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TokenRefreshRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TokenRefreshResponseDto
import com.po4yka.bitesizereader.data.remote.dto.UserDto

/**
 * Authentication API matching OpenAPI spec.
 */
interface AuthApi {
    // ========================================================================
    // Login Methods
    // ========================================================================

    suspend fun loginWithTelegram(request: TelegramLoginRequestDto): ApiResponseDto<AuthResponseDto>

    suspend fun secretLogin(request: SecretLoginRequestDto): ApiResponseDto<AuthResponseDto>

    /** Apple Sign In - returns login data with tokens, user, and preferences */
    suspend fun loginWithApple(request: AppleLoginRequestDto): ApiResponseDto<LoginDataDto>

    /** Google Sign In - returns login data with tokens, user, and preferences */
    suspend fun loginWithGoogle(request: GoogleLoginRequestDto): ApiResponseDto<LoginDataDto>

    // ========================================================================
    // Token Management
    // ========================================================================

    suspend fun refreshToken(request: TokenRefreshRequestDto): ApiResponseDto<TokenRefreshResponseDto>

    /** Logout - revokes the specified refresh token */
    suspend fun logout(refreshToken: String): ApiResponseDto<SuccessResponse>

    // ========================================================================
    // User & Session Management
    // ========================================================================

    suspend fun getCurrentUser(): ApiResponseDto<UserDto>

    /** Delete the current user's account and all associated data */
    suspend fun deleteAccount(): ApiResponseDto<SuccessResponse>

    /** List all active sessions for the current user */
    suspend fun listSessions(): ApiResponseDto<SessionListResponseDto>
}
