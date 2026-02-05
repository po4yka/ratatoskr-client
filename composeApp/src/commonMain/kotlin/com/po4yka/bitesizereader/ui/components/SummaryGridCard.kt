package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.util.extractDomain

@Suppress("FunctionNaming", "LongMethod")
@Composable
fun SummaryGridCard(
    summary: Summary,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit = {},
    onMarkReadClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val readStatus = if (summary.isRead) "Read" else "Unread"
    val source = extractDomain(summary.sourceUrl) ?: "Saved Article"
    val cardDescription = "${summary.title}. $readStatus article from $source"

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .clickable(onClick = onClick)
                .padding(8.dp)
                .semantics { contentDescription = cardDescription },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Title + overflow menu row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = summary.title,
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

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

        // Read indicator
        if (summary.isRead) {
            Box(
                modifier =
                    Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Carbon.theme.supportSuccess),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = CarbonIcons.Checkmark,
                    contentDescription = "Read",
                    tint = Carbon.theme.textOnColor,
                    modifier = Modifier.size(IconSizes.xs),
                )
            }
        }

        // Source
        Text(
            text = source,
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
