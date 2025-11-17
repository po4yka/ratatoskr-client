package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.repository.AuthRepository
import com.po4yka.bitesizereader.domain.usecase.LoginWithTelegramUseCase
import com.po4yka.bitesizereader.presentation.state.LoginState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for login screen
 */
class LoginViewModel(
    private val loginWithTelegramUseCase: LoginWithTelegramUseCase,
    private val authRepository: AuthRepository,
    private val viewModelScope: CoroutineScope,
) {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            val isAuthenticated = authRepository.isAuthenticated()
            _state.value = _state.value.copy(isAuthenticated = isAuthenticated)

            if (isAuthenticated) {
                loadCurrentUser()
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val result = authRepository.getCurrentUser()

            result.onSuccess { user ->
                _state.value = _state.value.copy(user = user)
            }
        }
    }

    fun loginWithTelegram(
        telegramUserId: Long,
        authHash: String,
        authDate: Long,
        username: String?,
        firstName: String?,
        lastName: String?,
        photoUrl: String?,
        clientId: String,
    ) {
        _state.value = _state.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result =
                loginWithTelegramUseCase(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    photoUrl = photoUrl,
                    clientId = clientId,
                )

            result.onSuccess { (_, user) ->
                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user,
                        error = null,
                    )
            }.onFailure { error ->
                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        error = error.message,
                    )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = LoginState()
        }
    }

    /**
     * Placeholder login method called by UI
     *
     * This method is called when the user taps the login button.
     * On Android: The UI launches a Custom Tab with TelegramAuthHelper
     * On iOS: The UI shows a sheet with TelegramAuthWebView
     *
     * The actual authentication happens through:
     * 1. Custom Tab (Android) or WKWebView (iOS) with Telegram login widget
     * 2. Deep link callback with auth data (handled by TelegramAuthActivity on Android)
     * 3. Calling loginWithTelegram(telegramUserId, ...) with the actual parameters
     *
     * Note: This method exists for backward compatibility and is not used in the actual flow.
     */
    fun loginWithTelegram() {
        // The UI handles launching the authentication flow directly
        // This method is kept for API compatibility
    }
}
