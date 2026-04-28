package com.po4yka.ratatoskr.presentation.state

import com.po4yka.ratatoskr.domain.model.DigestFormat
import com.po4yka.ratatoskr.domain.model.Summary

data class CustomDigestCreateState(
    val summaries: List<Summary> = emptyList(),
    val filteredSummaries: List<Summary> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val title: String = "",
    val format: DigestFormat = DigestFormat.BRIEF,
    val searchQuery: String = "",
    val isLoadingSummaries: Boolean = false,
    val isCreating: Boolean = false,
    val createdDigestId: String? = null,
    val error: String? = null,
)
