package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.ProcessingService
import com.po4yka.bitesizereader.domain.model.ProcessingStage
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.grpc.processing.ProcessingStatus
import com.po4yka.bitesizereader.grpc.processing.ProcessingStage as GrpcProcessingStage
import com.po4yka.bitesizereader.presentation.state.SubmitURLState
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class SubmitURLViewModel(
    private val processingService: ProcessingService,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SubmitURLState())
    val state = _state.asStateFlow()

    @Suppress("unused") // Public API for UI layer
    fun onUrlChanged(url: String) {
        _state.value = _state.value.copy(url = url, error = null)
    }

    @Suppress("unused") // Public API for UI layer
    fun submitUrl() {
        viewModelScope.launch {
            val url = _state.value.url
            if (url.isBlank()) {
                _state.value = _state.value.copy(error = "URL cannot be empty")
                return@launch
            }

            processingService.submitUrl(url)
                .onStart {
                    _state.value =
                        _state.value.copy(
                            isLoading = true,
                            error = null,
                            status = RequestStatus.PENDING,
                            stage = ProcessingStage.QUEUED,
                            progress = 0f,
                            message = "Starting...",
                        )
                }
                .catch { e ->
                    // Handle error
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            status = RequestStatus.FAILED,
                            error = e.toAppError().userMessage(),
                        )
                }
                .collect { update ->
                    val appStatus = mapStatus(update.status)
                    val appStage = mapStage(update.stage)

                    _state.value =
                        _state.value.copy(
                            isLoading = appStatus != RequestStatus.COMPLETED && appStatus != RequestStatus.FAILED,
                            status = appStatus,
                            stage = appStage,
                            progress = update.progress,
                            message = update.message,
                        )

                    if (appStatus == RequestStatus.FAILED) {
                        _state.value = _state.value.copy(error = update.error)
                    }
                }
        }
    }

    private fun mapStatus(grpcStatus: ProcessingStatus): RequestStatus {
        return when (grpcStatus) {
            ProcessingStatus.PROCESSING_STATUS_PENDING -> RequestStatus.PENDING
            ProcessingStatus.PROCESSING_STATUS_PROCESSING -> RequestStatus.PROCESSING
            ProcessingStatus.PROCESSING_STATUS_COMPLETED -> RequestStatus.COMPLETED
            ProcessingStatus.PROCESSING_STATUS_FAILED -> RequestStatus.FAILED
            else -> RequestStatus.PENDING
        }
    }

    private fun mapStage(grpcStage: GrpcProcessingStage): ProcessingStage {
        return when (grpcStage) {
            GrpcProcessingStage.PROCESSING_STAGE_QUEUED -> ProcessingStage.QUEUED
            GrpcProcessingStage.PROCESSING_STAGE_EXTRACTION -> ProcessingStage.EXTRACTION
            GrpcProcessingStage.PROCESSING_STAGE_SUMMARIZATION -> ProcessingStage.SUMMARIZATION
            GrpcProcessingStage.PROCESSING_STAGE_SAVING -> ProcessingStage.SAVING
            GrpcProcessingStage.PROCESSING_STAGE_DONE -> ProcessingStage.DONE
            else -> ProcessingStage.UNSPECIFIED
        }
    }
}
