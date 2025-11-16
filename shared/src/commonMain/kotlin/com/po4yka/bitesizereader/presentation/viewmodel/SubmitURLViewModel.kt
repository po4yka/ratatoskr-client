package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.domain.repository.RequestRepository
import com.po4yka.bitesizereader.domain.usecase.SubmitURLUseCase
import com.po4yka.bitesizereader.presentation.state.SubmitURLState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for submit URL screen
 */
class SubmitURLViewModel(
    private val submitURLUseCase: SubmitURLUseCase,
    private val requestRepository: RequestRepository,
    private val viewModelScope: CoroutineScope
) {
    private val _state = MutableStateFlow(SubmitURLState())
    val state: StateFlow<SubmitURLState> = _state.asStateFlow()

    fun setURL(url: String) {
        _state.value = _state.value.copy(url = url, validationError = null)
    }

    fun onUrlChange(url: String) = setURL(url)

    fun submitUrl() = submitURL()

    fun submitURL() {
        val url = _state.value.url

        if (url.isBlank()) {
            _state.value = _state.value.copy(validationError = "Please enter a URL")
            return
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            _state.value = _state.value.copy(validationError = "Please enter a valid URL (must start with http:// or https://)")
            return
        }

        _state.value = _state.value.copy(isSubmitting = true, validationError = null, error = null)

        viewModelScope.launch {
            val result = submitURLUseCase(url)

            result.onSuccess { request ->
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    request = request,
                    isPolling = true,
                    error = null
                )

                // Start polling for status
                pollRequestStatus(request.id)
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    error = error.message
                )
            }
        }
    }

    private fun pollRequestStatus(requestId: Int) {
        viewModelScope.launch {
            requestRepository.pollRequestStatus(requestId)
                .catch { error ->
                    _state.value = _state.value.copy(
                        isPolling = false,
                        error = error.message
                    )
                }
                .collect { request ->
                    _state.value = _state.value.copy(request = request)

                    // Stop polling when completed
                    if (request.status in listOf(
                            RequestStatus.COMPLETED,
                            RequestStatus.ERROR,
                            RequestStatus.CANCELLED
                        )
                    ) {
                        _state.value = _state.value.copy(isPolling = false)
                    }
                }
        }
    }

    fun retry() {
        _state.value.request?.let { request ->
            viewModelScope.launch {
                val result = requestRepository.retryRequest(request.id)

                result.onSuccess { retryRequest ->
                    _state.value = _state.value.copy(
                        request = retryRequest,
                        isPolling = true,
                        error = null
                    )
                    pollRequestStatus(retryRequest.id)
                }.onFailure { error ->
                    _state.value = _state.value.copy(error = error.message)
                }
            }
        }
    }

    fun reset() {
        _state.value = SubmitURLState()
    }

    fun cancelRequest() {
        _state.value.request?.let { request ->
            viewModelScope.launch {
                val result = requestRepository.cancelRequest(request.id)

                result.onSuccess {
                    _state.value = _state.value.copy(
                        isPolling = false,
                        request = request.copy(status = RequestStatus.CANCELLED)
                    )
                }.onFailure { error ->
                    _state.value = _state.value.copy(error = error.message)
                }
            }
        }
    }
}
