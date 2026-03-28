package com.po4yka.bitesizereader.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.collection_view_delete_action
import bitesizereader.composeapp.generated.resources.summary_card_already_read
import bitesizereader.composeapp.generated.resources.summary_card_mark_read
import bitesizereader.composeapp.generated.resources.summary_card_more_options
import bitesizereader.composeapp.generated.resources.summary_detail_add_to_collection
import bitesizereader.composeapp.generated.resources.summary_detail_favorite
import bitesizereader.composeapp.generated.resources.summary_detail_unfavorite
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import org.jetbrains.compose.resources.stringResource

@Composable
fun SummaryOverflowMenu(
    summary: Summary,
    onFavoriteClick: () -> Unit,
    onMarkReadClick: () -> Unit,
    onAddToCollectionClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    CarbonOverflowMenuButton(
        contentDescription = stringResource(Res.string.summary_card_more_options),
        onClick = { expanded = true },
    )

    CarbonMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        CarbonMenuItem(
            label =
                if (summary.isFavorited) {
                    stringResource(Res.string.summary_detail_unfavorite)
                } else {
                    stringResource(Res.string.summary_detail_favorite)
                },
            onClick = {
                expanded = false
                onFavoriteClick()
            },
            leadingIcon = if (summary.isFavorited) CarbonIcons.FavoriteFilled else CarbonIcons.Favorite,
            leadingIconTint =
                if (summary.isFavorited) {
                    Carbon.theme.supportError
                } else {
                    Carbon.theme.iconSecondary
                },
        )
        CarbonMenuItem(
            label =
                if (summary.isRead) {
                    stringResource(Res.string.summary_card_already_read)
                } else {
                    stringResource(Res.string.summary_card_mark_read)
                },
            onClick = {
                expanded = false
                if (!summary.isRead) {
                    onMarkReadClick()
                }
            },
            enabled = !summary.isRead,
            leadingIcon = CarbonIcons.CheckmarkFilled,
        )
        CarbonMenuItem(
            label = stringResource(Res.string.summary_detail_add_to_collection),
            onClick = {
                expanded = false
                onAddToCollectionClick()
            },
            leadingIcon = CarbonIcons.Folder,
        )
        CarbonMenuItem(
            label = stringResource(Res.string.collection_view_delete_action),
            onClick = {
                expanded = false
                onDeleteClick()
            },
            leadingIcon = CarbonIcons.TrashCan,
            isDestructive = true,
        )
    }
}
