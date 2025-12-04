package com.po4yka.bitesizereader.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.domain.usecase.GetCurrentUserUseCase
import com.po4yka.bitesizereader.domain.usecase.LoginWithTelegramUseCase
import com.po4yka.bitesizereader.domain.usecase.LogoutUseCase
import com.po4yka.bitesizereader.presentation.state.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginWithTelegramUseCase: LoginWithTelegramUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            _state.value = _state.value.copy(
                user = user,
                isAuthenticated = user != null
            )
        }
    }

    fun login(authData: AuthRequestDto) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                loginWithTelegramUseCase(authData)
                val user = getCurrentUserUseCase()
                _state.value = _state.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    user = user,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.value = _state.value.copy(isAuthenticated = false, user = null)
        }
    }
}
