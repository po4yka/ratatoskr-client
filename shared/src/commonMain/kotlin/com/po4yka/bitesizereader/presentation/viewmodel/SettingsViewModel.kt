package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.bitesizereader.domain.model.TelegramLinkStatus
import com.po4yka.bitesizereader.domain.usecase.DownloadDatabaseUseCase
import com.po4yka.bitesizereader.domain.usecase.DownloadMode
import com.po4yka.bitesizereader.domain.usecase.GetTelegramLinkStatusUseCase
import com.po4yka.bitesizereader.domain.usecase.LinkTelegramUseCase
import com.po4yka.bitesizereader.domain.usecase.UnlinkTelegramUseCase
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant
import org.koin.core.annotation.Factory

data class SettingsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val linkStatus: TelegramLinkStatus? = null,
    val linkNonce: String? = null, // Nonce for the linking process
    val isDownloading: Boolean = false,
    val downloadProgress: Long = 0,
    val downloadTotal: Long = 0,
    val downloadError: String? = null,
    val downloadAttempts: List<DownloadAttemptSnapshot> = emptyList(),
)

enum class DownloadStatus {
    InProgress,
    Success,
    Failed,
    Cancelled,
}

data class DownloadAttemptSnapshot(
    val mode: DownloadMode,
    val startedAt: Instant,
    val finishedAt: Instant? = null,
    val status: DownloadStatus = DownloadStatus.InProgress,
    val bytesDownloaded: Long = 0,
    val totalBytes: Long = 0,
    val error: String? = null,
)

private val logger = KotlinLogging.logger {}

@Factory
class SettingsViewModel(
    private val getTelegramLinkStatusUseCase: GetTelegramLinkStatusUseCase,
    private val unlinkTelegramUseCase: UnlinkTelegramUseCase,
    private val linkTelegramUseCase: LinkTelegramUseCase,
    private val downloadDatabaseUseCase: DownloadDatabaseUseCase,
    private val platform: com.po4yka.bitesizereader.Platform,
) : BaseViewModel() {
    private companion object {
        const val MAX_ATTEMPT_HISTORY = 10
        const val BACKUP_FILE_NAME = "bite_size_reader_backup.sqlite"
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

    fun downloadDatabase(mode: DownloadMode) {
        if (_state.value.isDownloading) {
            logger.warn { "downloadDatabase called but download is already in progress" }
            return
        }

        logger.info { "Starting database download. Mode: $mode" }
        val fileName = BACKUP_FILE_NAME
        val startedAt = Clock.System.now()

        downloadJob?.cancel()
        downloadJob =
            viewModelScope.launch {
                _state.value =
                    _state.value.copy(
                        isDownloading = true,
                        downloadProgress = 0,
                        downloadTotal = 0,
                        downloadError = null,
                        downloadAttempts = trackNewAttempt(mode, startedAt),
                    )
                runCatching { downloadDatabaseUseCase(fileName, mode) }
                    .onSuccess { flow ->
                        flow
                            .catch { throwable ->
                                _state.value =
                                    _state.value.copy(
                                        downloadAttempts =
                                            finalizeAttempt(
                                                status = DownloadStatus.Failed,
                                                error = throwable.toAppError().userMessage(),
                                            ),
                                        isDownloading = false,
                                        downloadError = throwable.toAppError().userMessage(),
                                    )
                                _state.value =
                                    _state.value.copy(
                                        isDownloading = false,
                                        downloadError = throwable.toAppError().userMessage(),
                                    )
                                logger.error(throwable) { "Error during download flow collection" }
                            }
                            .collect { progress ->
                                _state.value =
                                    _state.value.copy(
                                        downloadAttempts =
                                            updateLastAttempt(
                                                bytesDownloaded = progress.bytesDownloaded,
                                                totalBytes = progress.totalBytes,
                                            ),
                                    )
                                _state.value =
                                    _state.value.copy(
                                        downloadProgress = progress.bytesDownloaded,
                                        downloadTotal = progress.totalBytes,
                                    )
                                if (progress.isComplete) {
                                    _state.value = _state.value.copy(isDownloading = false)
                                    _state.value =
                                        _state.value.copy(
                                            downloadAttempts =
                                                finalizeAttempt(status = DownloadStatus.Success),
                                        )
                                    // If import, maybe show specific success message?
                                    // For now generic success is implied by end of loading.
                                    // Consider adding a "toast" or "message" to state.
                                    logger.info { "Download/Import completed successfully." }
                                    if (mode == DownloadMode.IMPORT) {
                                        logger.info { "Restarting app to apply database changes" }
                                        platform.restartApp()
                                    }
                                }
                            }
                    }
                    .onFailure { throwable ->
                        logger.error(throwable) { "Failed to start download/import process" }
                        _state.value =
                            _state.value.copy(
                                isDownloading = false,
                                downloadError = throwable.toAppError().userMessage(),
                                downloadAttempts =
                                    finalizeAttempt(
                                        status = DownloadStatus.Failed,
                                        error = throwable.toAppError().userMessage(),
                                    ),
                            )
                    }
            }
    }

    fun clearDownloadArtifacts() {
        logger.info { "Clearing temp download artifacts" }
        downloadDatabaseUseCase.cleanupTemp(BACKUP_FILE_NAME)
        _state.value =
            _state.value.copy(
                downloadProgress = 0,
                downloadTotal = 0,
                downloadError = null,
            )
    }

    fun cancelDownload() {
        logger.info { "Cancelling download/import" }
        downloadJob?.cancel()
        _state.value =
            _state.value.copy(
                isDownloading = false,
                downloadAttempts = finalizeAttempt(status = DownloadStatus.Cancelled),
            )
    }

    private fun trackNewAttempt(mode: DownloadMode, startedAt: Instant): List<DownloadAttemptSnapshot> {
        val updated =
            _state.value.downloadAttempts + DownloadAttemptSnapshot(
                mode = mode,
                startedAt = startedAt,
                status = DownloadStatus.InProgress,
            )
        return updated.takeLast(MAX_ATTEMPT_HISTORY)
    }

    private fun updateLastAttempt(
        bytesDownloaded: Long? = null,
        totalBytes: Long? = null,
    ): List<DownloadAttemptSnapshot> {
        if (_state.value.downloadAttempts.isEmpty()) return _state.value.downloadAttempts
        val updated = _state.value.downloadAttempts.toMutableList()
        val last = updated.last()
        updated[updated.lastIndex] =
            last.copy(
                bytesDownloaded = bytesDownloaded ?: last.bytesDownloaded,
                totalBytes = totalBytes ?: last.totalBytes,
            )
        return updated
    }

    private fun finalizeAttempt(
        status: DownloadStatus,
        error: String? = null,
    ): List<DownloadAttemptSnapshot> {
        if (_state.value.downloadAttempts.isEmpty()) return _state.value.downloadAttempts
        val updated = _state.value.downloadAttempts.toMutableList()
        val last = updated.last()
        updated[updated.lastIndex] =
            last.copy(
                status = status,
                finishedAt = Clock.System.now(),
                error = error,
            )
        return updated
    }
}
