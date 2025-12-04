package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.bitesizereader.presentation.navigation.SummaryListComponent
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import com.po4yka.bitesizereader.ui.components.EmptyStateView
import com.po4yka.bitesizereader.ui.components.ErrorView
import com.po4yka.bitesizereader.ui.components.SummaryCard
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.lazy.rememberLazyListState

/**
 * Summary list screen with pagination and filters
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryListScreen(
    component: SummaryListComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel: SummaryListViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Summaries") },
                actions = {
                    IconButton(onClick = { viewModel.loadSummaries() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        SummaryListContent(
            state = state,
            onSummaryClick = { id -> component.onSummaryClicked(id) },
            onRefresh = { viewModel.loadSummaries() },
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun SummaryListContent(
    state: SummaryListState,
    onSummaryClick: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.error != null && state.summaries.isEmpty() -> {
            ErrorView(
                message = state.error!!,
                onRetry = onRefresh,
                modifier = modifier,
            )
        }
        state.summaries.isEmpty() && !state.isLoading -> {
            EmptyStateView(
                title = "No summaries yet",
                message = "Submit a URL to generate your first summary",
                modifier = modifier,
            )
        }
        else -> {
            val listState = rememberLazyListState()

            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = state.summaries,
                    key = { it.id },
                ) { summary ->
                    SummaryCard(
                        summary = summary,
                        onClick = { onSummaryClick(summary.id) },
                    )
                }
            }
        }
    }

    // Initial loading
    if (state.isLoading && state.summaries.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}
