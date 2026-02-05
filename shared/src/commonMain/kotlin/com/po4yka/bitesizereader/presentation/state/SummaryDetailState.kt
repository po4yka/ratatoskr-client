package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Summary

data class SummaryDetailState(
    val summary: Summary? = null,
    val isLoading: Boolean = false,
    val isLoadingContent: Boolean = false,
    val error: String? = null,
)
