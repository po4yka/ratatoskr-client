package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.domain.usecase.DeleteAccountUseCase
import com.po4yka.ratatoskr.domain.usecase.GetUserPreferencesUseCase
import com.po4yka.ratatoskr.domain.usecase.GetUserStatsUseCase
import com.po4yka.ratatoskr.domain.usecase.ListSessionsUseCase
import com.po4yka.ratatoskr.domain.usecase.UpdateUserPreferencesUseCase
import com.po4yka.ratatoskr.presentation.state.AccountSettingsState
import com.po4yka.ratatoskr.util.error.toAppError
import com.po4yka.ratatoskr.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class AccountSettingsDelegate(
    private val getUserStatsUseCase: GetUserStatsUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateUserPreferencesUseCase: UpdateUserPreferencesUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val listSessionsUseCase: ListSessionsUseCase,
) {
    fun loadUserStats(
        scope: CoroutineScope,
        currentState: () -> AccountSettingsState,
        onState: (AccountSettingsState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isLoadingStats = true))
            runCatching { getUserStatsUseCase() }
                .onSuccess { stats ->
                    onState(currentState().copy(isLoadingStats = false, userStats = stats))
                }
                .onFailure { throwable ->
                    logger.warn(throwable) { "Failed to load user stats" }
                    onState(currentState().copy(isLoadingStats = false))
                }
        }
    }

    fun loadPreferences(
        scope: CoroutineScope,
        currentState: () -> AccountSettingsState,
        onState: (AccountSettingsState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isLoadingPreferences = true))
            runCatching { getUserPreferencesUseCase() }
                .onSuccess { prefs ->
                    onState(
                        currentState().copy(
                            isLoadingPreferences = false,
                            userPreferences = prefs,
                        ),
                    )
                }
                .onFailure { throwable ->
                    logger.warn(throwable) { "Failed to load user preferences" }
                    onState(currentState().copy(isLoadingPreferences = false))
                }
        }
    }

    fun updateLanguagePreference(
        lang: String,
        scope: CoroutineScope,
        currentState: () -> AccountSettingsState,
        onState: (AccountSettingsState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isSavingPreferences = true))
            runCatching { updateUserPreferencesUseCase(langPreference = lang) }
                .onSuccess { prefs ->
                    onState(
                        currentState().copy(
                            isSavingPreferences = false,
                            userPreferences = prefs,
                        ),
                    )
                }
                .onFailure { throwable ->
                    logger.warn(throwable) { "Failed to update language preference" }
                    onState(currentState().copy(isSavingPreferences = false))
                }
        }
    }

    fun showDeleteConfirmation(
        currentState: () -> AccountSettingsState,
        onState: (AccountSettingsState) -> Unit,
    ) {
        onState(currentState().copy(showDeleteConfirmation = true, deleteError = null))
    }

    fun hideDeleteConfirmation(
        currentState: () -> AccountSettingsState,
        onState: (AccountSettingsState) -> Unit,
    ) {
        onState(currentState().copy(showDeleteConfirmation = false, deleteError = null))
    }

    fun deleteAccount(
        scope: CoroutineScope,
        currentState: () -> AccountSettingsState,
        onState: (AccountSettingsState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isDeleting = true, deleteError = null))
            runCatching { deleteAccountUseCase() }
                .onSuccess {
                    logger.info { "Account deleted successfully" }
                    onState(
                        currentState().copy(
                            isDeleting = false,
                            showDeleteConfirmation = false,
                        ),
                    )
                }
                .onFailure { throwable ->
                    logger.error(throwable) { "Failed to delete account" }
                    onState(
                        currentState().copy(
                            isDeleting = false,
                            deleteError = throwable.toAppError().userMessage(),
                        ),
                    )
                }
        }
    }

    fun loadSessions(
        scope: CoroutineScope,
        currentState: () -> AccountSettingsState,
        onState: (AccountSettingsState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isLoadingSessions = true))
            runCatching { listSessionsUseCase() }
                .onSuccess { sessions ->
                    onState(
                        currentState().copy(
                            isLoadingSessions = false,
                            sessions = sessions,
                        ),
                    )
                }
                .onFailure { throwable ->
                    logger.warn(throwable) { "Failed to load sessions" }
                    onState(currentState().copy(isLoadingSessions = false))
                }
        }
    }

    fun toggleSessionsExpanded(
        scope: CoroutineScope,
        currentState: () -> AccountSettingsState,
        onState: (AccountSettingsState) -> Unit,
    ) {
        val state = currentState()
        val currentlyExpanded = state.sessionsExpanded
        onState(state.copy(sessionsExpanded = !currentlyExpanded))
        if (!currentlyExpanded && state.sessions.isEmpty()) {
            loadSessions(scope, currentState, onState)
        }
    }
}
