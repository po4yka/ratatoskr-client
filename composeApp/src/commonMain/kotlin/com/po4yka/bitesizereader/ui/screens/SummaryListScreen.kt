package com.po4yka.bitesizereader.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.Loading
import com.po4yka.bitesizereader.presentation.navigation.SummaryListComponent
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import com.po4yka.bitesizereader.ui.components.EmptyStateView
import com.po4yka.bitesizereader.ui.components.ErrorView
import com.po4yka.bitesizereader.ui.components.SummaryCard

/**
 * Summary list screen with pagination and filters using Carbon Design System
 */
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
        // Header
        CarbonHeader(
            title = "Read Later",
            onRefresh = { viewModel.loadSummaries() },
        )

        // Content
        SummaryListContent(
            state = state,
            onSummaryClick = { id -> component.onSummaryClicked(id) },
            onRefresh = { viewModel.loadSummaries() },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CarbonHeader(
    title: String,
    onRefresh: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Carbon.theme.layer01)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )

        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = Carbon.theme.iconPrimary,
            )
        }
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
            contentAlignment = Alignment.Center,
        ) {
            Loading(modifier = Modifier.size(88.dp))
        }
    }
}
