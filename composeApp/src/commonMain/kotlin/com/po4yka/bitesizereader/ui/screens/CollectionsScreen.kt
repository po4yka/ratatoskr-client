package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.CollectionType
import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import com.po4yka.bitesizereader.ui.components.CollectionItem
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CollectionsScreen(
    onCollectionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // In a real app we'd use a ViewModel, but for this generic view, injecting repo directly is quicker for now
    // or we can create a simple ViewModel. Let's use direct injection for this focused task unless complexity grows.
    val repository: CollectionRepository = koinInject()
    val collections by repository.getCollections().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Collections") },
                actions = {
                    IconButton(onClick = { /* TODO: Add collection */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Collection")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val systemCollections = collections.filter { it.type == CollectionType.System && it.id != "trash" }
            val midCollections = collections.filter { it.type == CollectionType.User }
            val trashCollection = collections.find { it.id == "trash" }

            // System Section (Unsorted, Read Later)
            items(systemCollections) { collection ->
                CollectionItem(
                    collection = collection,
                    onClick = { onCollectionClick(collection.id) }
                )
            }

            // Divider or Header
            if (midCollections.isNotEmpty()) {
                stickyHeader {
                    SectionHeader("Work") // Hardcoded from example, normally dynamic
                }
                items(midCollections.take(5)) { collection ->
                     CollectionItem(
                        collection = collection,
                        onClick = { onCollectionClick(collection.id) }
                    )
                }

                stickyHeader {
                    SectionHeader("Other")
                }
                 items(midCollections.drop(5)) { collection ->
                     CollectionItem(
                        collection = collection,
                        onClick = { onCollectionClick(collection.id) }
                    )
                }
            }

             // Trash at bottom
            trashCollection?.let {
                item {
                    HorizontalDivider()
                    CollectionItem(
                        collection = it,
                        onClick = { onCollectionClick(it.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
