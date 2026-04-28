package com.po4yka.ratatoskr.presentation.state

import com.po4yka.ratatoskr.domain.model.ReadFilter
import com.po4yka.ratatoskr.domain.model.SortOrder
import com.po4yka.ratatoskr.domain.model.Summary
import kotlin.time.Instant

data class SummarySearchState(
    val query: String = "",
    val isActive: Boolean = false,
    val trendingTopics: List<String> = emptyList(),
    val recentSearches: List<String> = emptyList(),
)

data class SummaryFilterState(
    val selectedTag: String? = null,
    val availableTags: List<String> = emptyList(),
    val readFilter: ReadFilter = ReadFilter.ALL,
    val sortOrder: SortOrder = SortOrder.NEWEST,
)

data class SummaryLayoutState(
    val layoutMode: LayoutMode = LayoutMode.LIST,
    val viewDensity: ViewDensity = ViewDensity.COMFORTABLE,
)

data class SummaryListState(
    // Core list data
    val summaries: List<Summary> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    // Sub-states
    val search: SummarySearchState = SummarySearchState(),
    val filter: SummaryFilterState = SummaryFilterState(),
    val layout: SummaryLayoutState = SummaryLayoutState(),
    // Connectivity and freshness
    val lastSyncTime: Instant? = null,
    val isOffline: Boolean = false,
    // Non-null while a sync error is active (cleared on next successful sync)
    val syncError: String? = null,
)
