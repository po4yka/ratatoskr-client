@file:Suppress("Indentation")

package com.po4yka.ratatoskr.widget

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.po4yka.ratatoskr.MainActivity
import com.po4yka.ratatoskr.domain.model.Summary

// Frost color constants: ink flips light/dark, page inverts, spark never changes.
private val INK =
    ColorProvider(
        day = androidx.compose.ui.graphics.Color(0xFF1C242C),
        night = androidx.compose.ui.graphics.Color(0xFFE8ECF0),
    )
private val PAGE =
    ColorProvider(
        day = androidx.compose.ui.graphics.Color(0xFFF0F2F5),
        night = androidx.compose.ui.graphics.Color(0xFF12161C),
    )
private val INK_MUTED =
    ColorProvider(
        day = androidx.compose.ui.graphics.Color(0xFF4A5568),
        night = androidx.compose.ui.graphics.Color(0xFFA0AEC0),
    )

@Composable
fun RecentSummariesContent(summaries: List<Summary>) {
    Column(
        modifier =
            GlanceModifier
                .fillMaxSize()
                .background(PAGE)
                .padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "RECENT SUMMARIES",
                style =
                    TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = INK,
                    ),
            )
        }

        // Hairline border under header
        Spacer(modifier = GlanceModifier.height(8.dp))
        Spacer(modifier = GlanceModifier.height(1.dp).fillMaxWidth().background(INK))
        Spacer(modifier = GlanceModifier.height(8.dp))

        if (summaries.isEmpty()) {
            EmptyState()
        } else {
            summaries.take(5).forEach { summary ->
                SummaryItem(summary)
                // Hairline divider between items
                Spacer(modifier = GlanceModifier.height(1.dp).fillMaxWidth().background(INK))
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier =
            GlanceModifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "NO SUMMARIES YET",
            style =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = INK,
                ),
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(
            text = "Submit a URL to get started",
            style =
                TextStyle(
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = INK_MUTED,
                ),
        )
    }
}

@Composable
private fun SummaryItem(summary: Summary) {
    Column(
        modifier =
            GlanceModifier
                .fillMaxWidth()
                .clickable(onClick = actionStartActivity<MainActivity>())
                .padding(vertical = 8.dp),
    ) {
        Text(
            text = summary.title,
            style =
                TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = INK,
                ),
            maxLines = 2,
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        Text(
            text = summary.content.take(120) + if (summary.content.length > 120) "…" else "",
            style =
                TextStyle(
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = INK_MUTED,
                ),
            maxLines = 2,
        )

        extractDomain(summary.sourceUrl)?.let { domain ->
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = domain.uppercase(),
                style =
                    TextStyle(
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = INK_MUTED,
                    ),
                maxLines = 1,
            )
        }
    }
}

private fun extractDomain(url: String): String? {
    val noProtocol = url.substringAfter("://", url)
    return noProtocol.substringBefore("/").ifBlank { null }
}
