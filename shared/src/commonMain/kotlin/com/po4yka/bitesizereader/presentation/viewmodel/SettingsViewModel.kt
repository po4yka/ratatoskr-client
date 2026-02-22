package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.TelegramLinkData
import com.po4yka.bitesizereader.domain.model.SyncPhase
import com.po4yka.bitesizereader.domain.model.TelegramLinkStatus
import com.po4yka.bitesizereader.presentation.state.SettingsState
import com.po4yka.bitesizereader.domain.usecase.DeleteAccountUseCase
import com.po4yka.bitesizereader.domain.usecase.GetRequestsUseCase
import com.po4yka.bitesizereader.domain.usecase.GetTelegramLinkStatusUseCase
import com.po4yka.bitesizereader.domain.usecase.GetUserPreferencesUseCase
import com.po4yka.bitesizereader.domain.usecase.GetUserStatsUseCase
import com.po4yka.bitesizereader.domain.usecase.LinkTelegramUseCase
import com.po4yka.bitesizereader.domain.usecase.ListSessionsUseCase
import com.po4yka.bitesizereader.domain.usecase.RetryRequestUseCase
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import com.po4yka.bitesizereader.domain.usecase.UnlinkTelegramUseCase
import com.po4yka.bitesizereader.domain.usecase.UpdateUserPreferencesUseCase
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class SettingsViewModel(
    private val getTelegramLinkStatusUseCase: GetTelegramLinkStatusUseCase,
    private val unlinkTelegramUseCase: UnlinkTelegramUseCase,
    private val linkTelegramUseCase: LinkTelegramUseCase,
    private val syncDataUseCase: SyncDataUseCase,
    private val getUserStatsUseCase: GetUserStatsUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val listSessionsUseCase: ListSessionsUseCase,
    private val getRequestsUseCase: GetRequestsUseCase,
    private val retryRequestUseCase: RetryRequestUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateUserPreferencesUseCase: UpdateUserPreferencesUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private var downloadJob: Job? = null

    init {
        loadLinkStatus()
        observeSyncProgress()
        loadUserStats()
        loadPreferences()
    }

    private fun observeSyncProgress() {
        syncDataUseCase.syncProgress
            .onEach { progress ->
                _state.value =
                    _state.value.copy(
                        syncProgress = progress,
                        isDownloading = progress?.isInProgress == true,
                    )
            }
            .launchIn(viewModelScope)
    }

    fun loadUserStats() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingStats = true)
            runCatching { getUserStatsUseCase() }
                .onSuccess { stats ->
                    _state.value = _state.value.copy(isLoadingStats = false, userStats = stats)
                }
                .onFailure { throwable ->
                    logger.warn(throwable) { "Failed to load user stats" }
                    _state.value = _state.value.copy(isLoadingStats = false)
                }
        }
    }

    fun loadLinkStatus() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching { getTelegramLinkStatusUseCase() }
                .onSuccess { status ->
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            linkStatus = status,
                            // Clear nonce if linking was successful
                            linkNonce = if (status.linked) null else _state.value.linkNonce,
                        )
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(isLoading = false, error = throwable.message)
                }
        }
    }

    fun unlinkTelegram() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching { unlinkTelegramUseCase() }
                .onSuccess { status ->
                    _state.value = _state.value.copy(isLoading = false, linkStatus = status)
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(isLoading = false, error = throwable.message)
                }
        }
    }

    fun beginTelegramLink() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching { linkTelegramUseCase.begin() }
                .onSuccess { nonce ->
                    _state.value = _state.value.copy(isLoading = false, linkNonce = nonce)
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(isLoading = false, error = throwable.message)
                }
        }
    }

    /**
     * Cancel the Telegram linking process and clear the nonce.
     */
    fun cancelTelegramLink() {
        _state.value = _state.value.copy(linkNonce = null)
    }

    @Suppress("unused")
    fun completeTelegramLink(telegramAuth: TelegramLinkData) {
        val nonce = _state.value.linkNonce ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching { linkTelegramUseCase.complete(nonce, telegramAuth) }
                .onSuccess { status ->
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            linkStatus = status,
                            linkNonce = null, // Reset nonce after success
                        )
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(isLoading = false, error = throwable.message)
                }
        }
    }

    fun importFromBackend() {
        if (_state.value.isDownloading) {
            logger.warn { "importFromBackend called but sync is already in progress" }
            return
        }

        logger.info { "Starting database synchronization (IMPORT mode). Force full sync." }
        downloadJob?.cancel()
        downloadJob =
            viewModelScope.launch {
                _state.value = _state.value.copy(downloadError = null)

                runCatching {
                    syncDataUseCase(forceFull = true)
                }.onSuccess {
                    logger.info { "Sync (Import) completed successfully." }
                    // Note: isDownloading is updated by observeSyncProgress
                }.onFailure { throwable ->
                    logger.error(throwable) { "Failed to sync/import" }
                    _state.value =
                        _state.value.copy(
                            downloadError = throwable.toAppError().userMessage(),
                        )
                }
            }
    }

    /**
     * Cancel the current sync operation if one is in progress.
     */
    fun cancelSync() {
        if (_state.value.isDownloading) {
            logger.info { "Cancelling sync operation" }
            downloadJob?.cancel()
            syncDataUseCase.cancelSync()
        }
    }

    // User preferences

    private fun loadPreferences() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingPreferences = true)
            runCatching { getUserPreferencesUseCase() }
                .onSuccess { prefs ->
                    _state.value = _state.value.copy(
                        isLoadingPreferences = false,
                        userPreferences = prefs,
                    )
                }
                .onFailure { throwable ->
                    logger.warn(throwable) { "Failed to load user preferences" }
                    _state.value = _state.value.copy(isLoadingPreferences = false)
                }
        }
    }

    fun updateLanguagePreference(lang: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSavingPreferences = true)
            runCatching { updateUserPreferencesUseCase(langPreference = lang) }
                .onSuccess { prefs ->
                    _state.value = _state.value.copy(
                        isSavingPreferences = false,
                        userPreferences = prefs,
                    )
                }
                .onFailure { throwable ->
                    logger.warn(throwable) { "Failed to update language preference" }
                    _state.value = _state.value.copy(isSavingPreferences = false)
                }
        }
    }

    // Account deletion

    fun showDeleteConfirmation() {
        _state.value = _state.value.copy(showDeleteConfirmation = true, deleteError = null)
    }

    fun hideDeleteConfirmation() {
        _state.value = _state.value.copy(showDeleteConfirmation = false, deleteError = null)
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isDeleting = true, deleteError = null)
            runCatching { deleteAccountUseCase() }
                .onSuccess {
                    logger.info { "Account deleted successfully" }
                    _state.value =
                        _state.value.copy(
                            isDeleting = false,
                            showDeleteConfirmation = false,
                        )
                    // Note: Auth state change will navigate to login screen
                }
                .onFailure { throwable ->
                    logger.error(throwable) { "Failed to delete account" }
                    _state.value =
                        _state.value.copy(
                            isDeleting = false,
                            deleteError = throwable.toAppError().userMessage(),
                        )
                }
        }
    }

    // Session management

    fun toggleSessionsExpanded() {
        val currentlyExpanded = _state.value.sessionsExpanded
        _state.value = _state.value.copy(sessionsExpanded = !currentlyExpanded)
        if (!currentlyExpanded && _state.value.sessions.isEmpty()) {
            loadSessions()
        }
    }

    fun loadSessions() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingSessions = true)
            runCatching { listSessionsUseCase() }
                .onSuccess { sessions ->
                    _state.value =
                        _state.value.copy(
                            isLoadingSessions = false,
                            sessions = sessions,
                        )
                }
                .onFailure { throwable ->
                    logger.warn(throwable) { "Failed to load sessions" }
                    _state.value = _state.value.copy(isLoadingSessions = false)
                }
        }
    }

    // Request history management

    fun toggleRequestsExpanded() {
        val currentlyExpanded = _state.value.requestsExpanded
        _state.value = _state.value.copy(requestsExpanded = !currentlyExpanded)
        if (!currentlyExpanded && _state.value.requests.isEmpty()) {
            loadRequests()
        }
    }

    private fun loadRequests() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingRequests = true)
            getRequestsUseCase()
                .catch { throwable ->
                    logger.warn(throwable) { "Failed to load requests" }
                    _state.value = _state.value.copy(isLoadingRequests = false)
                }
                .collect { requests ->
                    _state.value =
                        _state.value.copy(
                            isLoadingRequests = false,
                            requests = requests,
                        )
                }
        }
    }

    fun retryRequest(request: Request) {
        viewModelScope.launch {
            runCatching { retryRequestUseCase(request) }
                .onSuccess {
                    logger.info { "Retried request for URL: ${request.url}" }
                }
                .onFailure { throwable ->
                    logger.error(throwable) { "Failed to retry request: ${request.url}" }
                }
        }
    }
}
