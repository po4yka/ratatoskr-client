package com.po4yka.ratatoskr.domain.model

data class DuplicateCheckResult(
    val isDuplicate: Boolean,
    val existingSummaryId: String?,
)
