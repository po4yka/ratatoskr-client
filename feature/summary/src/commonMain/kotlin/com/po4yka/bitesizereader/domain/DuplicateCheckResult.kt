package com.po4yka.bitesizereader.domain.model

data class DuplicateCheckResult(
    val isDuplicate: Boolean,
    val existingSummaryId: String?,
)
