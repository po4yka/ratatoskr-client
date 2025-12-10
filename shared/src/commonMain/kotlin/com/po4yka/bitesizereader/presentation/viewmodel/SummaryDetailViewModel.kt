package com.po4yka.bitesizereader.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.po4yka.bitesizereader.domain.usecase.DeleteSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryByIdUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.presentation.state.SummaryDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class SummaryDetailViewModel(
    private val getSummaryByIdUseCase: GetSummaryByIdUseCase,
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase,
    private val deleteSummaryUseCase: DeleteSummaryUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SummaryDetailState())
    val state = _state.asStateFlow()

    fun loadSummary(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val summary = getSummaryByIdUseCase(id)
                _state.value = _state.value.copy(summary = summary, isLoading = false)
                if (summary != null && !summary.isRead) {
                    markSummaryAsReadUseCase(id)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

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
