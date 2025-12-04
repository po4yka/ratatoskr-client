package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.po4yka.bitesizereader.presentation.navigation.SummaryDetailComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDetailScreen(component: SummaryDetailComponent) {
    val state by component.viewModel.state.collectAsStateWithLifecycle()
    val summary = state.summary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Summary") },
                navigationIcon = {
                    IconButton(onClick = component::onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (summary != null) {
                Text(text = summary.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = summary.content, style = MaterialTheme.typography.bodyLarge)
            } else if (state.error != null) {
                Text(text = "Error: ${state.error}", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
