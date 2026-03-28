package com.po4yka.bitesizereader.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.Loading
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState
import com.po4yka.bitesizereader.domain.model.CollaboratorRole
import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.CollectionAcl
import com.po4yka.bitesizereader.domain.model.CollectionInvite
import com.po4yka.bitesizereader.presentation.navigation.CollectionViewComponent
import com.po4yka.bitesizereader.presentation.state.CollectionHeaderState
import com.po4yka.bitesizereader.presentation.state.CollectionItemsState
import com.po4yka.bitesizereader.presentation.state.CollectionSettingsState
import com.po4yka.bitesizereader.presentation.state.CollectionSharingState
import com.po4yka.bitesizereader.presentation.state.CollectionViewTab
import com.po4yka.bitesizereader.ui.components.CarbonDialog
import com.po4yka.bitesizereader.ui.components.CarbonTextArea
import com.po4yka.bitesizereader.ui.components.EmptyStateView
import com.po4yka.bitesizereader.ui.components.ErrorView
import com.po4yka.bitesizereader.ui.components.SummaryCard
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.Spacing
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.a11y_navigate_back
import bitesizereader.composeapp.generated.resources.collection_view_collaborators
import bitesizereader.composeapp.generated.resources.collection_view_creating_invite
import bitesizereader.composeapp.generated.resources.collection_view_create_invite_viewer
import bitesizereader.composeapp.generated.resources.collection_view_danger_zone
import bitesizereader.composeapp.generated.resources.collection_view_default_title
import bitesizereader.composeapp.generated.resources.collection_view_delete_action
import bitesizereader.composeapp.generated.resources.collection_view_delete_collection
import bitesizereader.composeapp.generated.resources.collection_view_delete_dialog_message
import bitesizereader.composeapp.generated.resources.collection_view_delete_dialog_title
import bitesizereader.composeapp.generated.resources.collection_view_deleting
import bitesizereader.composeapp.generated.resources.collection_view_description
import bitesizereader.composeapp.generated.resources.collection_view_empty_items_message
import bitesizereader.composeapp.generated.resources.collection_view_empty_items_title
import bitesizereader.composeapp.generated.resources.collection_view_expires
import bitesizereader.composeapp.generated.resources.collection_view_invite_link
import bitesizereader.composeapp.generated.resources.collection_view_invite_token
import bitesizereader.composeapp.generated.resources.collection_view_items_count
import bitesizereader.composeapp.generated.resources.collection_view_name
import bitesizereader.composeapp.generated.resources.collection_view_no_collaborators
import bitesizereader.composeapp.generated.resources.collection_view_remove
import bitesizereader.composeapp.generated.resources.collection_view_save_changes
import bitesizereader.composeapp.generated.resources.collection_view_saving
import bitesizereader.composeapp.generated.resources.collection_view_settings_title
import bitesizereader.composeapp.generated.resources.collection_view_shared
import bitesizereader.composeapp.generated.resources.collection_view_system_readonly
import bitesizereader.composeapp.generated.resources.collection_view_system_unshareable
import bitesizereader.composeapp.generated.resources.collection_view_tab_items
import bitesizereader.composeapp.generated.resources.collection_view_tab_settings
import bitesizereader.composeapp.generated.resources.collection_view_tab_sharing
import bitesizereader.composeapp.generated.resources.collection_view_user
import bitesizereader.composeapp.generated.resources.collections_cancel
import bitesizereader.composeapp.generated.resources.collections_description_placeholder
import bitesizereader.composeapp.generated.resources.collections_name_placeholder
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming")
@Composable
fun CollectionViewScreen(
    component: CollectionViewComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.viewModel.state.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        // Header with back button and collection info
        CollectionViewHeader(
            header = state.header,
            onBackClick = component::onBackClicked,
        )

        // Tab bar
        CollectionTabBar(
            selectedTab = state.selectedTab,
            onTabSelected = component.viewModel::selectTab,
            isSystemCollection = state.header.isSystemCollection,
        )

        // Tab content
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (state.selectedTab) {
                CollectionViewTab.Items ->
                    ItemsTabContent(
                        items = state.items,
                        onSummaryClick = component::onSummaryClicked,
                        onLoadMore = component.viewModel::loadMoreItems,
                    )
                CollectionViewTab.Settings ->
                    SettingsTabContent(
                        header = state.header,
                        settings = state.settings,
                        onUpdateCollection = component.viewModel::updateCollection,
                        onDeleteCollection = component::onDeleteConfirmed,
                    )
                CollectionViewTab.Sharing ->
                    SharingTabContent(
                        header = state.header,
                        sharing = state.sharing,
                        onRemoveCollaborator = component.viewModel::removeCollaborator,
                        onCreateInviteLink = component.viewModel::createInviteLink,
                    )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CollectionViewHeader(
    header: CollectionHeaderState,
    onBackClick: () -> Unit,
) {
    val collection = header.collection

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01),
    ) {
        // Top row with back button
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(Dimensions.detailHeaderHeight)
                    .padding(horizontal = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = CarbonIcons.ArrowLeft,
                    contentDescription = stringResource(Res.string.a11y_navigate_back),
                    tint = Carbon.theme.iconPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(Spacing.xs))

            Text(
                text = collection?.name ?: stringResource(Res.string.collection_view_default_title),
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }

        // Collection description and count
        if (collection != null) {
            CollectionInfoSection(collection = collection)
        }

        HorizontalDivider(color = Carbon.theme.borderSubtle00)
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CollectionInfoSection(collection: Collection) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md)
                .padding(bottom = Spacing.md),
    ) {
        val description = collection.description
        if (!description.isNullOrBlank()) {
            Text(
                text = description,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = stringResource(Res.string.collection_view_items_count, collection.count),
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )

            if (collection.isShared) {
                Text(
                    text = stringResource(Res.string.collection_view_shared),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.linkPrimary,
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CollectionTabBar(
    selectedTab: CollectionViewTab,
    onTabSelected: (CollectionViewTab) -> Unit,
    isSystemCollection: Boolean,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        TabItem(
            label = stringResource(Res.string.collection_view_tab_items),
            icon = CarbonIcons.Document,
            isSelected = selectedTab == CollectionViewTab.Items,
            onClick = { onTabSelected(CollectionViewTab.Items) },
        )

        TabItem(
            label = stringResource(Res.string.collection_view_tab_settings),
            icon = CarbonIcons.Settings,
            isSelected = selectedTab == CollectionViewTab.Settings,
            onClick = { onTabSelected(CollectionViewTab.Settings) },
            enabled = !isSystemCollection,
        )

        TabItem(
            label = stringResource(Res.string.collection_view_tab_sharing),
            icon = CarbonIcons.Share,
            isSelected = selectedTab == CollectionViewTab.Sharing,
            onClick = { onTabSelected(CollectionViewTab.Sharing) },
            enabled = !isSystemCollection,
        )
    }

    HorizontalDivider(color = Carbon.theme.borderSubtle00)
}

@Suppress("FunctionNaming")
@Composable
private fun TabItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val iconColor by animateColorAsState(
        targetValue =
            when {
                !enabled -> Carbon.theme.iconDisabled
                isSelected -> Carbon.theme.iconPrimary
                else -> Carbon.theme.iconSecondary
            },
        animationSpec = tween(durationMillis = 200),
        label = "iconColor",
    )
    val textColor by animateColorAsState(
        targetValue =
            when {
                !enabled -> Carbon.theme.textDisabled
                isSelected -> Carbon.theme.textPrimary
                else -> Carbon.theme.textSecondary
            },
        animationSpec = tween(durationMillis = 200),
        label = "textColor",
    )
    val indicatorColor by animateColorAsState(
        targetValue = if (isSelected && enabled) Carbon.theme.borderInteractive else Carbon.theme.layer01,
        animationSpec = tween(durationMillis = 200),
        label = "indicatorColor",
    )

    Column(
        modifier =
            Modifier
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xxs + 2.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = label,
                style = Carbon.typography.bodyCompact01,
                color = textColor,
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Bottom indicator
        Box(
            modifier =
                Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(
                        color = indicatorColor,
                        shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp),
                    ),
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ItemsTabContent(
    items: CollectionItemsState,
    onSummaryClick: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()
    val itemsError = items.error

    // Detect when scrolled to bottom for pagination
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= totalItems - 3
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && items.hasMore && !items.isLoading) {
                onLoadMore()
            }
        }
    }

    when {
        itemsError != null && items.items.isEmpty() ->
            ErrorView(
                message = itemsError,
                modifier = Modifier.fillMaxSize(),
            )

        items.items.isEmpty() && !items.isLoading ->
            EmptyStateView(
                title = stringResource(Res.string.collection_view_empty_items_title),
                message = stringResource(Res.string.collection_view_empty_items_message),
                modifier = Modifier.fillMaxSize(),
            )

        else -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(items = items.items, key = { it.id }) { summary ->
                    SummaryCard(
                        summary = summary,
                        onClick = { onSummaryClick(summary.id) },
                    )
                }

                if (items.isLoading) {
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.md),
                            contentAlignment = Alignment.Center,
                        ) {
                            Loading(modifier = Modifier.size(44.dp))
                        }
                    }
                }
            }
        }
    }

    // Initial loading overlay
    if (items.isLoading && items.items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Loading(modifier = Modifier.size(88.dp))
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SettingsTabContent(
    header: CollectionHeaderState,
    settings: CollectionSettingsState,
    onUpdateCollection: (String?, String?) -> Unit,
    onDeleteCollection: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editedName by remember(header.collection) { mutableStateOf(header.collection?.name ?: "") }
    var editedDescription by remember(header.collection) { mutableStateOf(header.collection?.description ?: "") }

    if (!header.canEdit) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.collection_view_system_readonly),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            Text(
                text = stringResource(Res.string.collection_view_settings_title),
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
            )
        }

        item {
            CollectionEditForm(
                editedName = editedName,
                onNameChange = { editedName = it },
                editedDescription = editedDescription,
                onDescriptionChange = { editedDescription = it },
                isUpdating = settings.isUpdating,
                onSave = {
                    onUpdateCollection(
                        editedName.takeIf { it != header.collection?.name },
                        editedDescription.takeIf { it != header.collection?.description },
                    )
                },
                updateError = settings.updateError,
            )
        }

        item {
            CollectionDangerZone(
                isDeleting = settings.isDeleting,
                deleteError = settings.deleteError,
                onShowDeleteDialog = { showDeleteDialog = true },
            )
        }
    }

    if (showDeleteDialog) {
        DeleteCollectionDialog(
            onConfirm = {
                showDeleteDialog = false
                onDeleteCollection()
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

@Suppress("FunctionNaming", "LongParameterList")
@Composable
private fun CollectionEditForm(
    editedName: String,
    onNameChange: (String) -> Unit,
    editedDescription: String,
    onDescriptionChange: (String) -> Unit,
    isUpdating: Boolean,
    onSave: () -> Unit,
    updateError: String?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = stringResource(Res.string.collection_view_name),
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
            TextInput(
                label = stringResource(Res.string.collection_view_name),
                value = editedName,
                onValueChange = onNameChange,
                placeholderText = stringResource(Res.string.collections_name_placeholder),
                state = if (isUpdating) TextInputState.Disabled else TextInputState.Enabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = stringResource(Res.string.collection_view_description),
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
            CarbonTextArea(
                label = stringResource(Res.string.collection_view_description),
                value = editedDescription,
                onValueChange = onDescriptionChange,
                placeholderText = stringResource(Res.string.collections_description_placeholder),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating,
            )
        }

        Button(
            label =
                if (isUpdating) {
                    stringResource(Res.string.collection_view_saving)
                } else {
                    stringResource(Res.string.collection_view_save_changes)
                },
            onClick = onSave,
            buttonType = ButtonType.Primary,
            isEnabled = !isUpdating,
            modifier = Modifier.fillMaxWidth(),
        )

        if (updateError != null) {
            Text(
                text = updateError,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.supportError,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CollectionDangerZone(
    isDeleting: Boolean,
    deleteError: String?,
    onShowDeleteDialog: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        HorizontalDivider(
            color = Carbon.theme.borderSubtle00,
            modifier = Modifier.padding(vertical = Spacing.xs),
        )

        Text(
            text = stringResource(Res.string.collection_view_danger_zone),
            style = Carbon.typography.heading03,
            color = Carbon.theme.supportError,
        )

        Button(
            label =
                if (isDeleting) {
                    stringResource(Res.string.collection_view_deleting)
                } else {
                    stringResource(Res.string.collection_view_delete_collection)
                },
            onClick = onShowDeleteDialog,
            buttonType = ButtonType.PrimaryDanger,
            isEnabled = !isDeleting,
            modifier = Modifier.fillMaxWidth(),
        )

        if (deleteError != null) {
            Text(
                text = deleteError,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.supportError,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun DeleteCollectionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    CarbonDialog(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.collection_view_delete_dialog_title),
        confirmButton = {
            Button(
                label = stringResource(Res.string.collection_view_delete_action),
                onClick = onConfirm,
                buttonType = ButtonType.PrimaryDanger,
            )
        },
        dismissButton = {
            Button(
                label = stringResource(Res.string.collections_cancel),
                onClick = onDismiss,
                buttonType = ButtonType.Ghost,
            )
        },
    ) {
        Text(
            text = stringResource(Res.string.collection_view_delete_dialog_message),
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SharingTabContent(
    header: CollectionHeaderState,
    sharing: CollectionSharingState,
    onRemoveCollaborator: (Int) -> Unit,
    onCreateInviteLink: (CollaboratorRole) -> Unit,
) {
    if (!header.canShare) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.collection_view_system_unshareable),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            CollaboratorsList(
                isLoading = sharing.isLoading,
                collaborators = sharing.collaborators,
                onRemoveCollaborator = onRemoveCollaborator,
                error = sharing.error,
            )
        }

        item {
            InviteLinkSection(
                isCreatingInvite = sharing.isCreatingInvite,
                inviteLink = sharing.inviteLink,
                inviteError = sharing.inviteError,
                onCreateInviteLink = onCreateInviteLink,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CollaboratorsList(
    isLoading: Boolean,
    collaborators: List<CollectionAcl>,
    onRemoveCollaborator: (Int) -> Unit,
    error: String?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(
            text = stringResource(Res.string.collection_view_collaborators),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Loading(modifier = Modifier.size(44.dp))
                }
            }
            collaborators.isEmpty() -> {
                Text(
                    text = stringResource(Res.string.collection_view_no_collaborators),
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
            }
            else -> {
                collaborators.forEach { collaborator ->
                    CollaboratorRow(
                        collaborator = collaborator,
                        onRemove = onRemoveCollaborator,
                    )
                }
            }
        }

        if (error != null) {
            Text(
                text = error,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.supportError,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CollaboratorRow(
    collaborator: CollectionAcl,
    onRemove: (Int) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Carbon.theme.layer01,
                    RoundedCornerShape(Dimensions.cardCornerRadius),
                )
                .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.collection_view_user, collaborator.userId ?: 0),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
            )
            Text(
                text = collaborator.role.name,
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }

        if (collaborator.role != CollaboratorRole.Owner && collaborator.userId != null) {
            IconButton(onClick = { onRemove(collaborator.userId!!) }) {
                Icon(
                    imageVector = CarbonIcons.Close,
                    contentDescription = stringResource(Res.string.collection_view_remove),
                    tint = Carbon.theme.iconSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun InviteLinkSection(
    isCreatingInvite: Boolean,
    inviteLink: CollectionInvite?,
    inviteError: String?,
    onCreateInviteLink: (CollaboratorRole) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        HorizontalDivider(
            color = Carbon.theme.borderSubtle00,
            modifier = Modifier.padding(vertical = Spacing.xs),
        )

        Text(
            text = stringResource(Res.string.collection_view_invite_link),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )

        Button(
            label =
                if (isCreatingInvite) {
                    stringResource(Res.string.collection_view_creating_invite)
                } else {
                    stringResource(Res.string.collection_view_create_invite_viewer)
                },
            onClick = { onCreateInviteLink(CollaboratorRole.Viewer) },
            buttonType = ButtonType.Secondary,
            isEnabled = !isCreatingInvite,
            modifier = Modifier.fillMaxWidth(),
        )

        if (inviteLink != null) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Carbon.theme.layer01,
                            RoundedCornerShape(Dimensions.cardCornerRadius),
                        )
                        .padding(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                Text(
                    text = stringResource(Res.string.collection_view_invite_token),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
                Text(
                    text = inviteLink.token,
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textPrimary,
                )
                val expiresAt = inviteLink.expiresAt
                if (expiresAt != null) {
                    Text(
                        text = stringResource(Res.string.collection_view_expires, expiresAt),
                        style = Carbon.typography.label01,
                        color = Carbon.theme.textSecondary,
                    )
                }
            }
        }

        if (inviteError != null) {
            Text(
                text = inviteError,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.supportError,
            )
        }
    }
}
