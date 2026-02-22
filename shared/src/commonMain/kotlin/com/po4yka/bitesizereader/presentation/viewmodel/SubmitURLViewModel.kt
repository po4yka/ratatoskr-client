package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.ProcessingService
import com.po4yka.bitesizereader.domain.model.ProcessingStage
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.domain.usecase.CheckDuplicateUrlUseCase
import com.po4yka.bitesizereader.domain.usecase.GetRequestsUseCase
import com.po4yka.bitesizereader.domain.usecase.RetryRequestUseCase
import com.po4yka.bitesizereader.presentation.state.SubmitURLState
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class SubmitURLViewModel(
    private val processingService: ProcessingService,
    private val getRequestsUseCase: GetRequestsUseCase,
    private val retryRequestUseCase: RetryRequestUseCase,
    private val checkDuplicateUrlUseCase: CheckDuplicateUrlUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SubmitURLState())
    val state = _state.asStateFlow()

    init {
        observeRequestHistory()
    }

    private fun observeRequestHistory() {
        getRequestsUseCase()
            .onEach { requests ->
                _state.value =
                    _state.value.copy(
                        recentRequests = requests,
                        isLoadingHistory = false,
                    )
            }
            .catch { e ->
                logger.warn(e) { "Failed to load request history" }
                _state.value = _state.value.copy(isLoadingHistory = false)
            }
            .launchIn(viewModelScope)
    }

    fun toggleHistoryVisibility() {
        _state.value = _state.value.copy(showHistory = !_state.value.showHistory)
    }

    fun retryRequest(request: Request) {
        viewModelScope.launch {
            _state.value = _state.value.copy(url = request.url)
            submitUrl()
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun onUrlChanged(url: String) {
        _state.value =
            _state.value.copy(
                url = url,
                error = null,
                isDuplicate = false,
                duplicateSummaryId = null,
            )
    }

    fun checkDuplicate() {
        val url = _state.value.url
        if (url.isBlank()) {
            _state.value = _state.value.copy(error = "URL cannot be empty")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isCheckingDuplicate = true, error = null)
            try {
                val result = checkDuplicateUrlUseCase(url)
                if (result.isDuplicate) {
                    _state.value =
                        _state.value.copy(
                            isCheckingDuplicate = false,
                            isDuplicate = true,
                            duplicateSummaryId = result.existingSummaryId,
                        )
                } else {
                    _state.value = _state.value.copy(isCheckingDuplicate = false)
                    submitUrl()
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to check duplicate URL, proceeding with submit" }
                _state.value = _state.value.copy(isCheckingDuplicate = false)
                submitUrl()
            }
        }
    }

    fun forceSubmit() {
        _state.value =
            _state.value.copy(
                isDuplicate = false,
                duplicateSummaryId = null,
            )
        submitUrl()
    }

    fun dismissDuplicate() {
        _state.value =
            _state.value.copy(
                isDuplicate = false,
                duplicateSummaryId = null,
            )
    }

    @Suppress("unused") // Public API for UI layer
    fun submitUrl() {
        viewModelScope.launch {
            val url = _state.value.url
            if (url.isBlank()) {
                _state.value = _state.value.copy(error = "URL cannot be empty")
                return@launch
            }

            processingService.submitUrl(url)
                .onStart {
                    _state.value =
                        _state.value.copy(
                            isLoading = true,
                            error = null,
                            status = RequestStatus.PENDING,
                            stage = ProcessingStage.QUEUED,
                            progress = 0f,
                            message = "Starting...",
                        )
                }
                .catch { e ->
                    // Handle error
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            status = RequestStatus.FAILED,
                            error = e.toAppError().userMessage(),
                        )
                }
                .collect { update ->
                    _state.value =
                        _state.value.copy(
                            isLoading = update.status != RequestStatus.COMPLETED &&
                                update.status != RequestStatus.FAILED,
                            status = update.status,
                            stage = update.stage,
                            progress = update.progress,
                            message = update.message,
                        )

                    if (update.status == RequestStatus.FAILED) {
                        _state.value = _state.value.copy(error = update.error)
                    }
                }
        }
    }
}
