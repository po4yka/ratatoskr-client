package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.po4yka.bitesizereader.presentation.navigation.SummaryListComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryListScreen(component: SummaryListComponent) {
    val state by component.viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Summaries") }) }
    ) { padding ->
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(contentPadding = padding) {
                items(state.summaries) { summary ->
                    ListItem(
                        leadingContent = {
                            summary.imageUrl?.let { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        },
                        headlineContent = { Text(summary.title) },
                        supportingContent = { Text(summary.sourceUrl) },
                        modifier = Modifier.clickable { component.onSummaryClicked(summary.id) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
