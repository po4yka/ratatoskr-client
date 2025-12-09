package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import compose.icons.FeatherIcons
import compose.icons.feathericons.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.material3.Text
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.ui.theme.ReadIndicator

/**
 * Card component for displaying a summary in a list using Carbon Design System
 */
@Composable
fun SummaryCard(
    summary: Summary,
    onClick: () -> Unit,
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
        // Image (if available)
        if (!summary.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = summary.imageUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(4.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
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

            Spacer(modifier = Modifier.height(4.dp))

            // Source and Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = extractDomain(summary.sourceUrl) ?: "Saved Article",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (summary.isRead) {
                    Icon(
                        imageVector = FeatherIcons.CheckCircle,
                        contentDescription = "Read",
                        tint = ReadIndicator,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        // More Options Icon
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "...",
            style = Carbon.typography.heading03,
            color = Carbon.theme.textSecondary,
        )
    }
}

private fun extractDomain(url: String): String? {
    val noProtocol = url.substringAfter("://", url)
    return noProtocol.substringBefore("/").ifBlank { null }
}
