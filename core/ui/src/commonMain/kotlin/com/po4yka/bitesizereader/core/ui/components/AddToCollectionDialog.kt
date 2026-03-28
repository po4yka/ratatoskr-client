package com.po4yka.bitesizereader.core.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.core.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.core.ui.theme.IconSizes
import com.po4yka.bitesizereader.core.ui.theme.Spacing
import bitesizereader.core.ui.generated.resources.Res
import bitesizereader.core.ui.generated.resources.add_to_collection_adding
import bitesizereader.core.ui.generated.resources.add_to_collection_empty
import bitesizereader.core.ui.generated.resources.add_to_collection_title
import bitesizereader.core.ui.generated.resources.collections_cancel
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
    CarbonDialog(
        onDismissRequest = { if (!isAdding) onDismiss() },
        title = stringResource(Res.string.add_to_collection_title),
        dismissButton = {
            Button(
                label = stringResource(Res.string.collections_cancel),
                onClick = onDismiss,
                isEnabled = !isAdding,
                buttonType = ButtonType.Ghost,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    SmallLoading()
                }
            } else if (collections.isEmpty()) {
                Text(
                    text = stringResource(Res.string.add_to_collection_empty),
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
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
                    SmallLoading()
                    Text(
                        text = stringResource(Res.string.add_to_collection_adding),
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textSecondary,
                    )
                }
            }

            error?.let {
                Text(
                    text = it,
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
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
                .background(Carbon.theme.layer02)
                .clickable(enabled = isEnabled, onClick = onClick)
                .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = CarbonIcons.Folder,
            contentDescription = null,
            tint = Carbon.theme.iconPrimary,
            modifier = Modifier.size(IconSizes.sm),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = collection.name,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
            )
            collection.description?.let {
                Text(
                    text = it,
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                    maxLines = 1,
                )
            }
        }
        Text(
            text = collection.count.toString(),
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )
    }
}
