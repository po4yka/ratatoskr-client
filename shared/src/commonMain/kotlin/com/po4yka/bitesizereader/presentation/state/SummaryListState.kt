package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Summary

data class SummaryListState(
    val summaries: List<Summary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val selectedTag: String? = null
)