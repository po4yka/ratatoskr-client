package com.po4yka.bitesizereader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.domain.model.ReadFilter
import com.po4yka.bitesizereader.domain.model.SortOrder
import com.po4yka.bitesizereader.presentation.navigation.SummaryListComponent
import com.po4yka.bitesizereader.presentation.state.LayoutMode
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import com.po4yka.bitesizereader.ui.components.ContextualEmptyState
import com.po4yka.bitesizereader.ui.components.EmptyStateType
import com.po4yka.bitesizereader.ui.components.FilterChipsRow
import com.po4yka.bitesizereader.ui.components.PullToRefreshContainer
import com.po4yka.bitesizereader.ui.components.RecentSearchesSection
import com.po4yka.bitesizereader.ui.components.SortOptionsMenu
import com.po4yka.bitesizereader.ui.components.SummaryCardSkeleton
import com.po4yka.bitesizereader.ui.components.SummaryGridCard
import com.po4yka.bitesizereader.ui.components.SummarySearchBar
import com.po4yka.bitesizereader.ui.components.SwipeableSummaryCard
import com.po4yka.bitesizereader.ui.components.TrendingTopicsSection
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing

/**
 * Summary list screen with search, filtering, sorting, swipe actions,
 * infinite scroll, and pull-to-refresh using Carbon Design System.
 */
@Suppress("FunctionNaming", "LongMethod")
@Composable
fun SummaryListScreen(
    component: SummaryListComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel: SummaryListViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        // Header with actions
        SummaryListHeader(
            title = "Read Later",
            isSearchActive = state.search.isActive,
            layoutMode = state.layout.layoutMode,
            sortOrder = state.filter.sortOrder,
            onRefresh = { viewModel.syncAndLoad() },
            onToggleSearch = { viewModel.toggleSearch() },
            onToggleLayout = {
                viewModel.setLayoutMode(
                    if (state.layout.layoutMode == LayoutMode.LIST) LayoutMode.GRID else LayoutMode.LIST,
                )
            },
            onSortOrderChanged = { viewModel.setSortOrder(it) },
            onSubmitUrlClicked = { component.onSubmitUrlClicked() },
        )

        // Search bar (collapsible)
        AnimatedVisibility(
            visible = state.search.isActive,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            SummarySearchBar(
                query = state.search.query,
                onQueryChange = { query -> viewModel.onSearchQueryChanged(query) },
                onClose = { viewModel.toggleSearch() },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Trending topics (shown when search is active but query is empty)
        AnimatedVisibility(
            visible = state.search.isActive &&
                state.search.query.isBlank() &&
                state.search.trendingTopics.isNotEmpty(),
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            TrendingTopicsSection(
                topics = state.search.trendingTopics,
                onTopicClick = { topic -> viewModel.selectTrendingTopic(topic) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Recent searches (shown when search is active but query is empty)
        AnimatedVisibility(
            visible = state.search.isActive &&
                state.search.query.isBlank() &&
                state.search.recentSearches.isNotEmpty(),
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            RecentSearchesSection(
                searches = state.search.recentSearches,
                onSearchClick = { query -> viewModel.selectRecentSearch(query) },
                onDeleteSearch = { query -> viewModel.deleteRecentSearch(query) },
                onClearAll = { viewModel.clearSearchHistory() },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Filter chips
        FilterChipsRow(
            readFilter = state.filter.readFilter,
            onReadFilterChange = { viewModel.setReadFilter(it) },
            availableTags = state.filter.availableTags,
            selectedTag = state.filter.selectedTag,
            onTagSelected = { viewModel.onTagSelected(it) },
            modifier = Modifier.fillMaxWidth(),
        )

        // Content with pull-to-refresh
        PullToRefreshContainer(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.weight(1f),
        ) {
            SummaryListContent(
                state = state,
                onSummaryClick = { id -> component.onSummaryClicked(id) },
                onDelete = { id -> viewModel.deleteSummary(id) },
                onArchive = { id -> viewModel.archiveSummary(id) },
                onMarkRead = { id -> viewModel.markAsRead(id) },
                onFavoriteClick = { id -> viewModel.toggleFavorite(id) },
                onLoadMore = { lastIndex -> viewModel.loadMoreIfNeeded(lastIndex) },
                onClearSearch = { viewModel.onSearchQueryChanged("") },
                onShowAllArticles = { viewModel.setReadFilter(ReadFilter.ALL) },
                onRetry = { viewModel.syncAndLoad() },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Suppress("FunctionNaming", "LongParameterList")
@Composable
private fun SummaryListHeader(
    title: String,
    isSearchActive: Boolean,
    layoutMode: LayoutMode,
    sortOrder: SortOrder,
    onRefresh: () -> Unit,
    onToggleSearch: () -> Unit,
    onToggleLayout: () -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit,
    onSubmitUrlClicked: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(Dimensions.headerHeight)
                .background(Carbon.theme.background)
                .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = Carbon.typography.heading04,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.weight(1f),
        )

        // Add URL button
        IconButton(onClick = onSubmitUrlClicked) {
            Icon(
                imageVector = CarbonIcons.Add,
                contentDescription = "Submit URL",
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(IconSizes.md),
            )
        }

        // Search toggle
        IconButton(onClick = onToggleSearch) {
            Icon(
                imageVector = if (isSearchActive) CarbonIcons.Close else CarbonIcons.Search,
                contentDescription = if (isSearchActive) "Close search" else "Search",
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(IconSizes.md),
            )
        }

        // Layout toggle
        IconButton(onClick = onToggleLayout) {
            Icon(
                imageVector =
                    if (layoutMode == LayoutMode.LIST) {
                        CarbonIcons.Grid
                    } else {
                        CarbonIcons.List
                    },
                contentDescription =
                    if (layoutMode == LayoutMode.LIST) {
                        "Switch to grid view"
                    } else {
                        "Switch to list view"
                    },
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(IconSizes.md),
            )
        }

        // Sort menu
        SortOptionsMenu(
            currentSortOrder = sortOrder,
            onSortOrderSelected = onSortOrderChanged,
        )

        // Refresh button
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = CarbonIcons.Renew,
                contentDescription = "Refresh",
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(IconSizes.md),
            )
        }
    }
}

@Suppress("FunctionNaming", "LongParameterList", "LongMethod", "CyclomaticComplexMethod")
@Composable
private fun SummaryListContent(
    state: SummaryListState,
    onSummaryClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onArchive: (String) -> Unit,
    onMarkRead: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onLoadMore: (Int) -> Unit,
    onClearSearch: () -> Unit,
    onShowAllArticles: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        // Initial loading - show skeleton
        state.isLoading && state.summaries.isEmpty() -> {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(5) {
                    SummaryCardSkeleton()
                }
            }
        }

        // Error state
        state.error != null && state.summaries.isEmpty() -> {
            ContextualEmptyState(
                type = EmptyStateType.ERROR,
                onAction = onRetry,
                modifier = modifier,
            )
        }

        // Empty states
        state.summaries.isEmpty() -> {
            val emptyStateType =
                when {
                    state.search.query.isNotBlank() -> EmptyStateType.NO_SEARCH_RESULTS
                    state.filter.readFilter == ReadFilter.UNREAD -> EmptyStateType.NO_UNREAD_ARTICLES
                    state.filter.readFilter == ReadFilter.READ -> EmptyStateType.NO_READ_ARTICLES
                    state.filter.readFilter == ReadFilter.ARCHIVED -> EmptyStateType.NO_ARCHIVED_ARTICLES
                    else -> EmptyStateType.NO_ARTICLES
                }
            val onAction: (() -> Unit)? =
                when (emptyStateType) {
                    EmptyStateType.NO_SEARCH_RESULTS -> onClearSearch
                    EmptyStateType.NO_UNREAD_ARTICLES,
                    EmptyStateType.NO_READ_ARTICLES,
                    EmptyStateType.NO_ARCHIVED_ARTICLES,
                    -> onShowAllArticles
                    else -> null
                }
            ContextualEmptyState(
                type = emptyStateType,
                searchQuery = state.search.query.takeIf { it.isNotBlank() },
                onAction = onAction,
                modifier = modifier,
            )
        }

        // Data loaded - show list or grid
        else -> {
            when (state.layout.layoutMode) {
                LayoutMode.LIST -> {
                    SummaryListView(
                        state = state,
                        onSummaryClick = onSummaryClick,
                        onDelete = onDelete,
                        onArchive = onArchive,
                        onMarkRead = onMarkRead,
                        onFavoriteClick = onFavoriteClick,
                        onLoadMore = onLoadMore,
                        modifier = modifier,
                    )
                }
                LayoutMode.GRID -> {
                    SummaryGridView(
                        state = state,
                        onSummaryClick = onSummaryClick,
                        onDelete = onDelete,
                        onMarkRead = onMarkRead,
                        onFavoriteClick = onFavoriteClick,
                        onLoadMore = onLoadMore,
                        modifier = modifier,
                    )
                }
            }
        }
    }
}

@Suppress("FunctionNaming", "LongParameterList")
@Composable
private fun SummaryListView(
    state: SummaryListState,
    onSummaryClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onArchive: (String) -> Unit,
    onMarkRead: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onLoadMore: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    // Infinite scroll trigger
    val lastVisibleIndex by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

    LaunchedEffect(lastVisibleIndex) {
        onLoadMore(lastVisibleIndex)
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = state.summaries,
            key = { it.id },
        ) { summary ->
            SwipeableSummaryCard(
                summary = summary,
                onClick = { onSummaryClick(summary.id) },
                onDelete = { onDelete(summary.id) },
                onMarkRead = { onMarkRead(summary.id) },
                onFavoriteClick = { onFavoriteClick(summary.id) },
                onArchiveClick = { onArchive(summary.id) },
            )
        }

        // Loading more indicator
        if (state.isLoadingMore) {
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                    contentAlignment = Alignment.Center,
                ) {
                    SmallLoading()
                }
            }
        }
    }
}

@Suppress("FunctionNaming", "LongParameterList")
@Composable
private fun SummaryGridView(
    state: SummaryListState,
    onSummaryClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onMarkRead: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onLoadMore: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()

    // Infinite scroll trigger
    val lastVisibleIndex by remember {
        derivedStateOf {
            gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

    LaunchedEffect(lastVisibleIndex) {
        onLoadMore(lastVisibleIndex)
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        state = gridState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = state.summaries,
            key = { it.id },
        ) { summary ->
            SummaryGridCard(
                summary = summary,
                onClick = { onSummaryClick(summary.id) },
                onDeleteClick = { onDelete(summary.id) },
                onMarkReadClick = { onMarkRead(summary.id) },
                onFavoriteClick = { onFavoriteClick(summary.id) },
            )
        }

        // Loading more indicator
        if (state.isLoadingMore) {
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                    contentAlignment = Alignment.Center,
                ) {
                    SmallLoading()
                }
            }
        }
    }
}
