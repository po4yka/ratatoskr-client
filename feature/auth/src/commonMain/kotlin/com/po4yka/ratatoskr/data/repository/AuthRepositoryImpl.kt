package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.local.SecureStorage
import com.po4yka.ratatoskr.data.mappers.createTelegramLoginRequest
import com.po4yka.ratatoskr.data.mappers.toAuthTokens
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.AuthApi
import com.po4yka.ratatoskr.data.remote.dto.AppleLoginRequestDto
import com.po4yka.ratatoskr.data.remote.dto.GoogleLoginRequestDto
import com.po4yka.ratatoskr.data.remote.dto.SecretLoginRequestDto
import com.po4yka.ratatoskr.data.remote.dto.TokenRefreshRequestDto
import com.po4yka.ratatoskr.domain.model.AuthTokens
import com.po4yka.ratatoskr.domain.model.Session
import com.po4yka.ratatoskr.domain.model.TelegramAuthData
import com.po4yka.ratatoskr.domain.model.User
import com.po4yka.ratatoskr.domain.model.UserPreferences
import com.po4yka.ratatoskr.feature.auth.api.AuthSessionPort
import com.po4yka.ratatoskr.domain.repository.AuthRepository
import com.po4yka.ratatoskr.util.config.AppConfig
import com.po4yka.ratatoskr.util.error.AppError
import com.po4yka.ratatoskr.util.error.toAppError
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single(binds = [AuthRepository::class, AuthSessionPort::class])
class AuthRepositoryImpl(
    private val authApi: AuthApi,
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
        val response = authApi.loginWithTelegram(request)
        val loginData = response.data
        if (response.success && loginData != null) {
            val authTokens = loginData.toDomain()
            secureStorage.saveAccessToken(authTokens.accessToken)
            if (authTokens.refreshToken.isNotEmpty()) {
                secureStorage.saveRefreshToken(authTokens.refreshToken)
            }
            // Fetch current user on-demand
            _currentUser.value = null
            _isAuthenticated.value = true
        } else {
            throw response.error?.toAppError() ?: AppError.UnknownError(fallbackMessage = "Login failed")
        }
    }

    override suspend fun loginWithSecret(
        userId: Int,
        clientId: String,
        secret: String,
    ) {
        val request =
            SecretLoginRequestDto(
                userId = userId,
                clientId = clientId,
                secret = secret,
            )
        val response = authApi.secretLogin(request)
        val secretData = response.data
        if (response.success && secretData != null) {
            val authTokens = secretData.toDomain()
            secureStorage.saveAccessToken(authTokens.accessToken)
            if (authTokens.refreshToken.isNotEmpty()) {
                secureStorage.saveRefreshToken(authTokens.refreshToken)
            }
            // Fetch current user on-demand
            _currentUser.value = null
            _isAuthenticated.value = true
        } else {
            throw response.error?.toAppError() ?: AppError.UnknownError(fallbackMessage = "Login failed")
        }
    }

    override suspend fun logout() {
        secureStorage.clearTokens()
        _currentUser.value = null
        _isAuthenticated.value = false
    }

    override suspend fun getCurrentUser(): User? {
        if (_currentUser.value != null) return _currentUser.value

        // If not in memory, try to fetch from API if authenticated
        if (secureStorage.getAccessToken() != null) {
            return try {
                val response = authApi.getCurrentUser()
                val userData = response.data
                if (response.success && userData != null) {
                    _currentUser.value = userData.toDomain()
                    _currentUser.value
                } else {
                    null
                }
            } catch (e: kotlin.coroutines.cancellation.CancellationException) {
                // Rethrow cancellation - this is not a token error, just a coroutine being cancelled
                logger.debug { "getCurrentUser request was cancelled" }
                throw e
            } catch (e: Exception) {
                logger.error(e) { "Failed to get current user" }
                // Token might be expired or invalid, clear and force re-login
                secureStorage.clearTokens()
                _isAuthenticated.value = false
                null
            }
        }
        return null
    }

    suspend fun refreshAuthTokens(): AuthTokens? {
        val refreshToken = secureStorage.getRefreshToken()
        if (refreshToken != null) {
            return try {
                val response = authApi.refreshToken(TokenRefreshRequestDto(refreshToken))
                val refreshData = response.data
                if (response.success && refreshData != null) {
                    val authTokens =
                        refreshData.toAuthTokens(
                            currentTime = Clock.System.now(),
                            refreshToken = refreshToken,
                        )
                    secureStorage.saveAccessToken(authTokens.accessToken)
                    secureStorage.saveRefreshToken(authTokens.refreshToken)
                    _isAuthenticated.value = true
                    authTokens
                } else {
                    secureStorage.clearTokens()
                    _isAuthenticated.value = false
                    null
                }
            } catch (e: Exception) {
                logger.error { "Failed to refresh auth tokens: ${e.message}" }
                // Refresh failed, force re-login
                secureStorage.clearTokens()
                _isAuthenticated.value = false
                null
            }
        }
        return null
    }

    override suspend fun loginWithApple(
        idToken: String,
        clientId: String,
        authorizationCode: String?,
        givenName: String?,
        familyName: String?,
    ) {
        val request =
            AppleLoginRequestDto(
                idToken = idToken,
                clientId = clientId,
                authorizationCode = authorizationCode,
                givenName = givenName,
                familyName = familyName,
            )
        val response = authApi.loginWithApple(request)
        val appleData = response.data
        if (response.success && appleData != null) {
            val authTokens = appleData.toAuthTokens()
            secureStorage.saveAccessToken(authTokens.accessToken)
            if (authTokens.refreshToken.isNotEmpty()) {
                secureStorage.saveRefreshToken(authTokens.refreshToken)
            }
            _currentUser.value = appleData.user.toDomain()
            _isAuthenticated.value = true
        } else {
            throw response.error?.toAppError() ?: AppError.UnknownError(fallbackMessage = "Apple login failed")
        }
    }

    override suspend fun loginWithGoogle(
        idToken: String,
        clientId: String,
    ) {
        val request =
            GoogleLoginRequestDto(
                idToken = idToken,
                clientId = clientId,
            )
        val response = authApi.loginWithGoogle(request)
        val googleData = response.data
        if (response.success && googleData != null) {
            val authTokens = googleData.toAuthTokens()
            secureStorage.saveAccessToken(authTokens.accessToken)
            if (authTokens.refreshToken.isNotEmpty()) {
                secureStorage.saveRefreshToken(authTokens.refreshToken)
            }
            _currentUser.value = googleData.user.toDomain()
            _isAuthenticated.value = true
        } else {
            throw response.error?.toAppError() ?: AppError.UnknownError(fallbackMessage = "Google login failed")
        }
    }

    override suspend fun logoutWithRevoke() {
        val refreshToken = secureStorage.getRefreshToken()
        if (refreshToken != null) {
            try {
                authApi.logout(refreshToken)
            } catch (e: Exception) {
                logger.warn(e) { "Failed to revoke refresh token on server, proceeding with local logout" }
            }
        }
        logout()
    }

    override suspend fun listSessions(): List<Session> {
        val response = authApi.listSessions()
        val sessionsData = response.data
        if (response.success && sessionsData != null) {
            return sessionsData.sessions.map { it.toDomain() }
        } else {
            throw response.error?.toAppError() ?: AppError.UnknownError(fallbackMessage = "Failed to list sessions")
        }
    }

    override suspend fun deleteAccount() {
        val response = authApi.deleteAccount()
        if (response.success) {
            logout()
        } else {
            throw response.error?.toAppError() ?: AppError.UnknownError(fallbackMessage = "Failed to delete account")
        }
    }
}
