package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.model.DeveloperCredentials
import com.po4yka.bitesizereader.domain.model.TelegramAuthData
import com.po4yka.bitesizereader.domain.usecase.ClearDeveloperCredentialsUseCase
import com.po4yka.bitesizereader.domain.usecase.GetCurrentUserUseCase
import com.po4yka.bitesizereader.domain.usecase.GetDeveloperCredentialsUseCase
import com.po4yka.bitesizereader.domain.usecase.LoginWithSecretUseCase
import com.po4yka.bitesizereader.domain.usecase.LoginWithTelegramUseCase
import com.po4yka.bitesizereader.domain.usecase.LogoutUseCase
import com.po4yka.bitesizereader.domain.usecase.SaveDeveloperCredentialsUseCase
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
    private val getDeveloperCredentialsUseCase: GetDeveloperCredentialsUseCase,
    private val saveDeveloperCredentialsUseCase: SaveDeveloperCredentialsUseCase,
    private val clearDeveloperCredentialsUseCase: ClearDeveloperCredentialsUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    init {
        checkAuthStatus()
        loadSavedDeveloperCredentials()
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

    private fun loadSavedDeveloperCredentials() {
        viewModelScope.launch {
            val credentials = getDeveloperCredentialsUseCase()
            _state.value = _state.value.copy(savedDeveloperCredentials = credentials)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun login(authData: TelegramAuthData) {
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
        rememberCredentials: Boolean = true,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                loginWithSecretUseCase(userId, clientId, secret)
                val user = getCurrentUserUseCase()

                // Save credentials if requested
                if (rememberCredentials) {
                    saveDeveloperCredentialsUseCase(userId, clientId, secret)
                }

                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user,
                        error = null,
                        savedDeveloperCredentials =
                            if (rememberCredentials) {
                                DeveloperCredentials(
                                    userId = userId,
                                    clientId = clientId,
                                    secret = secret,
                                )
                            } else {
                                _state.value.savedDeveloperCredentials
                            },
                    )
            } catch (e: Exception) {
                logger.error(e) { "Login with Secret failed" }
                _state.value = _state.value.copy(isLoading = false, error = e.toAppError().userMessage())
            }
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun logout(clearSavedCredentials: Boolean = false) {
        viewModelScope.launch {
            logoutUseCase()
            if (clearSavedCredentials) {
                clearDeveloperCredentialsUseCase()
            }
            _state.value =
                _state.value.copy(
                    isAuthenticated = false,
                    user = null,
                    savedDeveloperCredentials =
                        if (clearSavedCredentials) null else _state.value.savedDeveloperCredentials,
                )
        }
    }
}
