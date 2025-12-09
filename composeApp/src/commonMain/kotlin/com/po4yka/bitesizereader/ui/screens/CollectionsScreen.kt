package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
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
import com.po4yka.bitesizereader.domain.model.CollectionType
import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import com.po4yka.bitesizereader.ui.components.CollectionItem
import org.koin.compose.koinInject

/**
 * Collections screen using Carbon Design System
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionsScreen(
    onCollectionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository: CollectionRepository = koinInject()
    val collections by repository.getCollections().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Carbon.theme.background)
    ) {
        // Header
        item {
            CarbonCollectionsHeader(
                title = "Collections",
                onAddClick = { /* TODO: Add collection */ }
            )
        }

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
                SectionHeader("Work")
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
                HorizontalDivider(color = Carbon.theme.borderSubtle00)
                CollectionItem(
                    collection = it,
                    onClick = { onCollectionClick(it.id) }
                )
            }
        }
    }
}

@Composable
private fun CarbonCollectionsHeader(
    title: String,
    onAddClick: () -> Unit,
) {
    Row(
        modifier = Modifier
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

        IconButton(onClick = onAddClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Collection",
                tint = Carbon.theme.iconPrimary,
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = Carbon.typography.label01,
        color = Carbon.theme.textSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .background(Carbon.theme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
