package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.mappers.createTelegramLoginRequest
import com.po4yka.bitesizereader.data.mappers.toAuthTokens
import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.AuthApi
import com.po4yka.bitesizereader.data.remote.dto.AppleLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.data.remote.dto.GoogleLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SecretLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TokenRefreshRequestDto
import com.po4yka.bitesizereader.domain.model.AuthTokens
import com.po4yka.bitesizereader.domain.model.Session
import com.po4yka.bitesizereader.domain.model.User
import com.po4yka.bitesizereader.domain.model.UserPreferences
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import com.po4yka.bitesizereader.util.config.AppConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val secureStorage: SecureStorage,
    private val externalScope: CoroutineScope,
) : AuthRepository {
    private val _isAuthenticated = MutableStateFlow(false)
    override val isAuthenticated: Flow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: Flow<User?> = _currentUser.asStateFlow()

    init {
        // Initialize authentication status on startup in a coroutine
        externalScope.launch {
            checkAuthStatus()
        }
    }

    private suspend fun checkAuthStatus() {
        if (secureStorage.getAccessToken() != null) {
            _isAuthenticated.value = true
            // Attempt to get user info if tokens exist
            // This might need a separate call to `getCurrentUser()` or rely on it
        }
    }

    override suspend fun login(authData: AuthRequestDto) {
        val request =
            createTelegramLoginRequest(
                telegramUserId = authData.id.toLong(),
                authHash = authData.hash,
                authDate = authData.authDate,
                username = authData.username,
                firstName = authData.firstName,
                lastName = authData.lastName,
                photoUrl = authData.photoUrl,
                clientId = AppConfig.App.CLIENT_ID,
            )
        val response = authApi.loginWithTelegram(request)
        if (response.success && response.data != null) {
            val authTokens = response.data.toDomain()
            secureStorage.saveAccessToken(authTokens.accessToken)
            if (authTokens.refreshToken.isNotEmpty()) {
                secureStorage.saveRefreshToken(authTokens.refreshToken)
            }
            // Fetch current user on-demand
            _currentUser.value = null
            _isAuthenticated.value = true
        } else {
            throw response.error?.let { Exception(it.message) } ?: Exception("Login failed")
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
        if (response.success && response.data != null) {
            val authTokens = response.data.toDomain()
            secureStorage.saveAccessToken(authTokens.accessToken)
            if (authTokens.refreshToken.isNotEmpty()) {
                secureStorage.saveRefreshToken(authTokens.refreshToken)
            }
            // Fetch current user on-demand
            _currentUser.value = null
            _isAuthenticated.value = true
        } else {
            throw response.error?.let { Exception(it.message) } ?: Exception("Login failed")
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
                if (response.success && response.data != null) {
                    _currentUser.value = response.data.toDomain()
                    _currentUser.value
                } else {
                    null
                }
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
                if (response.success && response.data != null) {
                    val authTokens =
                        response.data.toAuthTokens(
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
                logger.error(e) { "Failed to refresh auth tokens" }
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
    ): UserPreferences? {
        val request =
            AppleLoginRequestDto(
                idToken = idToken,
                clientId = clientId,
                authorizationCode = authorizationCode,
                givenName = givenName,
                familyName = familyName,
            )
        val response = authApi.loginWithApple(request)
        if (response.success && response.data != null) {
            val loginData = response.data
            val authTokens = loginData.toAuthTokens()
            secureStorage.saveAccessToken(authTokens.accessToken)
            if (authTokens.refreshToken.isNotEmpty()) {
                secureStorage.saveRefreshToken(authTokens.refreshToken)
            }
            _currentUser.value = loginData.user.toDomain()
            _isAuthenticated.value = true
            return loginData.preferences?.toDomain()
        } else {
            throw response.error?.let { Exception(it.message) } ?: Exception("Apple login failed")
        }
    }

    override suspend fun loginWithGoogle(
        idToken: String,
        clientId: String,
    ): UserPreferences? {
        val request =
            GoogleLoginRequestDto(
                idToken = idToken,
                clientId = clientId,
            )
        val response = authApi.loginWithGoogle(request)
        if (response.success && response.data != null) {
            val loginData = response.data
            val authTokens = loginData.toAuthTokens()
            secureStorage.saveAccessToken(authTokens.accessToken)
            if (authTokens.refreshToken.isNotEmpty()) {
                secureStorage.saveRefreshToken(authTokens.refreshToken)
            }
            _currentUser.value = loginData.user.toDomain()
            _isAuthenticated.value = true
            return loginData.preferences?.toDomain()
        } else {
            throw response.error?.let { Exception(it.message) } ?: Exception("Google login failed")
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
        if (response.success && response.data != null) {
            return response.data.sessions.map { it.toDomain() }
        } else {
            throw response.error?.let { Exception(it.message) } ?: Exception("Failed to list sessions")
        }
    }

    override suspend fun deleteAccount() {
        val response = authApi.deleteAccount()
        if (response.success) {
            logout()
        } else {
            throw response.error?.let { Exception(it.message) } ?: Exception("Failed to delete account")
        }
    }
}
