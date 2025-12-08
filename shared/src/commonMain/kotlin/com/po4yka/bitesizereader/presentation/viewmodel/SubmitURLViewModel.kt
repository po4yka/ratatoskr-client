package com.po4yka.bitesizereader.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.po4yka.bitesizereader.domain.usecase.GetRequestStatusUseCase
import com.po4yka.bitesizereader.domain.usecase.SubmitURLUseCase
import com.po4yka.bitesizereader.presentation.state.SubmitURLState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage

class SubmitURLViewModel(
    private val submitURLUseCase: SubmitURLUseCase,
    private val getRequestStatusUseCase: GetRequestStatusUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SubmitURLState())
    val state = _state.asStateFlow()

    fun onUrlChanged(url: String) {
        _state.value = _state.value.copy(url = url, error = null)
    }

    fun submitUrl() {
        viewModelScope.launch {
            val url = _state.value.url
            if (url.isBlank()) {
                _state.value = _state.value.copy(error = "URL cannot be empty")
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val request = submitURLUseCase(url)
                _state.value = _state.value.copy(isLoading = false, status = request.status)
                // Start polling
                pollStatus(request.id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.toAppError().userMessage())
            }
        }
    }

    private fun pollStatus(requestId: String) {
        viewModelScope.launch {
            while (true) {
                delay(2000) // Poll every 2 seconds
                try {
                    val request = getRequestStatusUseCase(requestId)
                    _state.value = _state.value.copy(status = request.status)
                    // Break if completed or failed
                    // Using simplistic check here, should rely on enum
                } catch (e: Exception) {
                    // Ignore polling errors or retry
                }
            }
        }
    }
}
