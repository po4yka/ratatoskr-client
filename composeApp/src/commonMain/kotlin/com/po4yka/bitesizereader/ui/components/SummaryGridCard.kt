package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.IconSizes

@Suppress("FunctionNaming")
@Composable
fun SummaryGridCard(
    summary: Summary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val readStatus = if (summary.isRead) "Read" else "Unread"
    val source = extractDomain(summary.sourceUrl)
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
        // Image
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Carbon.theme.layer02),
            contentAlignment = Alignment.Center,
        ) {
            if (!summary.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = summary.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    imageVector = CarbonIcons.Document,
                    contentDescription = null,
                    tint = Carbon.theme.iconSecondary,
                    modifier = Modifier.size(32.dp),
                )
            }

            // Read indicator
            if (summary.isRead) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
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
        }

        // Title
        Text(
            text = summary.title,
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        // Source
        Text(
            text = extractDomain(summary.sourceUrl),
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun extractDomain(url: String): String {
    return try {
        val withoutProtocol =
            url
                .removePrefix("https://")
                .removePrefix("http://")
                .removePrefix("www.")
        withoutProtocol.substringBefore("/")
    } catch (_: Exception) {
        "Saved Article"
    }
}
