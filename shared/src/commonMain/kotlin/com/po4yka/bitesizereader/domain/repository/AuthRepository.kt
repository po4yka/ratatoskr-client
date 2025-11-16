package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.AuthTokens
import com.po4yka.bitesizereader.domain.model.User

/**
 * Repository interface for Authentication operations
 */
interface AuthRepository {
    /**
     * Login with Telegram credentials
     */
    suspend fun loginWithTelegram(
        telegramUserId: Long,
        authHash: String,
        authDate: Long,
        username: String?,
        firstName: String?,
        lastName: String?,
        photoUrl: String?,
        clientId: String
    ): Result<Pair<AuthTokens, User>>

    /**
     * Refresh access token
     */
    suspend fun refreshToken(refreshToken: String): Result<AuthTokens>

    /**
     * Get current authenticated user
     */
    suspend fun getCurrentUser(): Result<User>

    /**
     * Check if user is authenticated
     */
    suspend fun isAuthenticated(): Boolean

    /**
     * Get stored tokens
     */
    suspend fun getTokens(): Pair<String, String>? // (accessToken, refreshToken)

    /**
     * Store tokens
     */
    suspend fun storeTokens(authTokens: AuthTokens)

    /**
     * Logout and clear tokens
     */
    suspend fun logout()
}
