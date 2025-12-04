package com.po4yka.bitesizereader.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.po4yka.bitesizereader.domain.usecase.GetSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SummaryListViewModel(
    private val getSummariesUseCase: GetSummariesUseCase,
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SummaryListState())
    val state = _state.asStateFlow()

    init {
        loadSummaries()
    }

    fun loadSummaries() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            getSummariesUseCase(_state.value.page, 20, listOfNotNull(_state.value.selectedTag))
                .catch { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
                .collect { summaries ->
                    _state.value = _state.value.copy(
                        summaries = summaries,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun onTagSelected(tag: String?) {
        _state.value = _state.value.copy(selectedTag = tag, page = 1, summaries = emptyList())
        loadSummaries()
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            markSummaryAsReadUseCase(id)
        }
    }
}