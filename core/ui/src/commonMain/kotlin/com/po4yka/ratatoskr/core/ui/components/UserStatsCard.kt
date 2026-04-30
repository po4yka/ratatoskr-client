package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.stats_avg_per_summary
import ratatoskr.core.ui.generated.resources.stats_read
import ratatoskr.core.ui.generated.resources.stats_total
import ratatoskr.core.ui.generated.resources.stats_total_reading_time
import ratatoskr.core.ui.generated.resources.stats_unread
import ratatoskr.core.ui.generated.resources.user_stats_average_minutes
import ratatoskr.core.ui.generated.resources.user_stats_favorite_topics
import ratatoskr.core.ui.generated.resources.user_stats_hours_minutes_short
import ratatoskr.core.ui.generated.resources.user_stats_hours_short
import ratatoskr.core.ui.generated.resources.user_stats_loading
import ratatoskr.core.ui.generated.resources.user_stats_member_since
import ratatoskr.core.ui.generated.resources.user_stats_minutes_short
import ratatoskr.core.ui.generated.resources.user_stats_title
import ratatoskr.core.ui.generated.resources.user_stats_topic_chip
import ratatoskr.core.ui.generated.resources.user_stats_unavailable
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.TopicStat
import com.po4yka.ratatoskr.domain.model.UserStats
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

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
                .clip(RectangleShape)
                .background(AppTheme.colors.layer01)
                .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.user_stats_title),
            style = AppTheme.type.headingCompact01,
            color = AppTheme.colors.textPrimary,
        )

        when {
            isLoading -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppSmallSpinner()
                    Text(
                        text = stringResource(Res.string.user_stats_loading),
                        style = AppTheme.type.label01,
                        color = AppTheme.colors.textSecondary,
                    )
                }
            }
            stats != null -> {
                // Summary counts row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    StatItem(label = stringResource(Res.string.stats_total), value = stats.totalSummaries.toString())
                    StatItem(label = stringResource(Res.string.stats_read), value = stats.readCount.toString())
                    StatItem(label = stringResource(Res.string.stats_unread), value = stats.unreadCount.toString())
                }

                // Reading time stats (if available)
                stats.totalReadingTimeMin?.let { totalTime ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(Res.string.stats_total_reading_time),
                            style = AppTheme.type.bodyCompact01,
                            color = AppTheme.colors.textSecondary,
                        )
                        Text(
                            text = formatReadingTime(totalTime),
                            style = AppTheme.type.bodyCompact01,
                            color = AppTheme.colors.textPrimary,
                        )
                    }
                }

                stats.averageReadingTimeMin?.let { avgTime ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(Res.string.stats_avg_per_summary),
                            style = AppTheme.type.bodyCompact01,
                            color = AppTheme.colors.textSecondary,
                        )
                        Text(
                            text = stringResource(Res.string.user_stats_average_minutes, avgTime.toInt()),
                            style = AppTheme.type.bodyCompact01,
                            color = AppTheme.colors.textPrimary,
                        )
                    }
                }

                // Favorite topics (if available)
                stats.favoriteTopics?.takeIf { it.isNotEmpty() }?.let { topics ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        Text(
                            text = stringResource(Res.string.user_stats_favorite_topics),
                            style = AppTheme.type.label01,
                            color = AppTheme.colors.textSecondary,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
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
                            text = stringResource(Res.string.user_stats_member_since),
                            style = AppTheme.type.bodyCompact01,
                            color = AppTheme.colors.textSecondary,
                        )
                        Text(
                            text = formatDate(joinedAt),
                            style = AppTheme.type.bodyCompact01,
                            color = AppTheme.colors.textPrimary,
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = stringResource(Res.string.user_stats_unavailable),
                    style = AppTheme.type.bodyCompact01,
                    color = AppTheme.colors.textSecondary,
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
            style = AppTheme.type.heading03,
            color = AppTheme.colors.textPrimary,
        )
        Text(
            text = label,
            style = AppTheme.type.label01,
            color = AppTheme.colors.textSecondary,
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
        text = stringResource(Res.string.user_stats_topic_chip, topicStat.topic, topicStat.count),
        style = AppTheme.type.label01,
        color = AppTheme.colors.textOnColor,
        modifier =
            modifier
                .clip(RectangleShape)
                .background(AppTheme.colors.linkPrimary)
                .padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
    )
}

@Composable
private fun formatReadingTime(minutes: Int): String {
    return when {
        minutes < 60 -> stringResource(Res.string.user_stats_minutes_short, minutes)
        else -> {
            val hours = minutes / 60
            val mins = minutes % 60
            if (mins > 0) {
                stringResource(Res.string.user_stats_hours_minutes_short, hours, mins)
            } else {
                stringResource(Res.string.user_stats_hours_short, hours)
            }
        }
    }
}

private fun formatDate(isoDate: String): String {
    // Simple date formatting - just extract the date part
    return isoDate.take(10)
}
