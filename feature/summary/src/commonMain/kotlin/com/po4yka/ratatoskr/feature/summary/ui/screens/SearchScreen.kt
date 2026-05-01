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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.presentation.navigation.SearchComponent
import com.po4yka.ratatoskr.presentation.state.SearchMode
import com.po4yka.ratatoskr.presentation.state.SearchState
import com.po4yka.ratatoskr.core.ui.components.AppSearchField
import com.po4yka.ratatoskr.core.ui.components.frost.BracketIconButton
import com.po4yka.ratatoskr.core.ui.components.frost.MultiSelectChip
import com.po4yka.ratatoskr.core.ui.components.frost.FrostSpinner
import com.po4yka.ratatoskr.core.ui.components.ContextualEmptyState
import com.po4yka.ratatoskr.core.ui.components.EmptyStateType
import com.po4yka.ratatoskr.core.ui.components.InsightsSection
import com.po4yka.ratatoskr.core.ui.components.RecentSearchesSection
import com.po4yka.ratatoskr.core.ui.components.SummaryCard
import com.po4yka.ratatoskr.core.ui.components.TrendingTopicsSection
import com.po4yka.ratatoskr.core.ui.components.frost.MarkRange
import com.po4yka.ratatoskr.core.ui.components.frost.MarkStyle
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.search_filter_all
import ratatoskr.core.ui.generated.resources.search_filter_read
import ratatoskr.core.ui.generated.resources.search_filter_unread
import ratatoskr.core.ui.generated.resources.search_filters
import ratatoskr.core.ui.generated.resources.search_language
import ratatoskr.core.ui.generated.resources.search_language_all
import ratatoskr.core.ui.generated.resources.search_language_english
import ratatoskr.core.ui.generated.resources.search_language_russian
import ratatoskr.core.ui.generated.resources.search_mode_ai
import ratatoskr.core.ui.generated.resources.search_mode_text
import ratatoskr.core.ui.generated.resources.search_placeholder
import ratatoskr.core.ui.generated.resources.search_title
import ratatoskr.core.ui.generated.resources.search_toggle_filters
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
    val viewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(AppTheme.frostColors.page),
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
                .background(AppTheme.frostColors.page),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = AppTheme.spacing.line),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FrostText(
                text = stringResource(Res.string.search_title),
                style = AppTheme.frostType.monoEmph,
                color = AppTheme.frostColors.ink,
                modifier = Modifier.weight(1f),
            )

            BracketIconButton(
                onClick = onFilterClick,
                contentDescription = stringResource(Res.string.search_toggle_filters),
            ) {
                FrostIcon(
                    imageVector = AppIcons.Filter,
                    contentDescription = null,
                    modifier = Modifier.size(IconSizes.sm),
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
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.spacing.line)
                .padding(bottom = AppTheme.spacing.cell),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
    ) {
        AppSearchField(
            query = query,
            onQueryChange = onQueryChange,
            onClearQuery = onClearQuery,
            placeholder = stringResource(Res.string.search_placeholder),
            modifier = Modifier.weight(1f),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SearchModeChip(
                label = stringResource(Res.string.search_mode_text),
                isSelected = searchMode == SearchMode.FULLTEXT,
                onClick = {
                    if (searchMode != SearchMode.FULLTEXT) {
                        onModeToggle()
                    }
                },
            )
            SearchModeChip(
                label = stringResource(Res.string.search_mode_ai),
                isSelected = searchMode == SearchMode.SEMANTIC,
                onClick = {
                    if (searchMode != SearchMode.SEMANTIC) {
                        onModeToggle()
                    }
                },
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SearchModeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MultiSelectChip(
        label = label,
        selected = isSelected,
        onToggle = onClick,
        modifier = modifier,
    )
}

@Suppress("FunctionNaming")
@Composable
private fun SearchFiltersPanel(
    state: SearchState,
    onFiltersChanged: (com.po4yka.ratatoskr.presentation.state.SearchFilters) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(AppTheme.frostColors.page)
                .padding(AppTheme.spacing.line),
    ) {
        FrostText(
            text = stringResource(Res.string.search_filters),
            style = AppTheme.frostType.monoXs,
            color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
            modifier = Modifier.padding(bottom = AppTheme.spacing.cell),
        )

        // Read filter row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
        ) {
            ReadFilterChip(
                label = stringResource(Res.string.search_filter_all),
                isSelected =
                    state.filters.readFilter ==
                        com.po4yka.ratatoskr.domain.model.ReadFilter.ALL,
                onClick = {
                    onFiltersChanged(
                        state.filters.copy(
                            readFilter =
                                com.po4yka.ratatoskr.domain.model.ReadFilter.ALL,
                        ),
                    )
                },
            )
            ReadFilterChip(
                label = stringResource(Res.string.search_filter_read),
                isSelected =
                    state.filters.readFilter ==
                        com.po4yka.ratatoskr.domain.model.ReadFilter.READ,
                onClick = {
                    onFiltersChanged(
                        state.filters.copy(
                            readFilter =
                                com.po4yka.ratatoskr.domain.model.ReadFilter.READ,
                        ),
                    )
                },
            )
            ReadFilterChip(
                label = stringResource(Res.string.search_filter_unread),
                isSelected =
                    state.filters.readFilter ==
                        com.po4yka.ratatoskr.domain.model.ReadFilter.UNREAD,
                onClick = {
                    onFiltersChanged(
                        state.filters.copy(
                            readFilter =
                                com.po4yka.ratatoskr.domain.model.ReadFilter.UNREAD,
                        ),
                    )
                },
            )
        }

        // Language filter (semantic mode only)
        if (state.searchMode == SearchMode.SEMANTIC) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.cell))
            FrostText(
                text = stringResource(Res.string.search_language),
                style = AppTheme.frostType.monoXs,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                modifier = Modifier.padding(bottom = AppTheme.spacing.cell),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
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
    MultiSelectChip(
        label = label,
        selected = isSelected,
        onToggle = onClick,
        modifier = modifier,
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
                contentPadding = PaddingValues(vertical = AppTheme.spacing.cell),
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
                FrostSpinner(modifier = Modifier.size(48.dp))
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
        contentPadding = PaddingValues(AppTheme.spacing.line),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
    ) {
        items(
            items = results,
            key = { it.id },
        ) { summary ->
            // TODO: AtomMark match-highlight wiring — SummaryCard (core/ui) renders title and body
            // as plain String fields and does not accept AnnotatedString overloads. Once SummaryCard
            // exposes titleAnnotated/bodyAnnotated parameters (or is replaced with an inline row),
            // wire highlights here via:
            //   val query = state.query  (pass query down to SearchResultsList)
            //   val titleRanges = remember(summary.title, query) { computeMatchRanges(summary.title, query) }
            //   val bodySnippet = summary.content.take(150).replace("\n", " ")
            //   val bodyRanges = remember(bodySnippet, query) { computeMatchRanges(bodySnippet, query) }
            //   val titleAnnotated = rememberMarkedAnnotatedString(summary.title, titleRanges)
            //   val bodyAnnotated = rememberMarkedAnnotatedString(bodySnippet, bodyRanges)
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
                            .padding(AppTheme.spacing.line),
                    contentAlignment = Alignment.Center,
                ) {
                    FrostSpinner(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

/**
 * Computes [MarkRange] spans with [MarkStyle.Match] for every occurrence of each whitespace-
 * tokenized query term inside [text] (case-insensitive). Empty query returns an empty list.
 *
 * Intended for use with [com.po4yka.ratatoskr.core.ui.components.frost.rememberMarkedAnnotatedString].
 */
private fun computeMatchRanges(
    text: String,
    query: String,
): List<MarkRange> {
    if (query.isBlank()) return emptyList()
    val tokens = query.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    val ranges = mutableListOf<MarkRange>()
    val lowerText = text.lowercase()
    for (token in tokens) {
        val lowerToken = token.lowercase()
        var start = 0
        while (true) {
            val index = lowerText.indexOf(lowerToken, start)
            if (index == -1) break
            ranges += MarkRange(index, index + token.length, MarkStyle.Match)
            start = index + 1
        }
    }
    return ranges
}
