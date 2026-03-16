package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import com.po4yka.bitesizereader.domain.usecase.GetRequestsUseCase
import com.po4yka.bitesizereader.domain.usecase.RetryRequestUseCase
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import com.po4yka.bitesizereader.presentation.state.SyncSettingsState
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class SyncSettingsDelegate(
    private val syncDataUseCase: SyncDataUseCase,
    private val getRequestsUseCase: GetRequestsUseCase,
    private val retryRequestUseCase: RetryRequestUseCase,
    private val summaryRepository: SummaryRepository,
) {
    private var downloadJob: Job? = null

    fun observeSyncProgress(
        scope: CoroutineScope,
        currentState: () -> SyncSettingsState,
        onState: (SyncSettingsState) -> Unit,
    ) {
        syncDataUseCase.syncProgress
            .onEach { progress ->
                onState(
                    currentState().copy(
                        syncProgress = progress,
                        isDownloading = progress?.isInProgress == true,
                    ),
                )
            }
            .launchIn(scope)
    }

    fun importFromBackend(
        scope: CoroutineScope,
        currentState: () -> SyncSettingsState,
        onState: (SyncSettingsState) -> Unit,
    ) {
        if (currentState().isDownloading) {
            logger.warn { "importFromBackend called but sync is already in progress" }
            return
        }

        logger.info { "Starting database synchronization (IMPORT mode). Force full sync." }
        downloadJob?.cancel()
        downloadJob =
            scope.launch {
                onState(currentState().copy(downloadError = null))

                runCatching {
                    syncDataUseCase(forceFull = true)
                }.onSuccess {
                    logger.info { "Sync (Import) completed successfully." }
                }.onFailure { throwable ->
                    logger.error(throwable) { "Failed to sync/import" }
                    onState(
                        currentState().copy(
                            downloadError = throwable.toAppError().userMessage(),
                        ),
                    )
                }
            }
    }

    fun cancelSync(
        currentState: () -> SyncSettingsState,
    ) {
        if (currentState().isDownloading) {
            logger.info { "Cancelling sync operation" }
            downloadJob?.cancel()
            syncDataUseCase.cancelSync()
        }
    }

    fun loadRequests(
        scope: CoroutineScope,
        currentState: () -> SyncSettingsState,
        onState: (SyncSettingsState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isLoadingRequests = true))
            getRequestsUseCase()
                .catch { throwable ->
                    logger.warn(throwable) { "Failed to load requests" }
                    onState(currentState().copy(isLoadingRequests = false))
                }
                .collect { requests ->
                    onState(
                        currentState().copy(
                            isLoadingRequests = false,
                            requests = requests,
                        ),
                    )
                }
        }
    }

    fun toggleRequestsExpanded(
        currentState: () -> SyncSettingsState,
        onState: (SyncSettingsState) -> Unit,
        scope: CoroutineScope,
    ) {
        val state = currentState()
        val currentlyExpanded = state.requestsExpanded
        onState(state.copy(requestsExpanded = !currentlyExpanded))
        if (!currentlyExpanded && state.requests.isEmpty()) {
            loadRequests(scope, currentState, onState)
        }
    }

    fun retryRequest(
        request: Request,
        scope: CoroutineScope,
    ) {
        scope.launch {
            runCatching { retryRequestUseCase(request) }
                .onSuccess {
                    logger.info { "Retried request for URL: ${request.url}" }
                }
                .onFailure { throwable ->
                    logger.error(throwable) { "Failed to retry request: ${request.url}" }
                }
        }
    }

    fun loadCacheSize(
        scope: CoroutineScope,
        currentState: () -> SyncSettingsState,
        onState: (SyncSettingsState) -> Unit,
    ) {
        scope.launch {
            runCatching { summaryRepository.getCacheSize() }
                .onSuccess { size ->
                    onState(currentState().copy(cacheSize = size))
                }
                .onFailure { throwable ->
                    logger.warn(throwable) { "Failed to load cache size" }
                }
        }
    }

    fun clearContentCache(
        scope: CoroutineScope,
        currentState: () -> SyncSettingsState,
        onState: (SyncSettingsState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isClearingCache = true))
            runCatching { summaryRepository.clearContentCache() }
                .onSuccess {
                    onState(currentState().copy(isClearingCache = false, cacheSize = 0L))
                    logger.info { "Content cache cleared" }
                }
                .onFailure { throwable ->
                    logger.warn(throwable) { "Failed to clear content cache" }
                    onState(currentState().copy(isClearingCache = false))
                }
        }
    }
}
