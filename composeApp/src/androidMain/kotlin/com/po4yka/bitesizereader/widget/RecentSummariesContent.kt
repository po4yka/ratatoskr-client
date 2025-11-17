package com.po4yka.bitesizereader.widget

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.po4yka.bitesizereader.MainActivity
import com.po4yka.bitesizereader.domain.model.Summary

/**
 * Widget content showing recent summaries.
 *
 * Displays a list of recent summaries with title, TLDR preview, and reading time.
 * Each item is clickable and opens the app to the corresponding summary.
 */
@Composable
fun RecentSummariesContent(summaries: List<Summary>) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Widget header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📚 Recent Summaries",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onBackground
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Summaries list or empty state
        if (summaries.isEmpty()) {
            EmptyState()
        } else {
            summaries.forEach { summary ->
                SummaryItem(summary)
                Spacer(modifier = GlanceModifier.height(8.dp))
            }
        }
    }
}

/**
 * Empty state when no summaries are available.
 */
@Composable
private fun EmptyState() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "No summaries yet",
            style = TextStyle(
                fontSize = 16.sp,
                color = GlanceTheme.colors.onBackground
            )
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(
            text = "Submit a URL to get started",
            style = TextStyle(
                fontSize = 14.sp,
                color = GlanceTheme.colors.onSurfaceVariant
            )
        )
    }
}

/**
 * Individual summary item in the widget.
 */
@Composable
private fun SummaryItem(summary: Summary) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.surface)
            .cornerRadius(8.dp)
            .padding(12.dp)
            .clickable(
                onClick = actionStartActivity<MainActivity>(
                    parameters = actionParametersOf(
                        "summaryId" to summary.id.toString()
                    )
                )
            )
    ) {
        // Title
        Text(
            text = summary.title,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onSurface
            ),
            maxLines = 2
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        // TLDR preview
        Text(
            text = summary.tldr,
            style = TextStyle(
                fontSize = 12.sp,
                color = GlanceTheme.colors.onSurfaceVariant
            ),
            maxLines = 2
        )

        Spacer(modifier = GlanceModifier.height(6.dp))

        // Metadata row
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reading time
            Text(
                text = "⏱ ${summary.readingTimeMin} min",
                style = TextStyle(
                    fontSize = 11.sp,
                    color = GlanceTheme.colors.primary
                )
            )

            Spacer(modifier = GlanceModifier.width(12.dp))

            // Domain
            summary.domain?.let { domain ->
                Text(
                    text = "🌐 $domain",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = GlanceTheme.colors.onSurfaceVariant
                    ),
                    maxLines = 1
                )
            }
        }
    }
}
