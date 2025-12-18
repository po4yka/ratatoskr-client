package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.ProcessingStage
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.RequestStatus

data class SubmitURLState(
    val url: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val isLoading: Boolean = false,
    val error: String? = null,
    val progress: Float = 0f,
    val stage: ProcessingStage = ProcessingStage.UNSPECIFIED,
    val message: String? = null,
    // Request history
    val recentRequests: List<Request> = emptyList(),
    val isLoadingHistory: Boolean = false,
    val showHistory: Boolean = false,
)
