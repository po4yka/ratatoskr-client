package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.domain.model.DeveloperCredentials
import com.po4yka.ratatoskr.domain.model.TelegramAuthData
import com.po4yka.ratatoskr.domain.usecase.ClearDeveloperCredentialsUseCase
import com.po4yka.ratatoskr.domain.usecase.GetCurrentUserUseCase
import com.po4yka.ratatoskr.domain.usecase.GetDeveloperCredentialsUseCase
import com.po4yka.ratatoskr.domain.usecase.LoginWithSecretUseCase
import com.po4yka.ratatoskr.domain.usecase.LoginWithTelegramUseCase
import com.po4yka.ratatoskr.domain.usecase.LogoutUseCase
import com.po4yka.ratatoskr.domain.usecase.SaveDeveloperCredentialsUseCase
import com.po4yka.ratatoskr.presentation.state.AuthState
import com.po4yka.ratatoskr.util.error.runCatchingDomain
import com.po4yka.ratatoskr.util.error.toAppError
import com.po4yka.ratatoskr.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

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
            runCatchingDomain { getCurrentUserUseCase() }
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            user = user,
                            isAuthenticated = user != null,
                        )
                    }
                }
                .onFailure { e ->
                    logger.error(e) { "Failed to check auth status" }
                    _state.update {
                        it.copy(
                            user = null,
                            isAuthenticated = false,
                        )
                    }
                }
        }
    }

    private fun loadSavedDeveloperCredentials() {
        viewModelScope.launch {
            runCatchingDomain { getDeveloperCredentialsUseCase() }
                .onSuccess { credentials ->
                    _state.update { it.copy(savedDeveloperCredentials = credentials) }
                }
                .onFailure { e ->
                    logger.error { "Failed to load saved developer credentials: ${e.message}" }
                }
        }
    }

    fun login(authData: TelegramAuthData) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatchingDomain {
                loginWithTelegramUseCase(authData)
                getCurrentUserUseCase()
            }
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            user = user,
                            error = null,
                        )
                    }
                }
                .onFailure { e ->
                    logger.error(e) { "Login with Telegram failed" }
                    _state.update { it.copy(isLoading = false, error = e.toAppError().userMessage()) }
                }
        }
    }

    fun loginWithSecret(
        userId: Int,
        clientId: String,
        secret: String,
        rememberCredentials: Boolean = true,
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatchingDomain {
                loginWithSecretUseCase(userId, clientId, secret)
                val user = getCurrentUserUseCase()
                if (rememberCredentials) {
                    saveDeveloperCredentialsUseCase(userId, clientId, secret)
                }
                user
            }
                .onSuccess { user ->
                    _state.update {
                        it.copy(
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
                                    it.savedDeveloperCredentials
                                },
                        )
                    }
                }
                .onFailure { e ->
                    logger.error { "Login with Secret failed: ${e.message}" }
                    _state.update { it.copy(isLoading = false, error = e.toAppError().userMessage()) }
                }
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun logout(clearSavedCredentials: Boolean = false) {
        viewModelScope.launch {
            runCatchingDomain {
                logoutUseCase()
                if (clearSavedCredentials) {
                    clearDeveloperCredentialsUseCase()
                }
            }
                .onFailure { e -> logger.error(e) { "Logout failed" } }
            _state.update {
                it.copy(
                    isAuthenticated = false,
                    user = null,
                    savedDeveloperCredentials =
                        if (clearSavedCredentials) null else it.savedDeveloperCredentials,
                )
            }
        }
    }
}
