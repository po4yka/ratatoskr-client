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
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
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
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostDialog
import com.po4yka.ratatoskr.core.ui.components.frost.BracketIconButton
import com.po4yka.ratatoskr.core.ui.components.frost.FrostSpinner
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
import com.po4yka.ratatoskr.core.ui.components.TextArea
import com.po4yka.ratatoskr.core.ui.components.EmptyStateView
import com.po4yka.ratatoskr.core.ui.components.ErrorView
import com.po4yka.ratatoskr.core.ui.components.SummaryCard
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
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
                .background(AppTheme.frostColors.page),
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
                .background(AppTheme.frostColors.page),
    ) {
        // Top row with back button
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = AppTheme.spacing.cell),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BracketIconButton(
                onClick = onBackClick,
                contentDescription = stringResource(Res.string.a11y_navigate_back),
            ) {
                FrostIcon(
                    imageVector = AppIcons.ArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.size(IconSizes.md),
                )
            }

            Spacer(modifier = Modifier.width(AppTheme.spacing.cell))

            FrostText(
                text = collection?.name ?: stringResource(Res.string.collection_view_default_title),
                style = AppTheme.frostType.monoEmph,
                color = AppTheme.frostColors.ink,
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
                .padding(horizontal = AppTheme.spacing.line)
                .padding(bottom = AppTheme.spacing.line),
    ) {
        val description = collection.description
        if (!description.isNullOrBlank()) {
            FrostText(
                text = description,
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.cell))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
        ) {
            FrostText(
                text = stringResource(Res.string.collection_view_items_count, collection.count),
                style = AppTheme.frostType.monoXs,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
            )

            if (collection.isShared) {
                FrostText(
                    text = stringResource(Res.string.collection_view_shared),
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.ink,
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
                .background(AppTheme.frostColors.page),
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
                !enabled -> AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.inactive)
                isSelected -> AppTheme.frostColors.ink
                else -> AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary)
            },
        animationSpec = tween(durationMillis = 200),
        label = "iconColor",
    )
    val textColor by animateColorAsState(
        targetValue =
            when {
                !enabled -> AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.inactive)
                isSelected -> AppTheme.frostColors.ink
                else -> AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary)
            },
        animationSpec = tween(durationMillis = 200),
        label = "textColor",
    )
    val indicatorColor by animateColorAsState(
        targetValue = if (isSelected && enabled) AppTheme.frostColors.ink else AppTheme.frostColors.page,
        animationSpec = tween(durationMillis = 200),
        label = "indicatorColor",
    )

    Column(
        modifier =
            Modifier
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 24.dp, vertical = AppTheme.spacing.cell),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gapInline + 2.dp),
        ) {
            FrostIcon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp),
            )
            FrostText(
                text = label,
                style = AppTheme.frostType.monoBody,
                color = textColor,
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.cell))

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
                contentPadding = PaddingValues(AppTheme.spacing.line),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
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
                                    .padding(AppTheme.spacing.line),
                            contentAlignment = Alignment.Center,
                        ) {
                            FrostSpinner(modifier = Modifier.size(44.dp), size = 44.dp)
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
            FrostSpinner(modifier = Modifier.size(88.dp), size = 88.dp)
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
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppTheme.spacing.line),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.line),
    ) {
        item {
            FrostText(
                text = stringResource(Res.string.collection_view_settings_title),
                style = AppTheme.frostType.monoEmph,
                color = AppTheme.frostColors.ink,
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
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.line)) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell)) {
            FrostText(
                text = stringResource(Res.string.collection_view_name),
                style = AppTheme.frostType.monoXs,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
            )
            BracketField(
                value = editedName,
                onValueChange = onNameChange,
                label = stringResource(Res.string.collection_view_name),
                enabled = !isUpdating,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell)) {
            FrostText(
                text = stringResource(Res.string.collection_view_description),
                style = AppTheme.frostType.monoXs,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
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
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.spark,
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
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.line)) {
        FrostDivider(modifier = Modifier.padding(vertical = AppTheme.spacing.cell))

        FrostText(
            text = stringResource(Res.string.collection_view_danger_zone),
            style = AppTheme.frostType.monoEmph,
            color = AppTheme.frostColors.spark,
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
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.spark,
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
    FrostDialog(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.collection_view_delete_dialog_title),
        actions = {
            BracketButton(
                label = stringResource(Res.string.collections_cancel),
                onClick = onDismiss,
            )
            BracketButton(
                label = stringResource(Res.string.collection_view_delete_action),
                onClick = onConfirm,
                variant = BracketButtonVariant.Critical,
            )
        },
    ) {
        FrostText(
            text = stringResource(Res.string.collection_view_delete_dialog_message),
            style = AppTheme.frostType.monoBody,
            color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
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
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppTheme.spacing.line),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.line),
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
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.line)) {
        FrostText(
            text = stringResource(Res.string.collection_view_collaborators),
            style = AppTheme.frostType.monoEmph,
            color = AppTheme.frostColors.ink,
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    FrostSpinner(modifier = Modifier.size(44.dp), size = 44.dp)
                }
            }
            collaborators.isEmpty() -> {
                FrostText(
                    text = stringResource(Res.string.collection_view_no_collaborators),
                    style = AppTheme.frostType.monoBody,
                    color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
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
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.spark,
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
                    AppTheme.frostColors.page,
                    RectangleShape,
                )
                .padding(AppTheme.spacing.cell),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            FrostText(
                text = stringResource(Res.string.collection_view_user, collaborator.userId ?: 0),
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink,
            )
            FrostText(
                text = collaboratorRoleLabel(collaborator.role),
                style = AppTheme.frostType.monoXs,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
            )
        }

        if (collaborator.role != CollaboratorRole.Owner && collaborator.userId != null) {
            BracketIconButton(
                onClick = { onRemove(collaborator.userId!!) },
                contentDescription = stringResource(Res.string.collection_view_remove),
            ) {
                FrostIcon(
                    imageVector = AppIcons.Close,
                    contentDescription = null,
                    tint = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
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
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.line)) {
        FrostDivider(modifier = Modifier.padding(vertical = AppTheme.spacing.cell))

        FrostText(
            text = stringResource(Res.string.collection_view_invite_link),
            style = AppTheme.frostType.monoEmph,
            color = AppTheme.frostColors.ink,
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
                            AppTheme.frostColors.page,
                            RectangleShape,
                        )
                        .padding(AppTheme.spacing.cell),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.gapInline),
            ) {
                FrostText(
                    text = stringResource(Res.string.collection_view_invite_token),
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                )
                FrostText(
                    text = inviteLink.token,
                    style = AppTheme.frostType.monoBody,
                    color = AppTheme.frostColors.ink,
                )
                val expiresAt = inviteLink.expiresAt
                if (expiresAt != null) {
                    FrostText(
                        text = stringResource(Res.string.collection_view_expires, expiresAt),
                        style = AppTheme.frostType.monoXs,
                        color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                    )
                }
            }
        }

        if (inviteError != null) {
            FrostText(
                text = inviteError,
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.spark,
            )
        }
    }
}
