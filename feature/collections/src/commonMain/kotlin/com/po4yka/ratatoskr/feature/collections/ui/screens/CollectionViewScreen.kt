package com.po4yka.ratatoskr.feature.collections.ui.screens

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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.Icon
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
import com.po4yka.ratatoskr.core.ui.components.frost.BracketField
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostDivider
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButtonVariant
import com.po4yka.ratatoskr.core.ui.components.AppSpinner
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.CollaboratorRole
import com.po4yka.ratatoskr.domain.model.Collection
import com.po4yka.ratatoskr.domain.model.CollectionAcl
import com.po4yka.ratatoskr.domain.model.CollectionInvite
import com.po4yka.ratatoskr.presentation.navigation.CollectionViewComponent
import com.po4yka.ratatoskr.presentation.state.CollectionHeaderState
import com.po4yka.ratatoskr.presentation.state.CollectionItemsState
import com.po4yka.ratatoskr.presentation.state.CollectionSettingsState
import com.po4yka.ratatoskr.presentation.state.CollectionSharingState
import com.po4yka.ratatoskr.presentation.state.CollectionViewTab
import com.po4yka.ratatoskr.core.ui.components.AppDialog
import com.po4yka.ratatoskr.core.ui.components.AppIconButton
import com.po4yka.ratatoskr.core.ui.components.TextArea
import com.po4yka.ratatoskr.core.ui.components.EmptyStateView
import com.po4yka.ratatoskr.core.ui.components.ErrorView
import com.po4yka.ratatoskr.core.ui.components.SummaryCard
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.a11y_navigate_back
import ratatoskr.core.ui.generated.resources.collection_view_collaborators
import ratatoskr.core.ui.generated.resources.collection_view_creating_invite
import ratatoskr.core.ui.generated.resources.collection_view_create_invite_viewer
import ratatoskr.core.ui.generated.resources.collection_view_danger_zone
import ratatoskr.core.ui.generated.resources.collection_view_default_title
import ratatoskr.core.ui.generated.resources.collection_view_delete_action
import ratatoskr.core.ui.generated.resources.collection_view_delete_collection
import ratatoskr.core.ui.generated.resources.collection_view_delete_dialog_message
import ratatoskr.core.ui.generated.resources.collection_view_delete_dialog_title
import ratatoskr.core.ui.generated.resources.collection_view_deleting
import ratatoskr.core.ui.generated.resources.collection_view_description
import ratatoskr.core.ui.generated.resources.collection_view_empty_items_message
import ratatoskr.core.ui.generated.resources.collection_view_empty_items_title
import ratatoskr.core.ui.generated.resources.collection_view_expires
import ratatoskr.core.ui.generated.resources.collection_view_invite_link
import ratatoskr.core.ui.generated.resources.collection_view_invite_token
import ratatoskr.core.ui.generated.resources.collection_view_items_count
import ratatoskr.core.ui.generated.resources.collection_view_name
import ratatoskr.core.ui.generated.resources.collection_view_no_collaborators
import ratatoskr.core.ui.generated.resources.collection_view_remove
import ratatoskr.core.ui.generated.resources.collection_view_role_editor
import ratatoskr.core.ui.generated.resources.collection_view_role_owner
import ratatoskr.core.ui.generated.resources.collection_view_role_viewer
import ratatoskr.core.ui.generated.resources.collection_view_save_changes
import ratatoskr.core.ui.generated.resources.collection_view_saving
import ratatoskr.core.ui.generated.resources.collection_view_settings_title
import ratatoskr.core.ui.generated.resources.collection_view_shared
import ratatoskr.core.ui.generated.resources.collection_view_system_readonly
import ratatoskr.core.ui.generated.resources.collection_view_system_unshareable
import ratatoskr.core.ui.generated.resources.collection_view_tab_items
import ratatoskr.core.ui.generated.resources.collection_view_tab_settings
import ratatoskr.core.ui.generated.resources.collection_view_tab_sharing
import ratatoskr.core.ui.generated.resources.collection_view_user
import ratatoskr.core.ui.generated.resources.collections_cancel
import ratatoskr.core.ui.generated.resources.collections_description_placeholder
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
                .background(AppTheme.colors.background),
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
                .background(AppTheme.colors.layer01),
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
            AppIconButton(
                imageVector = AppIcons.ArrowLeft,
                contentDescription = stringResource(Res.string.a11y_navigate_back),
                onClick = onBackClick,
                iconSize = IconSizes.md,
            )

            Spacer(modifier = Modifier.width(Spacing.xs))

            FrostText(
                text = collection?.name ?: stringResource(Res.string.collection_view_default_title),
                style = AppTheme.type.heading03,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }

        // Collection description and count
        if (collection != null) {
            CollectionInfoSection(collection = collection)
        }

        FrostDivider()
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
            FrostText(
                text = description,
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            FrostText(
                text = stringResource(Res.string.collection_view_items_count, collection.count),
                style = AppTheme.type.label01,
                color = AppTheme.colors.textSecondary,
            )

            if (collection.isShared) {
                FrostText(
                    text = stringResource(Res.string.collection_view_shared),
                    style = AppTheme.type.label01,
                    color = AppTheme.colors.linkPrimary,
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
                .background(AppTheme.colors.layer01),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        TabItem(
            label = stringResource(Res.string.collection_view_tab_items),
            icon = AppIcons.Document,
            isSelected = selectedTab == CollectionViewTab.Items,
            onClick = { onTabSelected(CollectionViewTab.Items) },
        )

        TabItem(
            label = stringResource(Res.string.collection_view_tab_settings),
            icon = AppIcons.Settings,
            isSelected = selectedTab == CollectionViewTab.Settings,
            onClick = { onTabSelected(CollectionViewTab.Settings) },
            enabled = !isSystemCollection,
        )

        TabItem(
            label = stringResource(Res.string.collection_view_tab_sharing),
            icon = AppIcons.Share,
            isSelected = selectedTab == CollectionViewTab.Sharing,
            onClick = { onTabSelected(CollectionViewTab.Sharing) },
            enabled = !isSystemCollection,
        )
    }

    FrostDivider()
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
                !enabled -> AppTheme.colors.iconDisabled
                isSelected -> AppTheme.colors.iconPrimary
                else -> AppTheme.colors.iconSecondary
            },
        animationSpec = tween(durationMillis = 200),
        label = "iconColor",
    )
    val textColor by animateColorAsState(
        targetValue =
            when {
                !enabled -> AppTheme.colors.textDisabled
                isSelected -> AppTheme.colors.textPrimary
                else -> AppTheme.colors.textSecondary
            },
        animationSpec = tween(durationMillis = 200),
        label = "textColor",
    )
    val indicatorColor by animateColorAsState(
        targetValue = if (isSelected && enabled) AppTheme.colors.borderInteractive else AppTheme.colors.layer01,
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
            FrostText(
                text = label,
                style = AppTheme.type.bodyCompact01,
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
                        shape = RectangleShape,
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
                            AppSpinner(modifier = Modifier.size(44.dp))
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
            AppSpinner(modifier = Modifier.size(88.dp))
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
            FrostText(
                text = stringResource(Res.string.collection_view_system_readonly),
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textSecondary,
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
            FrostText(
                text = stringResource(Res.string.collection_view_settings_title),
                style = AppTheme.type.heading03,
                color = AppTheme.colors.textPrimary,
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
            FrostText(
                text = stringResource(Res.string.collection_view_name),
                style = AppTheme.type.label01,
                color = AppTheme.colors.textSecondary,
            )
            BracketField(
                value = editedName,
                onValueChange = onNameChange,
                label = stringResource(Res.string.collection_view_name),
                enabled = !isUpdating,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            FrostText(
                text = stringResource(Res.string.collection_view_description),
                style = AppTheme.type.label01,
                color = AppTheme.colors.textSecondary,
            )
            TextArea(
                label = stringResource(Res.string.collection_view_description),
                value = editedDescription,
                onValueChange = onDescriptionChange,
                placeholderText = stringResource(Res.string.collections_description_placeholder),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating,
            )
        }

        BracketButton(
            label =
                if (isUpdating) {
                    stringResource(Res.string.collection_view_saving)
                } else {
                    stringResource(Res.string.collection_view_save_changes)
                },
            onClick = onSave,
            enabled = !isUpdating,
            modifier = Modifier.fillMaxWidth(),
        )

        if (updateError != null) {
            FrostText(
                text = updateError,
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.supportError,
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
        FrostDivider(modifier = Modifier.padding(vertical = Spacing.xs))

        FrostText(
            text = stringResource(Res.string.collection_view_danger_zone),
            style = AppTheme.type.heading03,
            color = AppTheme.colors.supportError,
        )

        BracketButton(
            label =
                if (isDeleting) {
                    stringResource(Res.string.collection_view_deleting)
                } else {
                    stringResource(Res.string.collection_view_delete_collection)
                },
            onClick = onShowDeleteDialog,
            enabled = !isDeleting,
            variant = BracketButtonVariant.Critical,
            modifier = Modifier.fillMaxWidth(),
        )

        if (deleteError != null) {
            FrostText(
                text = deleteError,
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.supportError,
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
    AppDialog(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.collection_view_delete_dialog_title),
        confirmButton = {
            BracketButton(
                label = stringResource(Res.string.collection_view_delete_action),
                onClick = onConfirm,
                variant = BracketButtonVariant.Critical,
            )
        },
        dismissButton = {
            BracketButton(
                label = stringResource(Res.string.collections_cancel),
                onClick = onDismiss,
            )
        },
    ) {
        FrostText(
            text = stringResource(Res.string.collection_view_delete_dialog_message),
            style = AppTheme.type.bodyCompact01,
            color = AppTheme.colors.textSecondary,
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
            FrostText(
                text = stringResource(Res.string.collection_view_system_unshareable),
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textSecondary,
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
        FrostText(
            text = stringResource(Res.string.collection_view_collaborators),
            style = AppTheme.type.heading03,
            color = AppTheme.colors.textPrimary,
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    AppSpinner(modifier = Modifier.size(44.dp))
                }
            }
            collaborators.isEmpty() -> {
                FrostText(
                    text = stringResource(Res.string.collection_view_no_collaborators),
                    style = AppTheme.type.bodyCompact01,
                    color = AppTheme.colors.textSecondary,
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
            FrostText(
                text = error,
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.supportError,
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
                    AppTheme.colors.layer01,
                    RectangleShape,
                )
                .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            FrostText(
                text = stringResource(Res.string.collection_view_user, collaborator.userId ?: 0),
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textPrimary,
            )
            FrostText(
                text = collaboratorRoleLabel(collaborator.role),
                style = AppTheme.type.label01,
                color = AppTheme.colors.textSecondary,
            )
        }

        if (collaborator.role != CollaboratorRole.Owner && collaborator.userId != null) {
            AppIconButton(
                imageVector = AppIcons.Close,
                contentDescription = stringResource(Res.string.collection_view_remove),
                onClick = { onRemove(collaborator.userId!!) },
                tint = AppTheme.colors.iconSecondary,
                buttonSize = Dimensions.compactIconButtonSize,
                iconSize = IconSizes.sm,
            )
        }
    }
}

@Composable
private fun collaboratorRoleLabel(role: CollaboratorRole): String =
    when (role) {
        CollaboratorRole.Owner -> stringResource(Res.string.collection_view_role_owner)
        CollaboratorRole.Editor -> stringResource(Res.string.collection_view_role_editor)
        CollaboratorRole.Viewer -> stringResource(Res.string.collection_view_role_viewer)
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
        FrostDivider(modifier = Modifier.padding(vertical = Spacing.xs))

        FrostText(
            text = stringResource(Res.string.collection_view_invite_link),
            style = AppTheme.type.heading03,
            color = AppTheme.colors.textPrimary,
        )

        BracketButton(
            label =
                if (isCreatingInvite) {
                    stringResource(Res.string.collection_view_creating_invite)
                } else {
                    stringResource(Res.string.collection_view_create_invite_viewer)
                },
            onClick = { onCreateInviteLink(CollaboratorRole.Viewer) },
            enabled = !isCreatingInvite,
            modifier = Modifier.fillMaxWidth(),
        )

        if (inviteLink != null) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            AppTheme.colors.layer01,
                            RectangleShape,
                        )
                        .padding(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                FrostText(
                    text = stringResource(Res.string.collection_view_invite_token),
                    style = AppTheme.type.label01,
                    color = AppTheme.colors.textSecondary,
                )
                FrostText(
                    text = inviteLink.token,
                    style = AppTheme.type.bodyCompact01,
                    color = AppTheme.colors.textPrimary,
                )
                val expiresAt = inviteLink.expiresAt
                if (expiresAt != null) {
                    FrostText(
                        text = stringResource(Res.string.collection_view_expires, expiresAt),
                        style = AppTheme.type.label01,
                        color = AppTheme.colors.textSecondary,
                    )
                }
            }
        }

        if (inviteError != null) {
            FrostText(
                text = inviteError,
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.supportError,
            )
        }
    }
}
