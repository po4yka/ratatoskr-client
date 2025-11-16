package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import com.po4yka.bitesizereader.ui.components.EmptyStateView
import com.po4yka.bitesizereader.ui.components.ErrorView
import com.po4yka.bitesizereader.ui.components.SelectableTagChip
import com.po4yka.bitesizereader.ui.components.SummaryCard

/**
 * Summary list screen with pagination and filters
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryListScreen(
    viewModel: SummaryListViewModel,
    onSummaryClick: (Int) -> Unit,
    onSubmitUrlClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Summaries") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onSubmitUrlClick) {
                Icon(Icons.Default.Add, contentDescription = "Submit URL")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        SummaryListContent(
            state = state,
            onSummaryClick = onSummaryClick,
            onRefresh = { viewModel.loadSummaries(refresh = true) },
            onLoadMore = { viewModel.loadMore() },
            modifier = Modifier.padding(paddingValues)
        )

        if (showFilterSheet) {
            FilterBottomSheet(
                selectedTags = state.filters.topicTags,
                showReadOnly = state.filters.readStatus?.let { it == "read" } ?: false,
                showUnreadOnly = state.filters.readStatus?.let { it == "unread" } ?: false,
                onTagToggle = { tag ->
                    viewModel.toggleTagFilter(tag)
                },
                onReadFilterChange = { showRead, showUnread ->
                    viewModel.setReadFilter(
                        when {
                            showRead -> "read"
                            showUnread -> "unread"
                            else -> null
                        }
                    )
                },
                onDismiss = { showFilterSheet = false },
                onClearFilters = { viewModel.clearFilters() }
            )
        }
    }
}

@Composable
private fun SummaryListContent(
    state: SummaryListState,
    onSummaryClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        state.error != null && state.summaries.isEmpty() -> {
            ErrorView(
                message = state.error,
                onRetry = onRefresh,
                modifier = modifier
            )
        }
        state.summaries.isEmpty() && !state.isLoading -> {
            EmptyStateView(
                title = "No summaries yet",
                message = "Submit a URL to generate your first summary",
                modifier = modifier
            )
        }
        else -> {
            val listState = rememberLazyListState()

            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.summaries,
                    key = { it.id }
                ) { summary ->
                    SummaryCard(
                        summary = summary,
                        onClick = { onSummaryClick(summary.id) }
                    )
                }

                // Loading indicator at bottom
                if (state.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                            )
                        }
                    }
                }
            }

            // Detect when user scrolls near bottom
            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo }
                    .collect { layoutInfo ->
                        val totalItems = layoutInfo.totalItemsCount
                        val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                        if (totalItems > 0 && lastVisibleItem >= totalItems - 3 && !state.isLoadingMore) {
                            onLoadMore()
                        }
                    }
            }
        }
    }

    // Initial loading
    if (state.isLoading && state.summaries.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    selectedTags: List<String>,
    showReadOnly: Boolean,
    showUnreadOnly: Boolean,
    onTagToggle: (String) -> Unit,
    onReadFilterChange: (Boolean, Boolean) -> Unit,
    onDismiss: () -> Unit,
    onClearFilters: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(onClick = onClearFilters) {
                    Text("Clear All")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Read status filter
            Text(
                text = "Reading Status",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectableTagChip(
                    tag = "Read",
                    selected = showReadOnly,
                    onSelectedChange = { onReadFilterChange(it, false) }
                )
                SelectableTagChip(
                    tag = "Unread",
                    selected = showUnreadOnly,
                    onSelectedChange = { onReadFilterChange(false, it) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Topic tags filter (would be populated from available tags)
            Text(
                text = "Topics",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select topics to filter (coming soon)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
