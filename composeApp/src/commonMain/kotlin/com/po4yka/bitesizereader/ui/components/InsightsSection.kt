package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.ui.theme.Spacing
import com.po4yka.bitesizereader.util.extractDomain

@Suppress("FunctionNaming")
@Composable
fun InsightsSection(
    insights: List<Summary>,
    onSummaryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (insights.isEmpty()) return

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01)
                .padding(vertical = Spacing.md),
    ) {
        Text(
            text = "Popular this month",
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
            modifier =
                Modifier
                    .padding(horizontal = Spacing.md)
                    .padding(bottom = Spacing.sm),
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            items(
                items = insights,
                key = { it.id },
            ) { summary ->
                InsightCard(
                    summary = summary,
                    onClick = { onSummaryClick(summary.id) },
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun InsightCard(
    summary: Summary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(200.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer02)
                .clickable(onClick = onClick)
                .padding(Spacing.sm),
    ) {
        if (summary.imageUrl != null) {
            ProxiedImage(
                imageUrl = summary.imageUrl!!,
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(4.dp)),
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
        }

        Text(
            text = summary.title,
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = extractDomain(summary.sourceUrl) ?: "Article",
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
