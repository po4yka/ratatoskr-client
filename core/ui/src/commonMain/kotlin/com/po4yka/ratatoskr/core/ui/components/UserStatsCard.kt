package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BrutalistCard
import com.po4yka.ratatoskr.core.ui.components.frost.MultiSelectChip
import com.po4yka.ratatoskr.core.ui.components.frost.RowDigest
import com.po4yka.ratatoskr.core.ui.components.frost.SectionHeading
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.frost.FrostSpinner
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.UserStats
import org.jetbrains.compose.resources.stringResource
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
    val ink = AppTheme.frostColors.ink

    BrutalistCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
        ) {
            SectionHeading(text = stringResource(Res.string.user_stats_title))

            when {
                isLoading -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FrostSpinner(size = 16.dp)
                        FrostText(
                            text = stringResource(Res.string.user_stats_loading),
                            style = AppTheme.frostType.monoSm,
                            color = ink.copy(alpha = AppTheme.alpha.secondary),
                        )
                    }
                }
                stats != null -> {
                    // Summary counts as RowDigest cells
                    RowDigest(
                        label = stringResource(Res.string.stats_total),
                        value = stats.totalSummaries.toString(),
                    )
                    RowDigest(
                        label = stringResource(Res.string.stats_read),
                        value = stats.readCount.toString(),
                    )
                    RowDigest(
                        label = stringResource(Res.string.stats_unread),
                        value = stats.unreadCount.toString(),
                    )

                    stats.totalReadingTimeMin?.let { totalTime ->
                        RowDigest(
                            label = stringResource(Res.string.stats_total_reading_time),
                            value = formatReadingTime(totalTime),
                        )
                    }

                    stats.averageReadingTimeMin?.let { avgTime ->
                        RowDigest(
                            label = stringResource(Res.string.stats_avg_per_summary),
                            value = stringResource(Res.string.user_stats_average_minutes, avgTime.toInt()),
                        )
                    }

                    // Favorite topics chips
                    stats.favoriteTopics?.takeIf { it.isNotEmpty() }?.let { topics ->
                        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell)) {
                            FrostText(
                                text = stringResource(Res.string.user_stats_favorite_topics),
                                style = AppTheme.frostType.monoSm,
                                color = ink.copy(alpha = AppTheme.alpha.secondary),
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
                                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
                            ) {
                                topics.take(5).forEach { topicStat ->
                                    MultiSelectChip(
                                        label =
                                            stringResource(
                                                Res.string.user_stats_topic_chip,
                                                topicStat.topic,
                                                topicStat.count,
                                            ),
                                        selected = true,
                                        onToggle = {},
                                        enabled = false,
                                    )
                                }
                            }
                        }
                    }

                    stats.joinedAt?.let { joinedAt ->
                        RowDigest(
                            label = stringResource(Res.string.user_stats_member_since),
                            value = formatDate(joinedAt),
                        )
                    }
                }
                else -> {
                    FrostText(
                        text = stringResource(Res.string.user_stats_unavailable),
                        style = AppTheme.frostType.monoBody,
                        color = ink.copy(alpha = AppTheme.alpha.secondary),
                    )
                }
            }
        }
    }
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
    return isoDate.take(10)
}
