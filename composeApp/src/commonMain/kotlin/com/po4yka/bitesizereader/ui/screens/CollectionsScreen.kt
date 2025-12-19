package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.CollectionType
import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import com.po4yka.bitesizereader.ui.components.CollectionItem
import com.po4yka.bitesizereader.ui.components.EmptyStateView
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import org.koin.compose.koinInject

private const val USER_COLLECTIONS_SPLIT_INDEX = 5

/**
 * Collections screen using Carbon Design System
 */
@Suppress("FunctionNaming")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionsScreen(
    onCollectionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val repository: CollectionRepository = koinInject()
    val collections by repository.getCollections().collectAsState(initial = emptyList())

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        // Header
        CollectionsHeader()

        if (collections.isEmpty()) {
            // Empty state
            EmptyStateView(
                title = "No collections yet",
                message = "Create collections to organize your articles",
                icon = CarbonIcons.Folder,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            // Collections list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                val systemCollections =
                    collections.filter { it.type == CollectionType.System && it.id != "trash" }
                val userCollections = collections.filter { it.type == CollectionType.User }
                val trashCollection = collections.find { it.id == "trash" }

                // System Section (Unsorted, Read Later)
                items(systemCollections) { collection ->
                    CollectionItem(
                        collection = collection,
                        onClick = { onCollectionClick(collection.id) },
                    )
                }

                // User collections sections
                if (userCollections.isNotEmpty()) {
                    stickyHeader {
                        SectionHeader("Work")
                    }
                    items(userCollections.take(USER_COLLECTIONS_SPLIT_INDEX)) { collection ->
                        CollectionItem(
                            collection = collection,
                            onClick = { onCollectionClick(collection.id) },
                        )
                    }

                    if (userCollections.size > USER_COLLECTIONS_SPLIT_INDEX) {
                        stickyHeader {
                            SectionHeader("Other")
                        }
                        items(userCollections.drop(USER_COLLECTIONS_SPLIT_INDEX)) { collection ->
                            CollectionItem(
                                collection = collection,
                                onClick = { onCollectionClick(collection.id) },
                            )
                        }
                    }
                }

                // Trash at bottom
                trashCollection?.let {
                    item {
                        HorizontalDivider(color = Carbon.theme.borderSubtle00)
                        CollectionItem(
                            collection = it,
                            onClick = { onCollectionClick(it.id) },
                        )
                    }
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CollectionsHeader() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Carbon.theme.background)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Collections",
            style = Carbon.typography.heading04,
            color = Carbon.theme.textPrimary,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = Carbon.typography.label01,
        color = Carbon.theme.textSecondary,
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Carbon.theme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
