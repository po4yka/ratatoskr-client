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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.Loading
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState
import com.po4yka.bitesizereader.presentation.state.SearchState
import com.po4yka.bitesizereader.presentation.viewmodel.SearchViewModel
import com.po4yka.bitesizereader.ui.components.EmptyStateView
import com.po4yka.bitesizereader.ui.components.ErrorView
import com.po4yka.bitesizereader.ui.components.SummaryCard

/**
 * Search screen with filters using Carbon Design System
 */
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onSummaryClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Carbon.theme.background)
    ) {
        // Header with search
        CarbonSearchHeader(
            query = state.query,
            onQueryChange = { viewModel.onQueryChanged(it) },
            onBackClick = onBackClick,
        )

        // Content
        SearchContent(
            state = state,
            onSummaryClick = onSummaryClick,
            onRetry = { viewModel.onQueryChanged(state.query) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CarbonSearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Carbon.theme.layer01)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Carbon.theme.iconPrimary,
            )
        }

        TextInput(
            label = "",
            value = query,
            onValueChange = onQueryChange,
            placeholderText = "Search summaries...",
            state = TextInputState.Enabled,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SearchContent(
    state: SearchState,
    onSummaryClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.error != null -> {
            ErrorView(
                message = state.error!!,
                onRetry = onRetry,
                modifier = modifier,
            )
        }
        state.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Loading(modifier = Modifier.size(88.dp))
            }
        }
        state.results.isEmpty() && state.query.isNotEmpty() -> {
            EmptyStateView(
                title = "No results found",
                message = "Try different keywords or check your spelling",
                icon = Icons.Default.Search,
                modifier = modifier,
            )
        }
        state.results.isEmpty() -> {
            EmptyStateView(
                title = "Search Summaries",
                message = "Enter keywords to search through your summaries",
                icon = Icons.Default.Search,
                modifier = modifier,
            )
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        text = "${state.results.size} results found",
                        style = Carbon.typography.label01,
                        color = Carbon.theme.textSecondary,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }

                items(
                    items = state.results,
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
}
