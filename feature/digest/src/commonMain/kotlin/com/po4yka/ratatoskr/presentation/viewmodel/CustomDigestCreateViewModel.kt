package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.domain.model.DigestFormat
import com.po4yka.ratatoskr.feature.summary.api.SummaryFeedPort
import com.po4yka.ratatoskr.domain.usecase.CreateCustomDigestUseCase
import com.po4yka.ratatoskr.presentation.state.CustomDigestCreateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomDigestCreateViewModel(
    private val summaryFeedPort: SummaryFeedPort,
    private val createCustomDigestUseCase: CreateCustomDigestUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(CustomDigestCreateState())
    val state = _state.asStateFlow()

    init {
        loadSummaries()
    }

    fun loadSummaries() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingSummaries = true) }
            summaryFeedPort.getSummaries(page = 1, pageSize = 100).collect { summaries ->
                _state.update { it.copy(summaries = summaries, isLoadingSummaries = false) }
                updateFiltered()
            }
        }
    }

    fun toggleSelection(id: String) {
        _state.update { state ->
            val newSelected =
                if (id in state.selectedIds) state.selectedIds - id else state.selectedIds + id
            state.copy(selectedIds = newSelected)
        }
    }

    fun setTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun setFormat(format: DigestFormat) {
        _state.update { it.copy(format = format) }
    }

    fun onSearchChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        updateFiltered()
    }

    private fun updateFiltered() {
        _state.update { state ->
            val filtered =
                if (state.searchQuery.isBlank()) {
                    state.summaries
                } else {
                    state.summaries.filter { it.title.contains(state.searchQuery, ignoreCase = true) }
                }
            state.copy(filteredSummaries = filtered)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun createDigest() {
        val state = _state.value
        if (state.selectedIds.isEmpty() || state.isCreating) return
        viewModelScope.launch {
            _state.update { it.copy(isCreating = true, error = null) }
            try {
                val title = state.title.ifBlank { "Custom Digest" }
                val digest = createCustomDigestUseCase(title, state.selectedIds.toList(), state.format)
                _state.update { it.copy(isCreating = false, createdDigestId = digest.id) }
            } catch (e: Exception) {
                _state.update { it.copy(isCreating = false, error = e.message ?: "Failed to create digest") }
            }
        }
    }
}
