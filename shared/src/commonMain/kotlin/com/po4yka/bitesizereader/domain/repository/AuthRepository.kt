package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(authData: AuthRequestDto)
    suspend fun loginWithSecret(userId: Int, clientId: String, secret: String)
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    val isAuthenticated: Flow<Boolean>
}
