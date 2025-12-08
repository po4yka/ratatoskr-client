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
import coil3.compose.AsyncImage

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
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image (if available)
            if (!summary.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = summary.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 12.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = summary.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Source and Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Favicon placeholder or generic icon could go here
                    Text(
                        text = extractDomain(summary.sourceUrl) ?: "Saved Article",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                         modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (summary.isRead) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Read",
                            tint = ReadIndicator,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

             // More Options Icon (Placeholder for future)
             IconButton(onClick = { /* TODO: More options */ }) {
                 Text("···", style = MaterialTheme.typography.titleLarge)
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
