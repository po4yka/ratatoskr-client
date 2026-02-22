package com.po4yka.bitesizereader.domain.model

data class DomainProcessingUpdate(
    val status: RequestStatus,
    val stage: ProcessingStage,
    val progress: Float,
    val message: String,
    val error: String?,
)
