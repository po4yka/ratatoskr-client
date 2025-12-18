package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Summary

data class SummaryListState(
    // Data
    val summaries: List<Summary> = emptyList(),
    // Loading states
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    // Error handling
    val error: String? = null,
    // Pagination
    val page: Int = 1,
    val hasMore: Boolean = true,
    // Search
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    // Filtering
    val selectedTag: String? = null,
    val availableTags: List<String> = emptyList(),
    val readFilter: ReadFilter = ReadFilter.ALL,
    // Sorting
    val sortOrder: SortOrder = SortOrder.NEWEST,
    // Layout preferences
    val layoutMode: LayoutMode = LayoutMode.LIST,
    val viewDensity: ViewDensity = ViewDensity.COMFORTABLE,
    // Trending topics (shown when search active but query empty)
    val trendingTopics: List<String> = emptyList(),
    // Recent searches (shown when search active but query empty)
    val recentSearches: List<String> = emptyList(),
)
