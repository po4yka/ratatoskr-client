package com.po4yka.ratatoskr.presentation.state

import com.po4yka.ratatoskr.domain.model.DeveloperCredentials
import com.po4yka.ratatoskr.domain.model.User

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isAuthenticated: Boolean = false,
    val savedDeveloperCredentials: DeveloperCredentials? = null,
)
