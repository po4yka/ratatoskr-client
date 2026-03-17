package com.po4yka.bitesizereader.domain.model

data class BatchUrlEntry(
    val url: String,
    val status: BatchUrlStatus,
    val progress: Float = 0f,
    val stage: ProcessingStage = ProcessingStage.UNSPECIFIED,
    val error: String? = null,
    val isDuplicate: Boolean = false,
    val duplicateSummaryId: String? = null,
)

enum class BatchUrlStatus { PENDING, CHECKING, SUBMITTING, COMPLETED, FAILED, SKIPPED }
