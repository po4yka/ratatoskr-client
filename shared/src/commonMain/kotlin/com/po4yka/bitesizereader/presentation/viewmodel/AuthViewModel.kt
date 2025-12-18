package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.domain.usecase.GetCurrentUserUseCase
import com.po4yka.bitesizereader.domain.usecase.LoginWithSecretUseCase
import com.po4yka.bitesizereader.domain.usecase.LoginWithTelegramUseCase
import com.po4yka.bitesizereader.domain.usecase.LogoutUseCase
import com.po4yka.bitesizereader.presentation.state.AuthState
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class AuthViewModel(
    private val loginWithTelegramUseCase: LoginWithTelegramUseCase,
    private val loginWithSecretUseCase: LoginWithSecretUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            _state.value =
                _state.value.copy(
                    user = user,
                    isAuthenticated = user != null,
                )
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun login(authData: AuthRequestDto) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                loginWithTelegramUseCase(authData)
                val user = getCurrentUserUseCase()
                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user,
                        error = null,
                    )
            } catch (e: Exception) {
                logger.error(e) { "Login with Telegram failed" }
                _state.value = _state.value.copy(isLoading = false, error = e.toAppError().userMessage())
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun loginWithSecret(
        userId: Int,
        clientId: String,
        secret: String,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                loginWithSecretUseCase(userId, clientId, secret)
                val user = getCurrentUserUseCase()
                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user,
                        error = null,
                    )
            } catch (e: Exception) {
                logger.error(e) { "Login with Secret failed" }
                _state.value = _state.value.copy(isLoading = false, error = e.toAppError().userMessage())
            }
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.value = _state.value.copy(isAuthenticated = false, user = null)
        }
    }
}
