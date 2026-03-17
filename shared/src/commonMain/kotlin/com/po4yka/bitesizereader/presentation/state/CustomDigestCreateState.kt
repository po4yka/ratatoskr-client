package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.DigestFormat
import com.po4yka.bitesizereader.domain.model.Summary

data class CustomDigestCreateState(
    val summaries: List<Summary> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val title: String = "",
    val format: DigestFormat = DigestFormat.BRIEF,
    val searchQuery: String = "",
    val isLoadingSummaries: Boolean = false,
    val isCreating: Boolean = false,
    val createdDigestId: String? = null,
    val error: String? = null,
)
