package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.User

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isAuthenticated: Boolean = false
)
