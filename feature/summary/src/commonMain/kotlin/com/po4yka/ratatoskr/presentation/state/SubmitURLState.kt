package com.po4yka.ratatoskr.presentation.state

import com.po4yka.ratatoskr.domain.model.BatchUrlEntry
import com.po4yka.ratatoskr.domain.model.ProcessingStage
import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.domain.model.RequestStatus

data class SubmitURLState(
    val url: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val isLoading: Boolean = false,
    val error: String? = null,
    val submitError: SubmitUrlError? = null,
    val progress: Float = 0f,
    val stage: ProcessingStage = ProcessingStage.UNSPECIFIED,
    val message: String? = null,
    // Request history
    val recentRequests: List<Request> = emptyList(),
    val isLoadingHistory: Boolean = false,
    val showHistory: Boolean = false,
    // Duplicate URL checking
    val isDuplicate: Boolean = false,
    val duplicateSummaryId: String? = null,
    val isCheckingDuplicate: Boolean = false,
    // Batch submission
    val isBatchMode: Boolean = false,
    val batchInput: String = "",
    val batchEntries: List<BatchUrlEntry> = emptyList(),
    val batchCompletedCount: Int = 0,
    val isBatchSubmitting: Boolean = false,
)
