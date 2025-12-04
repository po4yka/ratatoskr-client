@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.ui.theme.ReadIndicator
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Card component for displaying a summary in a list
 */
@Composable
fun SummaryCard(
    summary: Summary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            // Header: Title and Read Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = summary.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (summary.isRead) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Read",
                        tint = ReadIndicator,
                        modifier =
                            Modifier
                                .size(20.dp)
                                .padding(start = 8.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // TL;DR
            Text(
                text = summary.content.take(200) + if (summary.content.length > 200) "…" else "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Topic Tags
            if (summary.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    summary.tags.take(3).forEach { tag ->
                        TagChip(tag = tag)
                    }
                    if (summary.tags.size > 3) {
                        TagChip(tag = "+${summary.tags.size - 3}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Source and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = extractDomain(summary.sourceUrl) ?: "Unknown source",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = formatDate(summary.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun formatDate(instant: kotlin.time.Instant): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return formatter.format(instant.toJavaInstant().atZone(ZoneId.systemDefault()).toLocalDate())
}

private fun extractDomain(url: String): String? {
    val noProtocol = url.substringAfter("://", url)
    return noProtocol.substringBefore("/").ifBlank { null }
}
