package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.bitesizereader.domain.model.TelegramLinkStatus
import com.po4yka.bitesizereader.domain.usecase.DownloadDatabaseUseCase
import com.po4yka.bitesizereader.domain.usecase.GetTelegramLinkStatusUseCase
import com.po4yka.bitesizereader.domain.usecase.LinkTelegramUseCase
import com.po4yka.bitesizereader.domain.usecase.UnlinkTelegramUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val linkStatus: TelegramLinkStatus? = null,
    val linkNonce: String? = null, // Nonce for the linking process
    val isDownloading: Boolean = false,
    val downloadProgress: Long = 0,
    val downloadTotal: Long = 0,
    val downloadError: String? = null
)

class SettingsViewModel(
    private val getTelegramLinkStatusUseCase: GetTelegramLinkStatusUseCase,
    private val unlinkTelegramUseCase: UnlinkTelegramUseCase,
    private val linkTelegramUseCase: LinkTelegramUseCase,
    private val downloadDatabaseUseCase: DownloadDatabaseUseCase
) : BaseViewModel() {

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
                    _state.value = _state.value.copy(
                        isLoading = false,
                        linkStatus = status,
                        linkNonce = null // Reset nonce after success
                    )
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(isLoading = false, error = throwable.message)
                }
        }
    }

    fun downloadDatabase(outputFile: String) {
        if (_state.value.isDownloading) return

        downloadJob?.cancel()
        downloadJob = viewModelScope.launch {
            _state.value = _state.value.copy(
                isDownloading = true,
                downloadProgress = 0,
                downloadTotal = 0,
                downloadError = null
            )
            runCatching { downloadDatabaseUseCase(outputFile) }
                .onSuccess { flow ->
                    flow.collect { progress ->
                        _state.value = _state.value.copy(
                            downloadProgress = progress.bytesDownloaded,
                            downloadTotal = progress.totalBytes
                        )
                        if (progress.isComplete) {
                            _state.value = _state.value.copy(isDownloading = false)
                        }
                    }
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        isDownloading = false,
                        downloadError = throwable.message
                    )
                }
        }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        _state.value = _state.value.copy(isDownloading = false)
    }
}
