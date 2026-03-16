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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState
import com.po4yka.bitesizereader.domain.model.CollectionType
import com.po4yka.bitesizereader.presentation.navigation.CollectionsComponent
import com.po4yka.bitesizereader.presentation.viewmodel.CollectionsViewModel
import com.po4yka.bitesizereader.ui.components.CollectionItem
import com.po4yka.bitesizereader.ui.components.EmptyStateView
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing

private const val USER_COLLECTIONS_SPLIT_INDEX = 5

@Suppress("FunctionNaming", "LongMethod")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionsScreen(
    component: CollectionsComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel: CollectionsViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        // Header with "New Collection" button
        CollectionsHeader(
            onCreateClick = { viewModel.showCreateDialog() },
        )

        if (state.isLoading && state.collections.isEmpty()) {
            EmptyStateView(
                title = "Loading collections...",
                message = "",
                icon = CarbonIcons.Folder,
                modifier = Modifier.fillMaxSize(),
            )
        } else if (state.collections.isEmpty()) {
            EmptyStateView(
                title = "No collections yet",
                message = "Create collections to organize your articles",
                icon = CarbonIcons.Folder,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                val systemCollections =
                    state.collections.filter { it.type == CollectionType.System && it.id != "trash" }
                val userCollections = state.collections.filter { it.type == CollectionType.User }
                val trashCollection = state.collections.find { it.id == "trash" }

                items(systemCollections) { collection ->
                    CollectionItem(
                        collection = collection,
                        onClick = { component.onCollectionClicked(collection.id) },
                    )
                }

                if (userCollections.isNotEmpty()) {
                    stickyHeader {
                        SectionHeader("Work")
                    }
                    items(userCollections.take(USER_COLLECTIONS_SPLIT_INDEX)) { collection ->
                        CollectionItem(
                            collection = collection,
                            onClick = { component.onCollectionClicked(collection.id) },
                        )
                    }

                    if (userCollections.size > USER_COLLECTIONS_SPLIT_INDEX) {
                        stickyHeader {
                            SectionHeader("Other")
                        }
                        items(userCollections.drop(USER_COLLECTIONS_SPLIT_INDEX)) { collection ->
                            CollectionItem(
                                collection = collection,
                                onClick = { component.onCollectionClicked(collection.id) },
                            )
                        }
                    }
                }

                trashCollection?.let {
                    item {
                        HorizontalDivider(color = Carbon.theme.borderSubtle00)
                        CollectionItem(
                            collection = it,
                            onClick = { component.onCollectionClicked(it.id) },
                        )
                    }
                }
            }
        }
    }

    // Create Collection Dialog
    if (state.showCreateDialog) {
        CreateCollectionDialog(
            isCreating = state.isCreating,
            createError = state.createError,
            onDismiss = { viewModel.dismissCreateDialog() },
            onConfirm = { name, description -> viewModel.createCollection(name, description) },
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CollectionsHeader(onCreateClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(Dimensions.headerHeight)
                .background(Carbon.theme.background)
                .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Collections",
            style = Carbon.typography.heading04,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.weight(1f),
        )

        IconButton(onClick = onCreateClick) {
            Icon(
                imageVector = CarbonIcons.Add,
                contentDescription = "New Collection",
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(IconSizes.md),
            )
        }
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
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
    )
}

@Suppress("FunctionNaming")
@Composable
private fun CreateCollectionDialog(
    isCreating: Boolean,
    createError: String?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isCreating) onDismiss() },
        containerColor = Carbon.theme.layer01,
        title = {
            Text(
                text = "New Collection",
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                TextInput(
                    label = "Name",
                    value = name,
                    onValueChange = { name = it },
                    placeholderText = "Collection name",
                    state = if (isCreating) TextInputState.Disabled else TextInputState.Enabled,
                    modifier = Modifier.fillMaxWidth(),
                )

                TextInput(
                    label = "Description",
                    value = description,
                    onValueChange = { description = it },
                    placeholderText = "Optional description",
                    state = if (isCreating) TextInputState.Disabled else TextInputState.Enabled,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (createError != null) {
                    Text(
                        text = createError,
                        style = Carbon.typography.label01,
                        color = Carbon.theme.supportError,
                    )
                }

                if (isCreating) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        SmallLoading()
                        Text(
                            text = "Creating...",
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textSecondary,
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                label = "Create",
                onClick = { onConfirm(name, description) },
                isEnabled = name.isNotBlank() && !isCreating,
                buttonType = ButtonType.Primary,
            )
        },
        dismissButton = {
            Button(
                label = "Cancel",
                onClick = onDismiss,
                isEnabled = !isCreating,
                buttonType = ButtonType.Ghost,
            )
        },
    )
}
