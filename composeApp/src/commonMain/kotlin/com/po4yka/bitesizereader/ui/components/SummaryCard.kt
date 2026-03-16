package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.util.extractDomain

/**
 * Card component for displaying a summary in a list using Carbon Design System
 */
@Suppress("FunctionNaming", "LongMethod", "LongParameterList")
@Composable
fun SummaryCard(
    summary: Summary,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit = {},
    onMarkReadClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onAddToCollectionClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .clickable(onClick = onClick)
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Thumbnail
        if (summary.imageUrl != null) {
            ProxiedImage(
                imageUrl = summary.imageUrl!!,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(4.dp)),
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            // Title
            Text(
                text = summary.title,
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (summary.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = summary.content.take(150).replace("\n", " "),
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Source and Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text =
                        buildString {
                            append(extractDomain(summary.sourceUrl) ?: "Saved Article")
                            summary.readingTimeMin?.let { append(" | $it min") }
                        },
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )

                if (summary.isFavorited) {
                    Icon(
                        imageVector = CarbonIcons.FavoriteFilled,
                        contentDescription = "Favorited",
                        tint = Carbon.theme.supportError,
                        modifier = Modifier.size(IconSizes.xs),
                    )
                }

                if (summary.isFullContentCached) {
                    Icon(
                        imageVector = CarbonIcons.Download,
                        contentDescription = "Available offline",
                        tint = Carbon.theme.iconSecondary,
                        modifier = Modifier.size(IconSizes.xs),
                    )
                }

                if (summary.isRead) {
                    Icon(
                        imageVector = CarbonIcons.CheckmarkFilled,
                        contentDescription = "Read",
                        tint = Carbon.theme.supportSuccess,
                        modifier = Modifier.size(IconSizes.xs),
                    )
                }
            }
        }

        // More Options Menu
        Spacer(modifier = Modifier.width(8.dp))

        var menuExpanded by remember { mutableStateOf(false) }

        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = CarbonIcons.OverflowMenuVertical,
                    contentDescription = "More options",
                    tint = Carbon.theme.iconSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(if (summary.isFavorited) "Unfavorite" else "Favorite") },
                    onClick = {
                        menuExpanded = false
                        onFavoriteClick()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector =
                                if (summary.isFavorited) {
                                    CarbonIcons.FavoriteFilled
                                } else {
                                    CarbonIcons.Favorite
                                },
                            contentDescription = null,
                            tint =
                                if (summary.isFavorited) {
                                    Carbon.theme.supportError
                                } else {
                                    Carbon.theme.iconSecondary
                                },
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    },
                )
                DropdownMenuItem(
                    text = { Text(if (summary.isRead) "Already read" else "Mark as read") },
                    onClick = {
                        menuExpanded = false
                        if (!summary.isRead) onMarkReadClick()
                    },
                    enabled = !summary.isRead,
                    leadingIcon = {
                        Icon(
                            imageVector = CarbonIcons.CheckmarkFilled,
                            contentDescription = null,
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    },
                )
                DropdownMenuItem(
                    text = { Text("Add to collection") },
                    onClick = {
                        menuExpanded = false
                        onAddToCollectionClick()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = CarbonIcons.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    },
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = Carbon.theme.supportError) },
                    onClick = {
                        menuExpanded = false
                        onDeleteClick()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = CarbonIcons.TrashCan,
                            contentDescription = null,
                            tint = Carbon.theme.supportError,
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    },
                )
            }
        }
    }
}
