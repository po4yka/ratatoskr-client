package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.model.DigestFormat
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.usecase.CreateCustomDigestUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummariesUseCase
import com.po4yka.bitesizereader.presentation.state.CustomDigestCreateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class CustomDigestCreateViewModel(
    private val getSummariesUseCase: GetSummariesUseCase,
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
            getSummariesUseCase(page = 1, pageSize = 100).collect { summaries ->
                _state.update { it.copy(summaries = summaries, isLoadingSummaries = false) }
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
    }

    val filteredSummaries: List<Summary>
        get() =
            _state.value.let { s ->
                if (s.searchQuery.isBlank()) {
                    s.summaries
                } else {
                    s.summaries.filter { it.title.contains(s.searchQuery, ignoreCase = true) }
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
