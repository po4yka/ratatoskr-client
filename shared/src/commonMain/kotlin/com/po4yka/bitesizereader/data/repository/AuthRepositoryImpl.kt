package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.remote.AuthApi
import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.domain.model.User
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val secureStorage: SecureStorage
) : AuthRepository {

    private val _isAuthenticated = MutableStateFlow(false)
    override val isAuthenticated: Flow<Boolean> = _isAuthenticated.asStateFlow()

    // Simple in-memory cache for user, or could be DB
    private var currentUser: User? = null

    // Initialize state
    suspend fun init() {
        _isAuthenticated.value = secureStorage.getAccessToken() != null
    }

    override suspend fun login(authData: AuthRequestDto) {
        val response = authApi.login(authData)
        secureStorage.saveAccessToken(response.accessToken)
        secureStorage.saveRefreshToken(response.refreshToken)
        
        currentUser = User(
            id = authData.id,
            username = authData.username ?: "",
            firstName = authData.firstName,
            lastName = authData.lastName,
            photoUrl = authData.photoUrl
        )
        
        _isAuthenticated.value = true
    }

    override suspend fun logout() {
        secureStorage.clearTokens()
        currentUser = null
        _isAuthenticated.value = false
    }

    override suspend fun getCurrentUser(): User? {
        return currentUser
    }
}