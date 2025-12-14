package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.bitesizereader.domain.model.TelegramLinkStatus
import com.po4yka.bitesizereader.domain.usecase.GetTelegramLinkStatusUseCase
import com.po4yka.bitesizereader.domain.usecase.LinkTelegramUseCase
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import com.po4yka.bitesizereader.domain.usecase.UnlinkTelegramUseCase
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

data class SettingsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val linkStatus: TelegramLinkStatus? = null,
    val linkNonce: String? = null, // Nonce for the linking process
    val isDownloading: Boolean = false, // Reused for Import/Sync
    val downloadError: String? = null,
)

private val logger = KotlinLogging.logger {}

@Factory
class SettingsViewModel(
    private val getTelegramLinkStatusUseCase: GetTelegramLinkStatusUseCase,
    private val unlinkTelegramUseCase: UnlinkTelegramUseCase,
    private val linkTelegramUseCase: LinkTelegramUseCase,
    private val syncDataUseCase: SyncDataUseCase,
) : BaseViewModel() {
    private companion object {
        // Removed legacy constants
    }

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private var downloadJob: Job? = null

    init {
        loadLinkStatus()
    }

    fun loadLinkStatus() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching { getTelegramLinkStatusUseCase() }
                .onSuccess { status ->
                    _state.value = _state.value.copy(isLoading = false, linkStatus = status)
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

    @Suppress("unused")
    fun completeTelegramLink(telegramAuth: TelegramLoginRequestDto) {
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
                _state.value =
                    _state.value.copy(
                        isDownloading = true,
                        downloadError = null,
                    )

                runCatching {
                    syncDataUseCase(forceFull = true)
                }.onSuccess {
                    logger.info { "Sync (Import) completed successfully." }
                    _state.value =
                        _state.value.copy(
                            isDownloading = false,
                        )
                    // Note: UI refreshes automatically via StateFlow - no restart needed
                }.onFailure { throwable ->
                    logger.error(throwable) { "Failed to sync/import" }
                    _state.value =
                        _state.value.copy(
                            isDownloading = false,
                            downloadError = throwable.toAppError().userMessage(),
                        )
                }
            }
    }
}
