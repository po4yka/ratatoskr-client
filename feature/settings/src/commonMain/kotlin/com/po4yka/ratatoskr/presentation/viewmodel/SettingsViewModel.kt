package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.domain.model.TelegramLinkData
import com.po4yka.ratatoskr.presentation.state.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val telegramDelegate: TelegramLinkingDelegate,
    private val syncDelegate: SyncSettingsDelegate,
    private val accountDelegate: AccountSettingsDelegate,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadLinkStatus()
        observeSyncProgress()
        loadUserStats()
        loadPreferences()
        loadCacheSize()
    }

    // Telegram linking

    fun loadLinkStatus() {
        telegramDelegate.loadLinkStatus(
            viewModelScope,
            currentState = { _state.value.telegram },
        ) { subState -> _state.update { it.copy(telegram = subState) } }
    }

    fun unlinkTelegram() {
        telegramDelegate.unlinkTelegram(
            viewModelScope,
            currentState = { _state.value.telegram },
        ) { subState -> _state.update { it.copy(telegram = subState) } }
    }

    fun beginTelegramLink() {
        telegramDelegate.beginTelegramLink(
            viewModelScope,
            currentState = { _state.value.telegram },
        ) { subState -> _state.update { it.copy(telegram = subState) } }
    }

    fun cancelTelegramLink() {
        telegramDelegate.cancelTelegramLink(
            currentState = { _state.value.telegram },
        ) { subState -> _state.update { it.copy(telegram = subState) } }
    }

    @Suppress("unused")
    fun completeTelegramLink(telegramAuth: TelegramLinkData) {
        telegramDelegate.completeTelegramLink(
            telegramAuth,
            viewModelScope,
            currentState = { _state.value.telegram },
        ) { subState -> _state.update { it.copy(telegram = subState) } }
    }

    // Sync & data management

    private fun observeSyncProgress() {
        syncDelegate.observeSyncProgress(
            viewModelScope,
            currentState = { _state.value.sync },
        ) { subState -> _state.update { it.copy(sync = subState) } }
    }

    fun importFromBackend() {
        syncDelegate.importFromBackend(
            viewModelScope,
            currentState = { _state.value.sync },
        ) { subState -> _state.update { it.copy(sync = subState) } }
    }

    fun cancelSync() {
        syncDelegate.cancelSync(
            currentState = { _state.value.sync },
        )
    }

    fun toggleRequestsExpanded() {
        syncDelegate.toggleRequestsExpanded(
            currentState = { _state.value.sync },
            onState = { subState -> _state.update { it.copy(sync = subState) } },
            scope = viewModelScope,
        )
    }

    fun retryRequest(request: Request) {
        syncDelegate.retryRequest(request, viewModelScope)
    }

    private fun loadCacheSize() {
        syncDelegate.loadCacheSize(
            viewModelScope,
            currentState = { _state.value.sync },
        ) { subState -> _state.update { it.copy(sync = subState) } }
    }

    fun clearContentCache() {
        syncDelegate.clearContentCache(
            viewModelScope,
            currentState = { _state.value.sync },
        ) { subState -> _state.update { it.copy(sync = subState) } }
    }

    // Account settings

    fun loadUserStats() {
        accountDelegate.loadUserStats(
            viewModelScope,
            currentState = { _state.value.account },
        ) { subState -> _state.update { it.copy(account = subState) } }
    }

    private fun loadPreferences() {
        accountDelegate.loadPreferences(
            viewModelScope,
            currentState = { _state.value.account },
        ) { subState -> _state.update { it.copy(account = subState) } }
    }

    fun updateLanguagePreference(lang: String) {
        accountDelegate.updateLanguagePreference(
            lang,
            viewModelScope,
            currentState = { _state.value.account },
        ) { subState -> _state.update { it.copy(account = subState) } }
    }

    fun showDeleteConfirmation() {
        accountDelegate.showDeleteConfirmation(
            currentState = { _state.value.account },
        ) { subState -> _state.update { it.copy(account = subState) } }
    }

    fun hideDeleteConfirmation() {
        accountDelegate.hideDeleteConfirmation(
            currentState = { _state.value.account },
        ) { subState -> _state.update { it.copy(account = subState) } }
    }

    fun deleteAccount() {
        accountDelegate.deleteAccount(
            viewModelScope,
            currentState = { _state.value.account },
        ) { subState -> _state.update { it.copy(account = subState) } }
    }

    fun toggleSessionsExpanded() {
        accountDelegate.toggleSessionsExpanded(
            viewModelScope,
            currentState = { _state.value.account },
        ) { subState -> _state.update { it.copy(account = subState) } }
    }

    fun loadSessions() {
        accountDelegate.loadSessions(
            viewModelScope,
            currentState = { _state.value.account },
        ) { subState -> _state.update { it.copy(account = subState) } }
    }
}
