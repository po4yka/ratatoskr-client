package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.DeleteSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryByIdUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryContentUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.presentation.state.SummaryDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class SummaryDetailViewModel(
    private val getSummaryByIdUseCase: GetSummaryByIdUseCase,
    private val getSummaryContentUseCase: GetSummaryContentUseCase,
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase,
    private val deleteSummaryUseCase: DeleteSummaryUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SummaryDetailState())
    val state = _state.asStateFlow()

    @Suppress("TooGenericExceptionCaught")
    fun loadSummary(id: String) {
        viewModelScope.launch {
            _state.value = SummaryDetailState(isLoading = true)
            try {
                val summary = getSummaryByIdUseCase(id)
                _state.value = _state.value.copy(summary = summary, isLoading = false)
                if (summary != null && !summary.isRead) {
                    markSummaryAsReadUseCase(id)
                }
                if (summary != null) {
                    fetchFullContent(id)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun fetchFullContent(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingContent = true)
            try {
                val fullContent = getSummaryContentUseCase(id)
                if (fullContent != null) {
                    _state.value =
                        _state.value.copy(
                            summary = _state.value.summary?.copy(content = fullContent),
                            isLoadingContent = false,
                        )
                } else {
                    _state.value = _state.value.copy(isLoadingContent = false)
                }
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(
                        isLoadingContent = false,
                        error = e.message ?: "Failed to load content",
                    )
            }
        }
    }

    @Suppress("unused", "TooGenericExceptionCaught") // Public API for UI layer
    fun deleteSummary(id: String) {
        viewModelScope.launch {
            try {
                deleteSummaryUseCase(id)
                // Navigate back or update state? handled by UI usually
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
