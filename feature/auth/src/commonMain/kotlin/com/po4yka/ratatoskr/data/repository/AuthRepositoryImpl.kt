package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.AuthenticationApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.RefreshTokenRequest
import com.po4yka.ratatoskr.api.generated.models.SecretLoginRequest
import com.po4yka.ratatoskr.data.local.SecureStorage
import com.po4yka.ratatoskr.data.mappers.createTelegramLoginRequest
import com.po4yka.ratatoskr.data.mappers.toAuthTokens
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.domain.model.Session
import com.po4yka.ratatoskr.domain.model.TelegramAuthData
import com.po4yka.ratatoskr.domain.model.User
import com.po4yka.ratatoskr.domain.repository.AuthRepository
import com.po4yka.ratatoskr.feature.auth.api.AuthSessionPort
import com.po4yka.ratatoskr.util.config.AppConfig
import com.po4yka.ratatoskr.util.error.AppError
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single(binds = [AuthRepository::class, AuthSessionPort::class])
class AuthRepositoryImpl(
    private val secureStorage: SecureStorage,
) : AuthRepository {
    private val _isAuthenticated = MutableStateFlow(false)
    override val isAuthenticated: Flow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: Flow<User?> = _currentUser.asStateFlow()

    /**
     * Check authentication status based on stored tokens.
     * Should be called explicitly by ViewModels during initialization.
     */
    override suspend fun checkAuthStatus() {
        val hasToken = secureStorage.getAccessToken() != null
        _isAuthenticated.value = hasToken
        logger.debug { "Auth status checked: authenticated=$hasToken" }
    }

    override suspend fun login(authData: TelegramAuthData) {
        val request =
            createTelegramLoginRequest(
                telegramUserId = authData.id.toLong(),
                authHash = authData.hash,
                authDate = authData.authDate,
                username = authData.username,
                firstName = authData.firstName,
                lastName = authData.lastName,
                photoUrl = authData.photoUrl,
                clientId = AppConfig.App.clientId,
            )
        val loginData = AuthenticationApi.telegramLoginV1AuthTelegramLoginPost(request).unwrap().data
            ?: throw AppError.UnknownError(fallbackMessage = "Login failed")
        val authTokens = loginData.toAuthTokens()
        secureStorage.saveAccessToken(authTokens.accessToken)
        if (authTokens.refreshToken.isNotEmpty()) {
            secureStorage.saveRefreshToken(authTokens.refreshToken)
        }
        // Fetch current user on-demand
        _currentUser.value = null
        _isAuthenticated.value = true
    }

    override suspend fun loginWithSecret(
        userId: Int,
        clientId: String,
        secret: String,
    ) {
        val request =
            SecretLoginRequest(
                userId = userId.toLong(),
                clientId = clientId,
                secret = secret,
            )
        val tokens = AuthenticationApi.secretLoginV1AuthSecretLoginPost(request).unwrap().data
            ?: throw AppError.UnknownError(fallbackMessage = "Login failed")
        val authTokens = tokens.toDomain()
        secureStorage.saveAccessToken(authTokens.accessToken)
        if (authTokens.refreshToken.isNotEmpty()) {
            secureStorage.saveRefreshToken(authTokens.refreshToken)
        }
        // Fetch current user on-demand
        _currentUser.value = null
        _isAuthenticated.value = true
    }

    override suspend fun logout() {
        secureStorage.clearTokens()
        _currentUser.value = null
        _isAuthenticated.value = false
    }

    private suspend fun invalidateSession() {
        secureStorage.clearTokens()
        _isAuthenticated.value = false
    }

    override suspend fun getCurrentUser(): User? {
        if (_currentUser.value != null) return _currentUser.value

        // If not in memory, try to fetch from API if authenticated
        if (secureStorage.getAccessToken() != null) {
            return try {
                val user = AuthenticationApi.getCurrentUserInfoV1AuthMeGet().unwrap().data
                if (user != null) {
                    _currentUser.value = user.toDomain()
                    _currentUser.value
                } else {
                    null
                }
            } catch (e: kotlin.coroutines.cancellation.CancellationException) {
                logger.debug { "getCurrentUser request was cancelled" }
                throw e
            } catch (e: Exception) {
                logger.error(e) { "Failed to get current user" }
                invalidateSession()
                null
            }
        }
        return null
    }

    override suspend fun logoutWithRevoke() {
        val refreshToken = secureStorage.getRefreshToken()
        if (refreshToken != null) {
            try {
                AuthenticationApi.logoutV1AuthLogoutPost(RefreshTokenRequest(refreshToken = refreshToken))
                    .unwrap()
            } catch (e: Exception) {
                logger.warn(e) { "Failed to revoke refresh token on server, proceeding with local logout" }
            }
        }
        logout()
    }

    override suspend fun listSessions(): List<Session> {
        val data = AuthenticationApi.listSessionsV1AuthSessionsGet().unwrap().data
            ?: throw AppError.UnknownError(fallbackMessage = "Failed to list sessions")
        return data.sessions.map { it.toDomain() }
    }

    override suspend fun deleteAccount() {
        AuthenticationApi.deleteAccountV1AuthMeDelete().unwrap()
        logout()
    }
}
