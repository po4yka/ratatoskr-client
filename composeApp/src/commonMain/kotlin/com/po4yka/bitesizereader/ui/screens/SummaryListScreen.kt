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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.domain.model.ReadFilter
import com.po4yka.bitesizereader.domain.model.SortOrder
import com.po4yka.bitesizereader.presentation.navigation.SummaryListComponent
import com.po4yka.bitesizereader.presentation.state.LayoutMode
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalViewModel
import com.po4yka.bitesizereader.ui.components.ContextualEmptyState
import com.po4yka.bitesizereader.ui.components.EmptyStateType
import com.po4yka.bitesizereader.ui.components.FilterChipsRow
import com.po4yka.bitesizereader.ui.components.PullToRefreshContainer
import com.po4yka.bitesizereader.ui.components.ReadingGoalCard
import com.po4yka.bitesizereader.ui.components.RecentSearchesSection
import com.po4yka.bitesizereader.ui.components.SortOptionsMenu
import com.po4yka.bitesizereader.ui.components.SummaryCardSkeleton
import com.po4yka.bitesizereader.ui.components.SummaryGridCard
import com.po4yka.bitesizereader.ui.components.SummarySearchBar
import com.po4yka.bitesizereader.ui.components.SwipeableSummaryCard
import com.po4yka.bitesizereader.ui.components.RecommendationsSection
import com.po4yka.bitesizereader.ui.components.TrendingTopicsSection
import com.po4yka.bitesizereader.presentation.viewmodel.RecommendationsViewModel
import org.koin.compose.koinInject
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.summary_list_close_search
import bitesizereader.composeapp.generated.resources.summary_list_create_digest
import bitesizereader.composeapp.generated.resources.summary_list_offline
import bitesizereader.composeapp.generated.resources.summary_list_refresh
import bitesizereader.composeapp.generated.resources.summary_list_search
import bitesizereader.composeapp.generated.resources.summary_list_submit_url
import bitesizereader.composeapp.generated.resources.summary_list_switch_to_grid
import bitesizereader.composeapp.generated.resources.summary_list_switch_to_list
import bitesizereader.composeapp.generated.resources.summary_list_title
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.Duration.Companion.hours
import org.jetbrains.compose.resources.stringResource

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
    val readingGoalViewModel: ReadingGoalViewModel = koinInject()
    val readingGoalState by readingGoalViewModel.state.collectAsState()
    val recommendationsViewModel: RecommendationsViewModel = koinInject()
    val recommendationsState by recommendationsViewModel.state.collectAsState()

    val onRefresh = remember<() -> Unit>(viewModel) { { viewModel.syncAndLoad() } }
    val onToggleSearch = remember<() -> Unit>(viewModel) { { viewModel.toggleSearch() } }
    val onSortOrderChanged = remember<(SortOrder) -> Unit>(viewModel) { { viewModel.setSortOrder(it) } }
    val onSubmitUrlClicked = remember<() -> Unit>(component) { { component.onSubmitUrlClicked() } }
    val onCreateDigestClicked = remember<() -> Unit>(component) { { component.onCreateDigestClicked() } }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        // Header with actions
        SummaryListHeader(
            title = stringResource(Res.string.summary_list_title),
            isSearchActive = state.search.isActive,
            layoutMode = state.layout.layoutMode,
            sortOrder = state.filter.sortOrder,
            onRefresh = onRefresh,
            onToggleSearch = onToggleSearch,
            onToggleLayout = {
                viewModel.setLayoutMode(
                    if (state.layout.layoutMode == LayoutMode.LIST) LayoutMode.GRID else LayoutMode.LIST,
                )
            },
            onSortOrderChanged = onSortOrderChanged,
            onSubmitUrlClicked = onSubmitUrlClicked,
            onCreateDigestClicked = onCreateDigestClicked,
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
            visible =
                state.search.isActive &&
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
            visible =
                state.search.isActive &&
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

        // Reading goal progress card
        val goalProgress = readingGoalState.goalProgress
        if (goalProgress != null && goalProgress.goal.isEnabled) {
            ReadingGoalCard(
                goalProgress = goalProgress,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }

        // Recommendations strip
        if (recommendationsState.recommendations.isNotEmpty()) {
            RecommendationsSection(
                recommendations = recommendationsState.recommendations,
                onRecommendationClick = { id -> component.onSummaryClicked(id) },
                onDismiss = { id -> recommendationsViewModel.dismiss(id) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Offline / stale sync / error banner
        SyncStatusBanner(
            isOffline = state.isOffline,
            lastSyncTime = state.lastSyncTime,
            syncError = state.syncError,
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
    onCreateDigestClicked: () -> Unit,
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
            modifier = Modifier.weight(1f).semantics { heading() },
        )

        // Add URL button
        IconButton(onClick = onSubmitUrlClicked) {
            Icon(
                imageVector = CarbonIcons.Add,
                contentDescription = stringResource(Res.string.summary_list_submit_url),
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(IconSizes.md),
            )
        }

        // Create Digest button
        IconButton(onClick = onCreateDigestClicked) {
            Icon(
                imageVector = CarbonIcons.Document,
                contentDescription = stringResource(Res.string.summary_list_create_digest),
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(IconSizes.md),
            )
        }

        // Search toggle
        val searchDesc =
            if (isSearchActive) {
                stringResource(Res.string.summary_list_close_search)
            } else {
                stringResource(Res.string.summary_list_search)
            }
        IconButton(onClick = onToggleSearch) {
            Icon(
                imageVector = if (isSearchActive) CarbonIcons.Close else CarbonIcons.Search,
                contentDescription = searchDesc,
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
                        stringResource(Res.string.summary_list_switch_to_grid)
                    } else {
                        stringResource(Res.string.summary_list_switch_to_list)
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
                contentDescription = stringResource(Res.string.summary_list_refresh),
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
                items(count = 5, contentType = { "skeleton" }) {
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
            contentType = { "summary_card" },
        ) { summary ->
            SwipeableSummaryCard(
                summary = summary,
                onClick = { onSummaryClick(summary.id) },
                onDelete = { onDelete(summary.id) },
                onMarkRead = { onMarkRead(summary.id) },
                onFavoriteClick = { onFavoriteClick(summary.id) },
                onArchiveClick = { onArchive(summary.id) },
                modifier = Modifier.animateItem(),
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

@Suppress("FunctionNaming", "LongMethod")
@Composable
private fun SyncStatusBanner(
    isOffline: Boolean,
    lastSyncTime: Instant?,
    syncError: String?,
    modifier: Modifier = Modifier,
) {
    val now = remember { Clock.System.now() }
    val isStale = !isOffline && syncError == null && lastSyncTime != null && (now - lastSyncTime) > 24.hours
    val showBanner = isOffline || isStale || syncError != null

    val offlineText = stringResource(Res.string.summary_list_offline)
    // Priority: offline > sync error > stale data
    val (backgroundColor, text) =
        when {
            isOffline -> Carbon.theme.supportWarning to offlineText
            syncError != null -> Carbon.theme.supportError to syncError
            else -> Carbon.theme.supportWarning to "Last synced: ${formatTimeSince(lastSyncTime!!)} — may be outdated"
        }

    AnimatedVisibility(
        visible = showBanner,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .semantics { liveRegion = LiveRegionMode.Polite }
                    .background(backgroundColor)
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        ) {
            Icon(
                imageVector = CarbonIcons.WarningAlt,
                contentDescription = null,
                tint = Carbon.theme.textOnColor,
                modifier = Modifier.size(IconSizes.sm),
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = text,
                style = Carbon.typography.label01,
                color = Carbon.theme.textOnColor,
            )
        }
    }
}

private fun formatTimeSince(instant: Instant): String {
    val elapsed = Clock.System.now() - instant
    return when {
        elapsed.inWholeMinutes < 1 -> "just now"
        elapsed.inWholeMinutes < 60 -> "${elapsed.inWholeMinutes}m ago"
        elapsed.inWholeHours < 24 -> "${elapsed.inWholeHours}h ago"
        else -> "${elapsed.inWholeDays}d ago"
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
            contentType = { "summary_grid_card" },
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
