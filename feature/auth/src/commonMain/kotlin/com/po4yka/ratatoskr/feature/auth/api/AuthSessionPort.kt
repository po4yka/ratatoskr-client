package com.po4yka.ratatoskr.feature.auth.api

import kotlinx.coroutines.flow.Flow

interface AuthSessionPort {
    val isAuthenticated: Flow<Boolean>

    suspend fun checkAuthStatus()

    suspend fun logout()
}
