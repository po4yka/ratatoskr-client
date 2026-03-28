package com.po4yka.bitesizereader.domain.port

import kotlinx.coroutines.flow.Flow

interface AuthSessionPort {
    val isAuthenticated: Flow<Boolean>

    suspend fun checkAuthStatus()
}
