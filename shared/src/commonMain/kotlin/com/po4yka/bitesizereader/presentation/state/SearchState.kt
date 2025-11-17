package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Summary

/**
 * UI state for search screen
 */
data class SearchState(
    val query: String = "",
    val results: List<Summary> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null,
    val trendingTopics: List<String> = emptyList(),
)
