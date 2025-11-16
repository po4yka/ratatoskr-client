package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.User

/**
 * UI state for login screen
 */
data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val user: User? = null
)
