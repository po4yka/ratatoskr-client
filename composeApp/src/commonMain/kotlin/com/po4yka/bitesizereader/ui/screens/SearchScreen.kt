package com.po4yka.bitesizereader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.presentation.navigation.SearchComponent
import com.po4yka.bitesizereader.presentation.state.SearchMode
import com.po4yka.bitesizereader.presentation.state.SearchState
import com.po4yka.bitesizereader.presentation.viewmodel.SearchViewModel
import com.po4yka.bitesizereader.ui.components.ContextualEmptyState
import com.po4yka.bitesizereader.ui.components.EmptyStateType
import com.po4yka.bitesizereader.ui.components.RecentSearchesSection
import com.po4yka.bitesizereader.ui.components.SummaryCard
import com.po4yka.bitesizereader.ui.components.TrendingTopicsSection
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.Spacing

/**
 * Full-screen search destination with search bar, filters, trending topics,
 * recent searches, and paginated results.
 */
@Suppress("FunctionNaming", "LongMethod")
@Composable
fun SearchScreen(
    component: SearchComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel: SearchViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        // Search Header with search bar and mode toggle
        SearchScreenHeader(
            query = state.query,
            searchMode = state.searchMode,
            onQueryChange = viewModel::onQueryChanged,
            onModeToggle = viewModel::toggleSearchMode,
            onFilterClick = viewModel::toggleFiltersPanel,
            onClearQuery = viewModel::clearResults,
        )

        // Filters Panel (collapsible)
        AnimatedVisibility(
            visible = state.showFilters,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            SearchFiltersPanel(
                state = state,
                onFiltersChanged = viewModel::updateFilters,
            )
        }

        // Content based on state
        SearchScreenContent(
            state = state,
            onSummaryClick = { id -> component.onSummaryClicked(id) },
            onLoadMore = { viewModel.loadMoreResults() },
            onTrendingTopicClick = viewModel::selectTrendingTopic,
            onRecentSearchClick = viewModel::selectRecentSearch,
            onDeleteRecentSearch = viewModel::deleteRecentSearch,
            onClearHistory = viewModel::clearSearchHistory,
            onRetry = { viewModel.onQueryChanged(state.query) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Suppress("FunctionNaming", "LongParameterList")
@Composable
private fun SearchScreenHeader(
    query: String,
    searchMode: SearchMode,
    onQueryChange: (String) -> Unit,
    onModeToggle: () -> Unit,
    onFilterClick: () -> Unit,
    onClearQuery: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01),
    ) {
        // Title row
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(Dimensions.detailHeaderHeight)
                    .padding(horizontal = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Search",
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
                modifier = Modifier.weight(1f),
            )

            // Filter toggle
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = CarbonIcons.Filter,
                    contentDescription = "Toggle filters",
                    tint = Carbon.theme.iconPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Search input row
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md)
                    .padding(bottom = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Search input
            Row(
                modifier =
                    Modifier
                        .weight(1f)
                        .background(Carbon.theme.layer02)
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xs + 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = CarbonIcons.Search,
                    contentDescription = null,
                    tint = Carbon.theme.iconSecondary,
                    modifier = Modifier.size(20.dp),
                )

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(horizontal = Spacing.sm),
                    singleLine = true,
                    textStyle =
                        Carbon.typography.bodyCompact01.copy(
                            color = Carbon.theme.textPrimary,
                        ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions =
                        KeyboardActions(
                            onSearch = { focusManager.clearFocus() },
                        ),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                text = "Search summaries...",
                                style = Carbon.typography.bodyCompact01,
                                color = Carbon.theme.textPlaceholder,
                            )
                        }
                        innerTextField()
                    },
                )

                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClearQuery,
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            imageVector = CarbonIcons.Close,
                            contentDescription = "Clear search",
                            tint = Carbon.theme.iconSecondary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(Spacing.xs))

            // Search mode toggle
            SearchModeChip(
                mode = searchMode,
                onClick = onModeToggle,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SearchModeChip(
    mode: SearchMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label =
        when (mode) {
            SearchMode.FULLTEXT -> "Text"
            SearchMode.SEMANTIC -> "AI"
        }

    Text(
        text = label,
        style = Carbon.typography.label01,
        color = Carbon.theme.textOnColor,
        modifier =
            modifier
                .background(Carbon.theme.linkPrimary)
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
    )
}

@Suppress("FunctionNaming")
@Composable
private fun SearchFiltersPanel(
    state: SearchState,
    onFiltersChanged: (com.po4yka.bitesizereader.presentation.state.SearchFilters) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01)
                .padding(Spacing.md),
    ) {
        Text(
            text = "Filters",
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
            modifier = Modifier.padding(bottom = Spacing.sm),
        )

        // Read filter row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            ReadFilterChip(
                label = "All",
                isSelected =
                    state.filters.readFilter ==
                        com.po4yka.bitesizereader.domain.model.ReadFilter.ALL,
                onClick = {
                    onFiltersChanged(
                        state.filters.copy(
                            readFilter =
                                com.po4yka.bitesizereader.domain.model.ReadFilter.ALL,
                        ),
                    )
                },
            )
            ReadFilterChip(
                label = "Read",
                isSelected =
                    state.filters.readFilter ==
                        com.po4yka.bitesizereader.domain.model.ReadFilter.READ,
                onClick = {
                    onFiltersChanged(
                        state.filters.copy(
                            readFilter =
                                com.po4yka.bitesizereader.domain.model.ReadFilter.READ,
                        ),
                    )
                },
            )
            ReadFilterChip(
                label = "Unread",
                isSelected =
                    state.filters.readFilter ==
                        com.po4yka.bitesizereader.domain.model.ReadFilter.UNREAD,
                onClick = {
                    onFiltersChanged(
                        state.filters.copy(
                            readFilter =
                                com.po4yka.bitesizereader.domain.model.ReadFilter.UNREAD,
                        ),
                    )
                },
            )
        }

        // Language filter (semantic mode only)
        if (state.searchMode == SearchMode.SEMANTIC) {
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "Language",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                ReadFilterChip(
                    label = "All",
                    isSelected = state.filters.language == null,
                    onClick = { onFiltersChanged(state.filters.copy(language = null)) },
                )
                ReadFilterChip(
                    label = "English",
                    isSelected = state.filters.language == "en",
                    onClick = { onFiltersChanged(state.filters.copy(language = "en")) },
                )
                ReadFilterChip(
                    label = "Russian",
                    isSelected = state.filters.language == "ru",
                    onClick = { onFiltersChanged(state.filters.copy(language = "ru")) },
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ReadFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        if (isSelected) {
            Carbon.theme.linkPrimary
        } else {
            Carbon.theme.layer02
        }
    val textColor =
        if (isSelected) {
            Carbon.theme.textOnColor
        } else {
            Carbon.theme.textPrimary
        }

    Text(
        text = label,
        style = Carbon.typography.label01,
        color = textColor,
        modifier =
            modifier
                .background(backgroundColor)
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
    )
}

@Suppress("FunctionNaming", "LongParameterList", "LongMethod", "CyclomaticComplexMethod")
@Composable
private fun SearchScreenContent(
    state: SearchState,
    onSummaryClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onTrendingTopicClick: (String) -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onDeleteRecentSearch: (String) -> Unit,
    onClearHistory: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        // Query is empty - show discovery content
        state.query.isEmpty() -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = Spacing.xs),
            ) {
                // Trending topics
                if (state.trendingTopics.isNotEmpty()) {
                    item {
                        TrendingTopicsSection(
                            topics = state.trendingTopics,
                            onTopicClick = onTrendingTopicClick,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // Recent searches
                if (state.recentSearches.isNotEmpty()) {
                    item {
                        RecentSearchesSection(
                            searches = state.recentSearches,
                            onSearchClick = onRecentSearchClick,
                            onDeleteSearch = onDeleteRecentSearch,
                            onClearAll = onClearHistory,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // Empty state if no discovery content
                if (state.trendingTopics.isEmpty() && state.recentSearches.isEmpty()) {
                    item {
                        ContextualEmptyState(
                            type = EmptyStateType.SEARCH_PROMPT,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        // Loading initial results
        state.isLoading && state.results.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = Carbon.theme.linkPrimary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp),
                )
            }
        }

        // Error state with no results
        state.error != null && state.results.isEmpty() -> {
            ContextualEmptyState(
                type = EmptyStateType.ERROR,
                onAction = onRetry,
                modifier = modifier.fillMaxSize(),
            )
        }

        // No results found
        state.results.isEmpty() -> {
            ContextualEmptyState(
                type = EmptyStateType.NO_SEARCH_RESULTS,
                searchQuery = state.query,
                modifier = modifier.fillMaxSize(),
            )
        }

        // Show results with pagination
        else -> {
            SearchResultsList(
                results = state.results,
                isLoadingMore = state.isLoadingMore,
                hasMore = state.hasMoreResults,
                onSummaryClick = onSummaryClick,
                onLoadMore = onLoadMore,
                modifier = modifier,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SearchResultsList(
    results: List<Summary>,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onSummaryClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    // Infinite scroll trigger
    val lastVisibleIndex by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

    // Trigger load more when approaching end
    LaunchedEffect(lastVisibleIndex, results.size) {
        if (hasMore && !isLoadingMore && lastVisibleIndex >= results.size - 3) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        items(
            items = results,
            key = { it.id },
        ) { summary ->
            SummaryCard(
                summary = summary,
                onClick = { onSummaryClick(summary.id) },
            )
        }

        // Loading more indicator
        if (isLoadingMore) {
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = Carbon.theme.linkPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}
