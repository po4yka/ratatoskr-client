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
    private val viewModelScope: CoroutineScope
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
                filters = SearchFilters()
            ).catch { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = error.message
                )
            }.collect { summaries ->
                val updatedList = if (refresh || currentOffset == 0) {
                    summaries
                } else {
                    _state.value.summaries + summaries
                }

                _state.value = _state.value.copy(
                    summaries = updatedList,
                    isLoading = false,
                    isRefreshing = false,
                    error = null,
                    hasMore = summaries.size >= pageSize
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

    fun markAsRead(id: Int, isRead: Boolean) {
        viewModelScope.launch {
            markSummaryAsReadUseCase(id, isRead)

            // Update local state optimistically
            val updatedSummaries = _state.value.summaries.map { summary ->
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
}
