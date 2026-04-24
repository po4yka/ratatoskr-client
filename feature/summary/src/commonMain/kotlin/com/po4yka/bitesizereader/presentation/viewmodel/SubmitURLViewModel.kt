package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.ProcessingService
import com.po4yka.bitesizereader.domain.model.BatchUrlEntry
import com.po4yka.bitesizereader.domain.model.BatchUrlStatus
import com.po4yka.bitesizereader.domain.model.ProcessingStage
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.domain.usecase.CheckDuplicateUrlUseCase
import com.po4yka.bitesizereader.domain.usecase.GetRequestsUseCase
import com.po4yka.bitesizereader.domain.usecase.RetryRequestUseCase
import com.po4yka.bitesizereader.presentation.state.SubmitURLState
import com.po4yka.bitesizereader.presentation.state.SubmitUrlError
import com.po4yka.bitesizereader.util.error.AppError
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

private val urlRegex = Regex("^https?://[\\w\\-]+(\\.[\\w\\-]+)+(:\\d+)?(/.*)?$")

private fun isValidUrl(url: String): Boolean = urlRegex.matches(url.trim())

private fun String.redactQueryAndFragment(): String {
    val queryStart = indexOf('?').takeIf { it >= 0 } ?: length
    val fragmentStart = indexOf('#').takeIf { it >= 0 } ?: length
    val sensitiveStart = minOf(queryStart, fragmentStart)
    return take(sensitiveStart)
}

class SubmitURLViewModel(
    private val processingService: ProcessingService,
    private val getRequestsUseCase: GetRequestsUseCase,
    private val retryRequestUseCase: RetryRequestUseCase,
    private val checkDuplicateUrlUseCase: CheckDuplicateUrlUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SubmitURLState())
    val state = _state.asStateFlow()

    private var batchJob: Job? = null

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
                submitError = null,
                isDuplicate = false,
                duplicateSummaryId = null,
            )
    }

    fun checkDuplicate() {
        val url = _state.value.url
        if (url.isBlank() || !isValidUrl(url)) {
            _state.value = _state.value.copy(submitError = SubmitUrlError.InvalidUrl)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isCheckingDuplicate = true, error = null, submitError = null)
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

    fun toggleBatchMode() {
        batchJob?.cancel()
        batchJob = null
        _state.update {
            it.copy(
                isBatchMode = !it.isBatchMode,
                batchInput = "",
                batchEntries = emptyList(),
                batchCompletedCount = 0,
                isBatchSubmitting = false,
            )
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun onBatchInputChanged(text: String) {
        _state.update { it.copy(batchInput = text) }
    }

    private fun parseBatchUrls(): List<String> =
        _state.value.batchInput
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .filter { it.startsWith("http://") || it.startsWith("https://") }

    private fun recomputeCompletedCount() {
        _state.update { state ->
            state.copy(batchCompletedCount = state.batchEntries.count { it.status == BatchUrlStatus.COMPLETED })
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun submitBatch() {
        val urls = parseBatchUrls()
        if (urls.isEmpty()) return
        if (_state.value.isBatchSubmitting) return

        val entries = urls.map { BatchUrlEntry(url = it, status = BatchUrlStatus.PENDING) }
        _state.update { it.copy(batchEntries = entries, batchCompletedCount = 0, isBatchSubmitting = true) }

        batchJob =
            viewModelScope.launch {
                urls.forEachIndexed { index, url ->
                    if (_state.value.batchEntries.getOrNull(index)?.status == BatchUrlStatus.SKIPPED) {
                        return@forEachIndexed
                    }

                    // Duplicate check
                    updateEntryStatus(index, BatchUrlStatus.CHECKING)
                    val duplicateSummaryId = checkForDuplicate(url)
                    if (duplicateSummaryId != null) {
                        updateEntry(index) {
                            it.copy(
                                isDuplicate = true,
                                duplicateSummaryId = duplicateSummaryId,
                                status = BatchUrlStatus.SKIPPED,
                            )
                        }
                        return@forEachIndexed
                    }

                    // Submit
                    updateEntryStatus(index, BatchUrlStatus.SUBMITTING)
                    try {
                        var done = false
                        processingService.submitUrl(url).collect { update ->
                            if (done) return@collect
                            updateEntry(index) { entry ->
                                entry.copy(progress = update.progress, stage = update.stage)
                            }
                            when {
                                update.stage == ProcessingStage.DONE -> {
                                    done = true
                                    updateEntryStatus(index, BatchUrlStatus.COMPLETED)
                                    recomputeCompletedCount()
                                }
                                update.status == RequestStatus.FAILED -> {
                                    done = true
                                    updateEntry(index) { entry ->
                                        entry.copy(status = BatchUrlStatus.FAILED, error = update.error ?: "Failed")
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        updateEntry(index) { it.copy(status = BatchUrlStatus.FAILED, error = e.message ?: "Failed") }
                    }
                }
                _state.update { it.copy(isBatchSubmitting = false) }
            }
    }

    private suspend fun checkForDuplicate(url: String): String? =
        try {
            val result = checkDuplicateUrlUseCase(url)
            if (result.isDuplicate) result.existingSummaryId else null
        } catch (e: Exception) {
            logger.warn(e) { "Failed to check duplicate for ${url.redactQueryAndFragment()}, proceeding with submit" }
            null
        }

    private fun updateEntryStatus(
        index: Int,
        status: BatchUrlStatus,
    ) {
        _state.update { state ->
            val entries = state.batchEntries.toMutableList()
            entries.getOrNull(index)?.let { entries[index] = it.copy(status = status) }
            state.copy(batchEntries = entries)
        }
    }

    private fun updateEntry(
        index: Int,
        transform: (BatchUrlEntry) -> BatchUrlEntry,
    ) {
        _state.update { state ->
            val entries = state.batchEntries.toMutableList()
            entries.getOrNull(index)?.let { entries[index] = transform(it) }
            state.copy(batchEntries = entries)
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun skipBatchEntry(index: Int) {
        updateEntryStatus(index, BatchUrlStatus.SKIPPED)
    }

    @Suppress("unused") // Public API for UI layer
    fun retryBatchEntry(index: Int) {
        val entry = _state.value.batchEntries.getOrNull(index) ?: return
        viewModelScope.launch {
            updateEntryStatus(index, BatchUrlStatus.SUBMITTING)
            try {
                var done = false
                processingService.submitUrl(entry.url).collect { update ->
                    if (done) return@collect
                    updateEntry(index) { e -> e.copy(progress = update.progress, stage = update.stage) }
                    when {
                        update.stage == ProcessingStage.DONE -> {
                            done = true
                            updateEntryStatus(index, BatchUrlStatus.COMPLETED)
                            recomputeCompletedCount()
                        }
                        update.status == RequestStatus.FAILED -> {
                            done = true
                            updateEntry(index) { e ->
                                e.copy(status = BatchUrlStatus.FAILED, error = update.error ?: "Failed")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                updateEntry(index) { it.copy(status = BatchUrlStatus.FAILED, error = e.message ?: "Failed") }
            }
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun cancelBatch() {
        batchJob?.cancel()
        batchJob = null
        _state.update { state ->
            val entries =
                state.batchEntries.map { entry ->
                    if (entry.status == BatchUrlStatus.PENDING ||
                        entry.status == BatchUrlStatus.CHECKING ||
                        entry.status == BatchUrlStatus.SUBMITTING
                    ) {
                        entry.copy(status = BatchUrlStatus.SKIPPED)
                    } else {
                        entry
                    }
                }
            state.copy(isBatchSubmitting = false, batchEntries = entries)
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun submitBatchEntryAnyway(index: Int) {
        val currentEntry = _state.value.batchEntries.getOrNull(index) ?: return
        if (currentEntry.status == BatchUrlStatus.SUBMITTING ||
            currentEntry.status == BatchUrlStatus.COMPLETED
        ) {
            return
        }
        viewModelScope.launch {
            updateEntry(index) { it.copy(isDuplicate = false, duplicateSummaryId = null) }
            updateEntryStatus(index, BatchUrlStatus.SUBMITTING)
            try {
                var done = false
                processingService.submitUrl(currentEntry.url).collect { update ->
                    if (done) return@collect
                    updateEntry(index) { e -> e.copy(progress = update.progress, stage = update.stage) }
                    when {
                        update.stage == ProcessingStage.DONE -> {
                            done = true
                            updateEntryStatus(index, BatchUrlStatus.COMPLETED)
                            recomputeCompletedCount()
                        }
                        update.status == RequestStatus.FAILED -> {
                            done = true
                            updateEntry(index) { e ->
                                e.copy(status = BatchUrlStatus.FAILED, error = update.error ?: "Failed")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                updateEntry(index) { it.copy(status = BatchUrlStatus.FAILED, error = e.message ?: "Failed") }
            }
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun submitUrl() {
        viewModelScope.launch {
            val url = _state.value.url
            if (url.isBlank() || !isValidUrl(url)) {
                _state.value = _state.value.copy(submitError = SubmitUrlError.InvalidUrl)
                return@launch
            }

            processingService.submitUrl(url)
                .onStart {
                    _state.value =
                        _state.value.copy(
                            isLoading = true,
                            error = null,
                            submitError = null,
                            status = RequestStatus.PENDING,
                            stage = ProcessingStage.QUEUED,
                            progress = 0f,
                            message = "Starting...",
                        )
                }
                .catch { e ->
                    val appError = e.toAppError()
                    val submitError = appError.toSubmitUrlError()
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            status = RequestStatus.FAILED,
                            error = appError.userMessage(),
                            submitError = submitError,
                        )
                }
                .collect { update ->
                    _state.value =
                        _state.value.copy(
                            isLoading =
                                update.status != RequestStatus.COMPLETED &&
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

    private fun AppError.toSubmitUrlError(): SubmitUrlError =
        when (this) {
            is AppError.NetworkError, is AppError.TimeoutError -> SubmitUrlError.NetworkError
            is AppError.ConflictError -> SubmitUrlError.DuplicateUrl
            is AppError.ServerError -> SubmitUrlError.ServerError
            is AppError.ValidationError -> SubmitUrlError.InvalidUrl
            else -> SubmitUrlError.Unknown(userMessage())
        }
}
