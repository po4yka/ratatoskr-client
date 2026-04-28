package com.po4yka.ratatoskr.domain.model

data class DomainProcessingUpdate(
    val status: RequestStatus,
    val stage: ProcessingStage,
    val progress: Float,
    val message: String,
    val error: String?,
)
