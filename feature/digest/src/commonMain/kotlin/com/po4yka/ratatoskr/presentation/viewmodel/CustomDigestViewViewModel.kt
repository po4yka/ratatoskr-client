package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.domain.model.CustomDigestStatus
import com.po4yka.ratatoskr.domain.repository.CustomDigestRepository
import com.po4yka.ratatoskr.domain.usecase.DeleteCustomDigestUseCase
import com.po4yka.ratatoskr.domain.usecase.GetCustomDigestByIdUseCase
import com.po4yka.ratatoskr.presentation.state.CustomDigestViewState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomDigestViewViewModel(
    private val getCustomDigestByIdUseCase: GetCustomDigestByIdUseCase,
    private val deleteCustomDigestUseCase: DeleteCustomDigestUseCase,
    private val repository: CustomDigestRepository,
) : BaseViewModel() {
    private val _state = MutableStateFlow(CustomDigestViewState())
    val state = _state.asStateFlow()

    private var pollingJob: Job? = null

    @Suppress("TooGenericExceptionCaught")
    fun loadDigest(digestId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val digest = getCustomDigestByIdUseCase(digestId)
            _state.update { it.copy(digest = digest, isLoading = false) }
            if (digest != null &&
                (digest.status == CustomDigestStatus.GENERATING || digest.status == CustomDigestStatus.PENDING)
            ) {
                pollStatus(digestId)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun pollStatus(digestId: String) {
        pollingJob?.cancel()
        pollingJob =
            viewModelScope.launch {
                try {
                    val completed = repository.pollDigestStatus(digestId)
                    _state.update { it.copy(digest = completed) }
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
    }

    @Suppress("TooGenericExceptionCaught")
    fun deleteDigest(
        digestId: String,
        onDeleted: () -> Unit,
    ) {
        viewModelScope.launch {
            try {
                deleteCustomDigestUseCase(digestId)
                onDeleted()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}
