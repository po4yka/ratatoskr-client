package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.domain.model.TopicStat
import com.po4yka.bitesizereader.domain.model.UserStats
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.Spacing

/**
 * Card displaying user statistics on the settings screen.
 */
@OptIn(ExperimentalLayoutApi::class)
@Suppress("FunctionNaming")
@Composable
fun UserStatsCard(
    stats: UserStats?,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimensions.cardCornerRadius))
                .background(Carbon.theme.layer01)
                .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = "Your Statistics",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )

        when {
            isLoading -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SmallLoading()
                    Text(
                        text = "Loading stats...",
                        style = Carbon.typography.label01,
                        color = Carbon.theme.textSecondary,
                    )
                }
            }
            stats != null -> {
                // Summary counts row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    StatItem(label = "Total", value = stats.totalSummaries.toString())
                    StatItem(label = "Read", value = stats.readCount.toString())
                    StatItem(label = "Unread", value = stats.unreadCount.toString())
                }

                // Reading time stats (if available)
                stats.totalReadingTimeMin?.let { totalTime ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Total reading time",
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textSecondary,
                        )
                        Text(
                            text = formatReadingTime(totalTime),
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textPrimary,
                        )
                    }
                }

                stats.averageReadingTimeMin?.let { avgTime ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Avg. per article",
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textSecondary,
                        )
                        Text(
                            text = "${avgTime.toInt()} min",
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textPrimary,
                        )
                    }
                }

                // Favorite topics (if available)
                stats.favoriteTopics?.takeIf { it.isNotEmpty() }?.let { topics ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        Text(
                            text = "Favorite Topics",
                            style = Carbon.typography.label01,
                            color = Carbon.theme.textSecondary,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xxs + 2.dp),
                            verticalArrangement = Arrangement.spacedBy(Spacing.xxs + 2.dp),
                        ) {
                            topics.take(5).forEach { topicStat ->
                                TopicChip(topicStat = topicStat)
                            }
                        }
                    }
                }

                // Member since (if available)
                stats.joinedAt?.let { joinedAt ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Member since",
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textSecondary,
                        )
                        Text(
                            text = formatDate(joinedAt),
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textPrimary,
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = "Stats unavailable",
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )
        Text(
            text = label,
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun TopicChip(
    topicStat: TopicStat,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "${topicStat.topic} (${topicStat.count})",
        style = Carbon.typography.label01,
        color = Carbon.theme.textOnColor,
        modifier =
            modifier
                .clip(RoundedCornerShape(Spacing.sm))
                .background(Carbon.theme.linkPrimary)
                .padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
    )
}

private fun formatReadingTime(minutes: Int): String {
    return when {
        minutes < 60 -> "$minutes min"
        else -> {
            val hours = minutes / 60
            val mins = minutes % 60
            if (mins > 0) "${hours}h ${mins}m" else "${hours}h"
        }
    }
}

private fun formatDate(isoDate: String): String {
    // Simple date formatting - just extract the date part
    return isoDate.take(10)
}
