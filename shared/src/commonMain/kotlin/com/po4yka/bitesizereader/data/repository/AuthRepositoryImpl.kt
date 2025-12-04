package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.mappers.createTelegramLoginRequest
import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.AuthApi
import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TokenRefreshRequestDto
import com.po4yka.bitesizereader.domain.model.AuthTokens
import com.po4yka.bitesizereader.domain.model.User
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import com.po4yka.bitesizereader.util.error.toAppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.time.Clock
import com.po4yka.bitesizereader.data.mappers.toAuthTokens

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val secureStorage: SecureStorage
) : AuthRepository {

    private val _isAuthenticated = MutableStateFlow(false)
    override val isAuthenticated: Flow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: Flow<User?> = _currentUser.asStateFlow()

    init {
        // Initialize authentication status on startup in a coroutine
        GlobalScope.launch { // Use GlobalScope as init block is not a suspend context
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
        val request = createTelegramLoginRequest(
            telegramUserId = authData.id.toLong(),
            authHash = authData.hash,
            authDate = authData.authDate,
            username = authData.username,
            firstName = authData.firstName,
            lastName = authData.lastName,
            photoUrl = authData.photoUrl,
            clientId = "android-app" // TODO: From AppConfig
        )
        val response = authApi.loginWithTelegram(request)
        if (response.success && response.data != null) {
            val (authTokens, user) = response.data.toDomain()
            secureStorage.saveAccessToken(authTokens.accessToken)
            secureStorage.saveRefreshToken(authTokens.refreshToken)
            _currentUser.value = user
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
                    val authTokens = response.data.toAuthTokens(Clock.System.now())
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
                // Refresh failed, force re-login
                secureStorage.clearTokens()
                _isAuthenticated.value = false
                null
            }
        }
        return null
    }
}
