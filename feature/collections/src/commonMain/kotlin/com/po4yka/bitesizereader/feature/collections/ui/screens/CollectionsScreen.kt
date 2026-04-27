package com.po4yka.bitesizereader.feature.collections.ui.screens

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
import androidx.compose.material3.HorizontalDivider
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
import com.po4yka.bitesizereader.core.ui.components.CarbonDialog
import com.po4yka.bitesizereader.core.ui.components.CarbonIconButton
import com.po4yka.bitesizereader.core.ui.components.CollectionItem
import com.po4yka.bitesizereader.core.ui.components.EmptyStateView
import com.po4yka.bitesizereader.core.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.core.ui.theme.Dimensions
import com.po4yka.bitesizereader.core.ui.theme.IconSizes
import com.po4yka.bitesizereader.core.ui.theme.Spacing
import bitesizereader.core.ui.generated.resources.Res
import bitesizereader.core.ui.generated.resources.collections_cancel
import bitesizereader.core.ui.generated.resources.collections_create
import bitesizereader.core.ui.generated.resources.collections_create_dialog_title
import bitesizereader.core.ui.generated.resources.collections_creating
import bitesizereader.core.ui.generated.resources.collections_description_label
import bitesizereader.core.ui.generated.resources.collections_description_placeholder
import bitesizereader.core.ui.generated.resources.collections_empty_message
import bitesizereader.core.ui.generated.resources.collections_empty_title
import bitesizereader.core.ui.generated.resources.collections_loading
import bitesizereader.core.ui.generated.resources.collections_name_label
import bitesizereader.core.ui.generated.resources.collections_name_placeholder
import bitesizereader.core.ui.generated.resources.collections_new
import bitesizereader.core.ui.generated.resources.collections_section_other
import bitesizereader.core.ui.generated.resources.collections_section_work
import bitesizereader.core.ui.generated.resources.collections_title
import org.jetbrains.compose.resources.stringResource

private const val USER_COLLECTIONS_SPLIT_INDEX = 5

@Suppress("FunctionNaming", "LongMethod")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionsScreen(
    component: CollectionsComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel = component.viewModel
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
                title = stringResource(Res.string.collections_loading),
                message = "",
                icon = CarbonIcons.Folder,
                modifier = Modifier.fillMaxSize(),
            )
        } else if (state.collections.isEmpty()) {
            EmptyStateView(
                title = stringResource(Res.string.collections_empty_title),
                message = stringResource(Res.string.collections_empty_message),
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
                        SectionHeader(stringResource(Res.string.collections_section_work))
                    }
                    items(userCollections.take(USER_COLLECTIONS_SPLIT_INDEX)) { collection ->
                        CollectionItem(
                            collection = collection,
                            onClick = { component.onCollectionClicked(collection.id) },
                        )
                    }

                    if (userCollections.size > USER_COLLECTIONS_SPLIT_INDEX) {
                        stickyHeader {
                            SectionHeader(stringResource(Res.string.collections_section_other))
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
            text = stringResource(Res.string.collections_title),
            style = Carbon.typography.heading04,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.weight(1f),
        )

        CarbonIconButton(
            imageVector = CarbonIcons.Add,
            contentDescription = stringResource(Res.string.collections_new),
            onClick = onCreateClick,
            iconSize = IconSizes.md,
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

    CarbonDialog(
        onDismissRequest = { if (!isCreating) onDismiss() },
        title = stringResource(Res.string.collections_create_dialog_title),
        confirmButton = {
            Button(
                label = stringResource(Res.string.collections_create),
                onClick = { onConfirm(name, description) },
                isEnabled = name.isNotBlank() && !isCreating,
                buttonType = ButtonType.Primary,
            )
        },
        dismissButton = {
            Button(
                label = stringResource(Res.string.collections_cancel),
                onClick = onDismiss,
                isEnabled = !isCreating,
                buttonType = ButtonType.Ghost,
            )
        },
    ) {
        TextInput(
            label = stringResource(Res.string.collections_name_label),
            value = name,
            onValueChange = { name = it },
            placeholderText = stringResource(Res.string.collections_name_placeholder),
            state = if (isCreating) TextInputState.Disabled else TextInputState.Enabled,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = stringResource(Res.string.collections_description_label),
            value = description,
            onValueChange = { description = it },
            placeholderText = stringResource(Res.string.collections_description_placeholder),
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
                    text = stringResource(Res.string.collections_creating),
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
            }
        }
    }
}
