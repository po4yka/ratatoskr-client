package com.po4yka.ratatoskr.domain.model

data class QuickSaveResult(
    val requestId: Long? = null,
    val status: String,
    val title: String? = null,
    val url: String,
    val isDuplicate: Boolean = false,
    val summaryId: Long? = null,
    val attachedTags: List<String> = emptyList(),
)
