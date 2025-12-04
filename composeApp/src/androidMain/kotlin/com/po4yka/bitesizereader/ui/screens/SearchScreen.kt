package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.po4yka.bitesizereader.presentation.navigation.SearchComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(component: SearchComponent) {
    val state by component.viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Search") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = component.viewModel::onQueryChanged,
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
            
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn {
                items(state.results) { summary ->
                    ListItem(
                        headlineContent = { Text(summary.title) },
                        modifier = Modifier.clickable { component.onSummaryClicked(summary.id) }
                    )
                }
            }
        }
    }
}
