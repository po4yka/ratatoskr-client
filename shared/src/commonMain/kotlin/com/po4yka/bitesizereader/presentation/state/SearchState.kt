package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Summary

data class SearchState(
    val query: String = "",
    val results: List<Summary> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val trendingTopics: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)