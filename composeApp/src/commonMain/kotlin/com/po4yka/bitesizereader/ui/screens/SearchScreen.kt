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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.presentation.navigation.SearchComponent
import com.po4yka.bitesizereader.presentation.state.SearchMode
import com.po4yka.bitesizereader.presentation.state.SearchState
import com.po4yka.bitesizereader.presentation.viewmodel.SearchViewModel
import com.po4yka.bitesizereader.ui.components.ContextualEmptyState
import com.po4yka.bitesizereader.ui.components.EmptyStateType
import com.po4yka.bitesizereader.ui.components.InsightsSection
import com.po4yka.bitesizereader.ui.components.RecentSearchesSection
import com.po4yka.bitesizereader.ui.components.SummaryCard
import com.po4yka.bitesizereader.ui.components.TrendingTopicsSection
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.Spacing
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.search_clear
import bitesizereader.composeapp.generated.resources.search_filter_all
import bitesizereader.composeapp.generated.resources.search_filter_read
import bitesizereader.composeapp.generated.resources.search_filter_unread
import bitesizereader.composeapp.generated.resources.search_filters
import bitesizereader.composeapp.generated.resources.search_language
import bitesizereader.composeapp.generated.resources.search_language_all
import bitesizereader.composeapp.generated.resources.search_language_english
import bitesizereader.composeapp.generated.resources.search_language_russian
import bitesizereader.composeapp.generated.resources.search_mode_ai
import bitesizereader.composeapp.generated.resources.search_mode_text
import bitesizereader.composeapp.generated.resources.search_placeholder
import bitesizereader.composeapp.generated.resources.search_title
import bitesizereader.composeapp.generated.resources.search_toggle_filters
import org.jetbrains.compose.resources.stringResource

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
            onInsightClick = { id -> component.onSummaryClicked(id) },
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
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(Dimensions.detailHeaderHeight)
                    .padding(horizontal = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.search_title),
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
                modifier = Modifier.weight(1f),
            )

            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = CarbonIcons.Filter,
                    contentDescription = stringResource(Res.string.search_toggle_filters),
                    tint = Carbon.theme.iconPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        SearchInputRow(
            query = query,
            searchMode = searchMode,
            onQueryChange = onQueryChange,
            onClearQuery = onClearQuery,
            onModeToggle = onModeToggle,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SearchInputRow(
    query: String,
    searchMode: SearchMode,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onModeToggle: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md)
                .padding(bottom = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
                            text = stringResource(Res.string.search_placeholder),
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
                        contentDescription = stringResource(Res.string.search_clear),
                        tint = Carbon.theme.iconSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(Spacing.xs))

        SearchModeChip(
            mode = searchMode,
            onClick = onModeToggle,
        )
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
            SearchMode.FULLTEXT -> stringResource(Res.string.search_mode_text)
            SearchMode.SEMANTIC -> stringResource(Res.string.search_mode_ai)
        }

    Text(
        text = label,
        style = Carbon.typography.label01,
        color = Carbon.theme.textOnColor,
        modifier =
            modifier
                .semantics { role = Role.Button }
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
            text = stringResource(Res.string.search_filters),
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
                label = stringResource(Res.string.search_filter_all),
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
                label = stringResource(Res.string.search_filter_read),
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
                label = stringResource(Res.string.search_filter_unread),
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
                text = stringResource(Res.string.search_language),
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                ReadFilterChip(
                    label = stringResource(Res.string.search_language_all),
                    isSelected = state.filters.language == null,
                    onClick = { onFiltersChanged(state.filters.copy(language = null)) },
                )
                ReadFilterChip(
                    label = stringResource(Res.string.search_language_english),
                    isSelected = state.filters.language == "en",
                    onClick = { onFiltersChanged(state.filters.copy(language = "en")) },
                )
                ReadFilterChip(
                    label = stringResource(Res.string.search_language_russian),
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
                .semantics { role = Role.Button }
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
    onInsightClick: (String) -> Unit,
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

                // Insights
                if (state.insights.isNotEmpty()) {
                    item {
                        InsightsSection(
                            insights = state.insights,
                            onSummaryClick = onInsightClick,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // Empty state if no discovery content
                if (state.trendingTopics.isEmpty() && state.recentSearches.isEmpty() && state.insights.isEmpty()) {
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
