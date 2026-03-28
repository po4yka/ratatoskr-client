package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.ReadFilter
import com.po4yka.bitesizereader.domain.model.Summary

/**
 * State for the Search screen.
 */
data class SearchState(
    // Query and results
    val query: String = "",
    val results: List<Summary> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val trendingTopics: List<String> = emptyList(),
    // Loading states
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    // Pagination
    val currentPage: Int = 1,
    val hasMoreResults: Boolean = false,
    // Search mode
    val searchMode: SearchMode = SearchMode.FULLTEXT,
    // Filters
    val filters: SearchFilters = SearchFilters(),
    val showFilters: Boolean = false,
    // Insights
    val insights: List<Summary> = emptyList(),
    val isLoadingInsights: Boolean = false,
)

/**
 * Search mode - fulltext (keyword) or semantic (AI-powered).
 */
enum class SearchMode {
    FULLTEXT,
    SEMANTIC,
}

/**
 * Search filter options.
 * Uses the existing ReadFilter enum from domain/model/SummaryFilterEnums.kt.
 */
data class SearchFilters(
    val tags: List<String> = emptyList(),
    val readFilter: ReadFilter = ReadFilter.ALL,
    val dateRange: DateRange? = null,
    val language: String? = null,
)

/**
 * Date range filter for search results.
 */
data class DateRange(
    val startDate: Long? = null,
    val endDate: Long? = null,
)
