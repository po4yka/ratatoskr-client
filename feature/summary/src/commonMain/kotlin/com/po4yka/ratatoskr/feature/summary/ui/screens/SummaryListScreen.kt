package com.po4yka.ratatoskr.feature.summary.ui.screens

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
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.ReadFilter
import com.po4yka.ratatoskr.domain.model.SortOrder
import com.po4yka.ratatoskr.presentation.navigation.SummaryListComponent
import com.po4yka.ratatoskr.presentation.state.LayoutMode
import com.po4yka.ratatoskr.presentation.state.SummaryListState
import com.po4yka.ratatoskr.core.ui.components.AppSmallSpinner
import com.po4yka.ratatoskr.core.ui.components.ContextualEmptyState
import com.po4yka.ratatoskr.core.ui.components.AppIconButton
import com.po4yka.ratatoskr.core.ui.components.EmptyStateType
import com.po4yka.ratatoskr.core.ui.components.FilterChipsRow
import com.po4yka.ratatoskr.core.ui.components.PullToRefreshContainer
import com.po4yka.ratatoskr.core.ui.components.ReadingGoalCard
import com.po4yka.ratatoskr.core.ui.components.RecentSearchesSection
import com.po4yka.ratatoskr.core.ui.components.SortOptionsMenu
import com.po4yka.ratatoskr.core.ui.components.SummaryCardSkeleton
import com.po4yka.ratatoskr.core.ui.components.SummaryGridCard
import com.po4yka.ratatoskr.core.ui.components.SummarySearchBar
import com.po4yka.ratatoskr.core.ui.components.SwipeableSummaryCard
import com.po4yka.ratatoskr.core.ui.components.RecommendationsSection
import com.po4yka.ratatoskr.core.ui.components.TrendingTopicsSection
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.summary_list_close_search
import ratatoskr.core.ui.generated.resources.summary_list_create_digest
import ratatoskr.core.ui.generated.resources.summary_list_days_ago
import ratatoskr.core.ui.generated.resources.summary_list_hours_ago
import ratatoskr.core.ui.generated.resources.summary_list_just_now
import ratatoskr.core.ui.generated.resources.summary_list_last_synced_stale
import ratatoskr.core.ui.generated.resources.summary_list_minutes_ago
import ratatoskr.core.ui.generated.resources.summary_list_offline
import ratatoskr.core.ui.generated.resources.summary_list_refresh
import ratatoskr.core.ui.generated.resources.summary_list_search
import ratatoskr.core.ui.generated.resources.summary_list_submit_url
import ratatoskr.core.ui.generated.resources.summary_list_switch_to_grid
import ratatoskr.core.ui.generated.resources.summary_list_switch_to_list
import ratatoskr.core.ui.generated.resources.summary_list_title
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
    val viewModel = component.viewModel
    val state by viewModel.state.collectAsState()
    val readingGoalState by component.readingGoalController.state.collectAsState()
    val recommendationsViewModel = component.recommendationsViewModel
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
                .background(AppTheme.colors.background),
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
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xxs),
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
                .background(AppTheme.colors.background)
                .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = AppTheme.type.heading04,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.weight(1f).semantics { heading() },
        )

        // Add URL button
        AppIconButton(
            imageVector = AppIcons.Add,
            contentDescription = stringResource(Res.string.summary_list_submit_url),
            onClick = onSubmitUrlClicked,
            iconSize = IconSizes.md,
        )

        // Create Digest button
        AppIconButton(
            imageVector = AppIcons.Document,
            contentDescription = stringResource(Res.string.summary_list_create_digest),
            onClick = onCreateDigestClicked,
            iconSize = IconSizes.md,
        )

        // Search toggle
        val searchDesc =
            if (isSearchActive) {
                stringResource(Res.string.summary_list_close_search)
            } else {
                stringResource(Res.string.summary_list_search)
            }
        AppIconButton(
            imageVector = if (isSearchActive) AppIcons.Close else AppIcons.Search,
            contentDescription = searchDesc,
            onClick = onToggleSearch,
            iconSize = IconSizes.md,
        )

        // Layout toggle
        AppIconButton(
            imageVector =
                if (layoutMode == LayoutMode.LIST) {
                    AppIcons.Grid
                } else {
                    AppIcons.List
                },
            contentDescription =
                if (layoutMode == LayoutMode.LIST) {
                    stringResource(Res.string.summary_list_switch_to_grid)
                } else {
                    stringResource(Res.string.summary_list_switch_to_list)
                },
            onClick = onToggleLayout,
            iconSize = IconSizes.md,
        )

        // Sort menu
        SortOptionsMenu(
            currentSortOrder = sortOrder,
            onSortOrderSelected = onSortOrderChanged,
        )

        // Refresh button
        AppIconButton(
            imageVector = AppIcons.Renew,
            contentDescription = stringResource(Res.string.summary_list_refresh),
            onClick = onRefresh,
            iconSize = IconSizes.md,
        )
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
                    AppSmallSpinner()
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
    val staleText =
        stringResource(
            Res.string.summary_list_last_synced_stale,
            formatTimeSince(lastSyncTime ?: now),
        )
    // Priority: offline > sync error > stale data
    val (backgroundColor, text) =
        when {
            isOffline -> AppTheme.colors.supportWarning to offlineText
            syncError != null -> AppTheme.colors.supportError to syncError
            else -> AppTheme.colors.supportWarning to staleText
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
                imageVector = AppIcons.WarningAlt,
                contentDescription = null,
                tint = AppTheme.colors.textOnColor,
                modifier = Modifier.size(IconSizes.sm),
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = text,
                style = AppTheme.type.label01,
                color = AppTheme.colors.textOnColor,
            )
        }
    }
}

@Composable
private fun formatTimeSince(instant: Instant): String {
    val elapsed = Clock.System.now() - instant
    return when {
        elapsed.inWholeMinutes < 1 -> stringResource(Res.string.summary_list_just_now)
        elapsed.inWholeMinutes < 60 -> stringResource(Res.string.summary_list_minutes_ago, elapsed.inWholeMinutes)
        elapsed.inWholeHours < 24 -> stringResource(Res.string.summary_list_hours_ago, elapsed.inWholeHours)
        else -> stringResource(Res.string.summary_list_days_ago, elapsed.inWholeDays)
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
                    AppSmallSpinner()
                }
            }
        }
    }
}
