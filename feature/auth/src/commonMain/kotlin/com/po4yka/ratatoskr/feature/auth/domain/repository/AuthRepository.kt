package com.po4yka.ratatoskr.feature.auth.domain.repository

import com.po4yka.ratatoskr.domain.model.Session
import com.po4yka.ratatoskr.domain.model.TelegramAuthData
import com.po4yka.ratatoskr.domain.model.User
import com.po4yka.ratatoskr.domain.model.UserPreferences
import com.po4yka.ratatoskr.feature.auth.api.AuthSessionPort

interface AuthRepository : AuthSessionPort {
    /** Login with Telegram auth data */
    suspend fun login(authData: TelegramAuthData)

    /** Login with developer secret key */
    suspend fun loginWithSecret(
        userId: Int,
        clientId: String,
        secret: String,
    )

    /** Login with Apple Sign In */
    suspend fun loginWithApple(
        idToken: String,
        clientId: String,
        authorizationCode: String? = null,
        givenName: String? = null,
        familyName: String? = null,
    ): UserPreferences?

    /** Login with Google Sign In */
    suspend fun loginWithGoogle(
        idToken: String,
        clientId: String,
    ): UserPreferences?

    /** Logout locally (clear tokens) */
    override suspend fun logout()

    /** Logout and revoke refresh token on server */
    suspend fun logoutWithRevoke()

    /** Get current authenticated user */
    suspend fun getCurrentUser(): User?

    /** List all active sessions for the user */
    suspend fun listSessions(): List<Session>

    /** Delete user account permanently */
    suspend fun deleteAccount()

    /**
     * Check and update authentication status based on stored tokens.
     * Should be called during app initialization or when auth state needs refresh.
     */
    override suspend fun checkAuthStatus()
}
