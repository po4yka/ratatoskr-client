package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.Collection
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.add_to_collection_adding
import ratatoskr.core.ui.generated.resources.add_to_collection_empty
import ratatoskr.core.ui.generated.resources.add_to_collection_title
import ratatoskr.core.ui.generated.resources.collections_cancel
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming")
@Composable
fun AddToCollectionDialog(
    collections: List<Collection>,
    isLoading: Boolean,
    isAdding: Boolean,
    error: String?,
    onCollectionSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AppDialog(
        onDismissRequest = { if (!isAdding) onDismiss() },
        title = stringResource(Res.string.add_to_collection_title),
        dismissButton = {
            BracketButton(
                label = stringResource(Res.string.collections_cancel),
                onClick = onDismiss,
                enabled = !isAdding,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    AppSmallSpinner()
                }
            } else if (collections.isEmpty()) {
                FrostText(
                    text = stringResource(Res.string.add_to_collection_empty),
                    style = AppTheme.frostType.monoBody,
                    color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    items(
                        items = collections,
                        key = { it.id },
                    ) { collection ->
                        CollectionSelectionRow(
                            collection = collection,
                            isEnabled = !isAdding,
                            onClick = { onCollectionSelected(collection.id) },
                        )
                    }
                }
            }

            if (isAdding) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    AppSmallSpinner()
                    FrostText(
                        text = stringResource(Res.string.add_to_collection_adding),
                        style = AppTheme.frostType.monoBody,
                        color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                    )
                }
            }

            error?.let {
                FrostText(
                    text = it,
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.spark,
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CollectionSelectionRow(
    collection: Collection,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(AppTheme.colors.layer02)
                .clickable(enabled = isEnabled, onClick = onClick)
                .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = AppIcons.Folder,
            contentDescription = null,
            tint = AppTheme.colors.iconPrimary,
            modifier = Modifier.size(IconSizes.sm),
        )
        Column(modifier = Modifier.weight(1f)) {
            FrostText(
                text = collection.name,
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.active),
            )
            collection.description?.let {
                FrostText(
                    text = it,
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                )
            }
        }
        FrostText(
            text = collection.count.toString(),
            style = AppTheme.frostType.monoXs,
            color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
        )
    }
}
