package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.model.SearchFilters
import com.po4yka.bitesizereader.domain.usecase.GetSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for summary list screen
 */
class SummaryListViewModel(
    private val getSummariesUseCase: GetSummariesUseCase,
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase,
    private val syncDataUseCase: SyncDataUseCase,
    private val viewModelScope: CoroutineScope,
) {
    private val _state = MutableStateFlow(SummaryListState())
    val state: StateFlow<SummaryListState> = _state.asStateFlow()

    private var currentOffset = 0
    private val pageSize = 20

    init {
        loadSummaries()
    }

    fun loadSummaries(refresh: Boolean = false) {
        if (refresh) {
            currentOffset = 0
            _state.value = _state.value.copy(isRefreshing = true, summaries = emptyList())
        } else {
            _state.value = _state.value.copy(isLoading = true)
        }

        viewModelScope.launch {
            getSummariesUseCase(
                limit = pageSize,
                offset = currentOffset,
                filters = SearchFilters(),
            ).catch { error ->
                val appError = (error as? Exception)?.let {
                    com.po4yka.bitesizereader.util.error.toAppError(it)
                } ?: com.po4yka.bitesizereader.util.error.AppError.UnknownError(
                    message = error.message ?: "Unknown error"
                )

                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = error.message,
                        appError = appError,
                        canRetry = com.po4yka.bitesizereader.util.error.isRetryable(appError),
                    )
            }.collect { summaries ->
                val updatedList =
                    if (refresh || currentOffset == 0) {
                        summaries
                    } else {
                        _state.value.summaries + summaries
                    }

                _state.value =
                    _state.value.copy(
                        summaries = updatedList,
                        isLoading = false,
                        isRefreshing = false,
                        error = null,
                        appError = null,
                        canRetry = false,
                        retryAttempt = 0,
                        hasMore = summaries.size >= pageSize,
                    )

                if (!refresh) {
                    currentOffset += summaries.size
                }
            }
        }
    }

    fun loadMore() {
        if (!_state.value.isLoading && _state.value.hasMore) {
            loadSummaries()
        }
    }

    fun refresh() {
        loadSummaries(refresh = true)
    }

    fun markAsRead(
        id: Int,
        isRead: Boolean,
    ) {
        viewModelScope.launch {
            markSummaryAsReadUseCase(id, isRead)

            // Update local state optimistically
            val updatedSummaries =
                _state.value.summaries.map { summary ->
                    if (summary.id == id) {
                        summary.copy(isRead = isRead)
                    } else {
                        summary
                    }
                }

            _state.value = _state.value.copy(summaries = updatedSummaries)
        }
    }

    fun sync(forceFullSync: Boolean = false) {
        viewModelScope.launch {
            syncDataUseCase(forceFullSync).collect { syncState ->
                _state.value = _state.value.copy(syncState = syncState)

                // Reload summaries after successful sync
                if (syncState is com.po4yka.bitesizereader.domain.model.SyncState.Success) {
                    loadSummaries(refresh = true)
                }
            }
        }
    }

    fun toggleTagFilter(tag: String) {
        val currentTags = _state.value.filters.topicTags.toMutableList()
        if (currentTags.contains(tag)) {
            currentTags.remove(tag)
        } else {
            currentTags.add(tag)
        }

        _state.value =
            _state.value.copy(
                filters = _state.value.filters.copy(topicTags = currentTags),
            )
        loadSummaries(refresh = true)
    }

    fun setReadFilter(readStatus: String?) {
        _state.value =
            _state.value.copy(
                filters = _state.value.filters.copy(readStatus = readStatus),
            )
        loadSummaries(refresh = true)
    }

    fun clearFilters() {
        _state.value =
            _state.value.copy(
                filters = SearchFilters(),
            )
        loadSummaries(refresh = true)
    }

    /**
     * Retry the last failed operation
     */
    fun retry() {
        if (_state.value.canRetry) {
            _state.value = _state.value.copy(
                retryAttempt = _state.value.retryAttempt + 1
            )
            loadSummaries(refresh = true)
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _state.value = _state.value.copy(
            error = null,
            appError = null,
            canRetry = false,
            retryAttempt = 0
        )
    }
}
