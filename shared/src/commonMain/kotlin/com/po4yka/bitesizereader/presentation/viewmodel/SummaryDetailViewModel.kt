package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.GetSummaryByIdUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.presentation.state.SummaryDetailState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for summary detail screen
 */
class SummaryDetailViewModel(
    private val summaryId: Int,
    private val getSummaryByIdUseCase: GetSummaryByIdUseCase,
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SummaryDetailState())
    val state: StateFlow<SummaryDetailState> = _state.asStateFlow()

    init {
        loadSummary()
        markAsRead()
    }

    fun loadSummary() {
        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = getSummaryByIdUseCase(summaryId)

            result.onSuccess { summary ->
                _state.value =
                    _state.value.copy(
                        summary = summary,
                        isLoading = false,
                        error = null,
                    )
            }.onFailure { error ->
                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        error = error.message,
                    )
            }
        }
    }

    private fun markAsRead() {
        viewModelScope.launch {
            markSummaryAsReadUseCase(summaryId, true)
        }
    }

    fun toggleReadStatus() {
        viewModelScope.launch {
            _state.value.summary?.let { summary ->
                markSummaryAsReadUseCase(summaryId, !summary.isRead)
                loadSummary() // Reload to get updated state
            }
        }
    }

    fun retry() {
        loadSummary()
    }
}
