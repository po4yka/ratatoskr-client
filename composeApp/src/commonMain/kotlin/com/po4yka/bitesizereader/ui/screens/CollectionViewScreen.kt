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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.po4yka.bitesizereader.domain.model.CollaboratorRole
import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.presentation.navigation.CollectionViewComponent
import com.po4yka.bitesizereader.presentation.state.CollectionViewState
import com.po4yka.bitesizereader.presentation.state.CollectionViewTab
import com.po4yka.bitesizereader.ui.components.EmptyStateView
import com.po4yka.bitesizereader.ui.components.ErrorView
import com.po4yka.bitesizereader.ui.components.SummaryCard
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.Spacing

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
            state = state,
            onBackClick = component::onBackClicked,
        )

        // Tab bar
        CollectionTabBar(
            selectedTab = state.selectedTab,
            onTabSelected = component.viewModel::selectTab,
            isSystemCollection = state.isSystemCollection,
        )

        // Tab content
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (state.selectedTab) {
                CollectionViewTab.Items ->
                    ItemsTabContent(
                        state = state,
                        onSummaryClick = component::onSummaryClicked,
                        onLoadMore = component.viewModel::loadMoreItems,
                    )
                CollectionViewTab.Settings ->
                    SettingsTabContent(
                        state = state,
                        onUpdateCollection = component.viewModel::updateCollection,
                        onDeleteCollection = component::onDeleteConfirmed,
                    )
                CollectionViewTab.Sharing ->
                    SharingTabContent(
                        state = state,
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
    state: CollectionViewState,
    onBackClick: () -> Unit,
) {
    val collection = state.collection

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
                    contentDescription = "Back",
                    tint = Carbon.theme.iconPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(Spacing.xs))

            Text(
                text = collection?.name ?: "Collection",
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
                text = "${collection.count} items",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )

            if (collection.isShared) {
                Text(
                    text = "Shared",
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
            label = "Items",
            icon = CarbonIcons.Document,
            isSelected = selectedTab == CollectionViewTab.Items,
            onClick = { onTabSelected(CollectionViewTab.Items) },
        )

        TabItem(
            label = "Settings",
            icon = CarbonIcons.Settings,
            isSelected = selectedTab == CollectionViewTab.Settings,
            onClick = { onTabSelected(CollectionViewTab.Settings) },
            enabled = !isSystemCollection,
        )

        TabItem(
            label = "Sharing",
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
    state: CollectionViewState,
    onSummaryClick: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()
    val itemsError = state.itemsError

    // Detect when scrolled to bottom for pagination
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= totalItems - 3
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && state.hasMoreItems && !state.isLoadingItems) {
                onLoadMore()
            }
        }
    }

    when {
        itemsError != null && state.items.isEmpty() ->
            ErrorView(
                message = itemsError,
                modifier = Modifier.fillMaxSize(),
            )

        state.items.isEmpty() && !state.isLoadingItems ->
            EmptyStateView(
                title = "No items yet",
                message = "Add articles to this collection to see them here",
                modifier = Modifier.fillMaxSize(),
            )

        else -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(items = state.items, key = { it.id }) { summary ->
                    SummaryCard(
                        summary = summary,
                        onClick = { onSummaryClick(summary.id) },
                    )
                }

                if (state.isLoadingItems) {
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
    if (state.isLoadingItems && state.items.isEmpty()) {
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
    state: CollectionViewState,
    onUpdateCollection: (String?, String?) -> Unit,
    onDeleteCollection: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editedName by remember(state.collection) { mutableStateOf(state.collection?.name ?: "") }
    var editedDescription by remember(state.collection) { mutableStateOf(state.collection?.description ?: "") }

    val updateError = state.updateError
    val deleteError = state.deleteError

    if (!state.canEdit) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "System collections cannot be edited",
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
                text = "Collection Settings",
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "Name",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    placeholder = { Text("Collection name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "Description",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
                OutlinedTextField(
                    value = editedDescription,
                    onValueChange = { editedDescription = it },
                    placeholder = { Text("Optional description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                )
            }
        }

        item {
            Button(
                label = if (state.isUpdating) "Saving..." else "Save Changes",
                onClick = {
                    onUpdateCollection(
                        editedName.takeIf { it != state.collection?.name },
                        editedDescription.takeIf { it != state.collection?.description },
                    )
                },
                buttonType = ButtonType.Primary,
                isEnabled = !state.isUpdating,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (updateError != null) {
            item {
                Text(
                    text = updateError,
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.supportError,
                )
            }
        }

        item {
            HorizontalDivider(
                color = Carbon.theme.borderSubtle00,
                modifier = Modifier.padding(vertical = Spacing.xs),
            )
        }

        item {
            Text(
                text = "Danger Zone",
                style = Carbon.typography.heading03,
                color = Carbon.theme.supportError,
            )
        }

        item {
            Button(
                label = if (state.isDeleting) "Deleting..." else "Delete Collection",
                onClick = { showDeleteDialog = true },
                buttonType = ButtonType.PrimaryDanger,
                isEnabled = !state.isDeleting,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (deleteError != null) {
            item {
                Text(
                    text = deleteError,
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.supportError,
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Collection?",
                    style = Carbon.typography.heading03,
                    color = Carbon.theme.textPrimary,
                )
            },
            text = {
                Text(
                    text = "This action cannot be undone. All items will be removed from this collection.",
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteCollection()
                    },
                ) {
                    Text(
                        text = "Delete",
                        color = Carbon.theme.supportError,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        text = "Cancel",
                        color = Carbon.theme.textPrimary,
                    )
                }
            },
            containerColor = Carbon.theme.layer01,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SharingTabContent(
    state: CollectionViewState,
    onRemoveCollaborator: (Int) -> Unit,
    onCreateInviteLink: (CollaboratorRole) -> Unit,
) {
    val collaboratorsError = state.collaboratorsError
    val inviteLink = state.inviteLink
    val inviteError = state.inviteError

    if (!state.canShare) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "System collections cannot be shared",
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
                text = "Collaborators",
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
            )
        }

        if (state.isLoadingCollaborators) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Loading(modifier = Modifier.size(44.dp))
                }
            }
        } else if (state.collaborators.isEmpty()) {
            item {
                Text(
                    text = "No collaborators yet",
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
            }
        } else {
            items(items = state.collaborators, key = { it.userId ?: it.hashCode() }) { collaborator ->
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
                            text = "User #${collaborator.userId}",
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
                        IconButton(onClick = { onRemoveCollaborator(collaborator.userId!!) }) {
                            Icon(
                                imageVector = CarbonIcons.Close,
                                contentDescription = "Remove",
                                tint = Carbon.theme.iconSecondary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
        }

        if (collaboratorsError != null) {
            item {
                Text(
                    text = collaboratorsError,
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.supportError,
                )
            }
        }

        item {
            HorizontalDivider(
                color = Carbon.theme.borderSubtle00,
                modifier = Modifier.padding(vertical = Spacing.xs),
            )
        }

        item {
            Text(
                text = "Invite Link",
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
            )
        }

        item {
            Button(
                label = if (state.isCreatingInvite) "Creating..." else "Create Invite Link (Viewer)",
                onClick = { onCreateInviteLink(CollaboratorRole.Viewer) },
                buttonType = ButtonType.Secondary,
                isEnabled = !state.isCreatingInvite,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (inviteLink != null) {
            item {
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
                        text = "Invite Token:",
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
                            text = "Expires: $expiresAt",
                            style = Carbon.typography.label01,
                            color = Carbon.theme.textSecondary,
                        )
                    }
                }
            }
        }

        if (inviteError != null) {
            item {
                Text(
                    text = inviteError,
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.supportError,
                )
            }
        }
    }
}
