package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.DomainStat
import com.po4yka.bitesizereader.domain.model.GoalProgress
import com.po4yka.bitesizereader.domain.model.Streak
import com.po4yka.bitesizereader.domain.model.TopicStat
import com.po4yka.bitesizereader.domain.model.UserStats
import com.po4yka.bitesizereader.presentation.navigation.StatsComponent
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.Spacing
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.a11y_goal_in_progress
import bitesizereader.composeapp.generated.resources.a11y_goal_progress
import bitesizereader.composeapp.generated.resources.a11y_loading
import bitesizereader.composeapp.generated.resources.a11y_streak_description
import bitesizereader.composeapp.generated.resources.stats_avg_per_summary
import bitesizereader.composeapp.generated.resources.stats_error_default
import bitesizereader.composeapp.generated.resources.stats_error_icon
import bitesizereader.composeapp.generated.resources.stats_goal_achieved
import bitesizereader.composeapp.generated.resources.stats_goals_and_streaks
import bitesizereader.composeapp.generated.resources.stats_languages
import bitesizereader.composeapp.generated.resources.stats_overview
import bitesizereader.composeapp.generated.resources.stats_read
import bitesizereader.composeapp.generated.resources.stats_title
import bitesizereader.composeapp.generated.resources.stats_top_sources
import bitesizereader.composeapp.generated.resources.stats_top_topics
import bitesizereader.composeapp.generated.resources.stats_total
import bitesizereader.composeapp.generated.resources.stats_total_reading_time
import bitesizereader.composeapp.generated.resources.stats_unread
import org.jetbrains.compose.resources.stringResource

private const val MINUTES_PER_HOUR = 60
private const val PROGRESS_INDICATOR_SIZE = 48
private const val SUMMARY_CARD_WEIGHT = 1f
private const val STAT_CARD_CORNER_RADIUS = 4

@Suppress("FunctionNaming")
@Composable
fun StatsScreen(
    component: StatsComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.viewModel.state.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        StatsHeader()

        when {
            state.isLoading -> {
                val loadingDesc = stringResource(Res.string.a11y_loading)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(PROGRESS_INDICATOR_SIZE.dp)
                            .semantics { contentDescription = loadingDesc },
                        color = Carbon.theme.iconPrimary,
                    )
                }
            }
            state.error != null -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(Spacing.md),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = CarbonIcons.WarningAlt,
                            contentDescription = stringResource(Res.string.stats_error_icon),
                            tint = Carbon.theme.supportError,
                            modifier = Modifier.size(Spacing.xl),
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = state.error ?: stringResource(Res.string.stats_error_default),
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.supportError,
                        )
                    }
                }
            }
            state.stats != null -> {
                StatsContent(
                    stats = requireNotNull(state.stats),
                    streak = state.streak,
                    goalsProgress = state.goalsProgress,
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun StatsHeader() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(Dimensions.headerHeight)
                .background(Carbon.theme.layer01)
                .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.stats_title),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun StatsContent(
    stats: UserStats,
    streak: Streak?,
    goalsProgress: List<GoalProgress>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        if (streak != null || goalsProgress.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(Spacing.md))
                GoalsAndStreaksSection(streak = streak, goalsProgress = goalsProgress)
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.md))
            if (streak != null || goalsProgress.isNotEmpty()) {
                SectionDivider()
            }
            SummarySection(stats = stats)
        }

        if (!stats.favoriteTopics.isNullOrEmpty()) {
            item {
                Spacer(modifier = Modifier.height(Spacing.lg))
                SectionDivider()
                TopicsSection(topics = requireNotNull(stats.favoriteTopics))
            }
        }

        if (!stats.favoriteDomains.isNullOrEmpty()) {
            item {
                Spacer(modifier = Modifier.height(Spacing.lg))
                SectionDivider()
                DomainsSection(domains = requireNotNull(stats.favoriteDomains))
            }
        }

        if (!stats.languageDistribution.isNullOrEmpty()) {
            item {
                Spacer(modifier = Modifier.height(Spacing.lg))
                SectionDivider()
                LanguageSection(distribution = requireNotNull(stats.languageDistribution))
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun GoalsAndStreaksSection(
    streak: Streak?,
    goalsProgress: List<GoalProgress>,
) {
    Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
        Text(
            text = stringResource(Res.string.stats_goals_and_streaks),
            style = Carbon.typography.heading02,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(modifier = Modifier.height(Spacing.sm))

        if (streak != null) {
            StreakCard(streak = streak)
            Spacer(modifier = Modifier.height(Spacing.sm))
        }

        goalsProgress.forEach { goal ->
            GoalProgressRow(goalProgress = goal)
            Spacer(modifier = Modifier.height(Spacing.xs))
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun StreakCard(streak: Streak) {
    val streakDesc = stringResource(Res.string.a11y_streak_description, streak.currentStreak, streak.longestStreak)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .semantics(mergeDescendants = true) { contentDescription = streakDesc }
                .background(
                    color = Carbon.theme.layer01,
                    shape = RoundedCornerShape(STAT_CARD_CORNER_RADIUS.dp),
                )
                .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = CarbonIcons.Star,
            contentDescription = null,
            tint = Carbon.theme.supportWarning,
            modifier = Modifier.size(24.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${streak.currentStreak}-day streak",
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
            )
            Text(
                text = "Best: ${streak.longestStreak} days",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${streak.weekCount} this week",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
            Text(
                text = "${streak.monthCount} this month",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun GoalProgressRow(goalProgress: GoalProgress) {
    val fraction =
        if (goalProgress.targetCount > 0) {
            goalProgress.currentCount.toFloat() / goalProgress.targetCount.toFloat()
        } else {
            0f
        }
    val statusText =
        if (goalProgress.achieved) {
            stringResource(Res.string.stats_goal_achieved)
        } else {
            stringResource(Res.string.a11y_goal_in_progress)
        }
    val goalDesc = stringResource(
        Res.string.a11y_goal_progress,
        goalProgress.goalType.replaceFirstChar { it.uppercase() },
        goalProgress.currentCount,
        goalProgress.targetCount,
        statusText,
    )
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .semantics(mergeDescendants = true) { contentDescription = goalDesc }
                .background(
                    color = Carbon.theme.layer01,
                    shape = RoundedCornerShape(STAT_CARD_CORNER_RADIUS.dp),
                )
                .padding(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val goalLabel =
                "${goalProgress.goalType.replaceFirstChar { it.uppercase() }} goal: " +
                    "${goalProgress.currentCount}/${goalProgress.targetCount}"
            Text(
                text = goalLabel,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
            )
            if (goalProgress.achieved) {
                Icon(
                    imageVector = CarbonIcons.CheckmarkFilled,
                    contentDescription = stringResource(Res.string.stats_goal_achieved),
                    tint = Carbon.theme.supportSuccess,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.xxs))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        color = Carbon.theme.layer02,
                        shape = RoundedCornerShape(2.dp),
                    ),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .height(4.dp)
                        .background(
                            color =
                                if (goalProgress.achieved) {
                                    Carbon.theme.supportSuccess
                                } else {
                                    Carbon.theme.borderInteractive
                                },
                            shape = RoundedCornerShape(2.dp),
                        ),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SummarySection(stats: UserStats) {
    Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
        Text(
            text = stringResource(Res.string.stats_overview),
            style = Carbon.typography.heading02,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            StatCard(
                label = stringResource(Res.string.stats_total),
                value = stats.totalSummaries.toString(),
                modifier = Modifier.weight(SUMMARY_CARD_WEIGHT),
            )
            StatCard(
                label = stringResource(Res.string.stats_unread),
                value = stats.unreadCount.toString(),
                modifier = Modifier.weight(SUMMARY_CARD_WEIGHT),
            )
            StatCard(
                label = stringResource(Res.string.stats_read),
                value = stats.readCount.toString(),
                modifier = Modifier.weight(SUMMARY_CARD_WEIGHT),
            )
        }
        if (stats.totalReadingTimeMin != null || stats.averageReadingTimeMin != null) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                if (stats.totalReadingTimeMin != null) {
                    StatCard(
                        label = stringResource(Res.string.stats_total_reading_time),
                        value = formatReadingTime(stats.totalReadingTimeMin),
                        modifier = Modifier.weight(SUMMARY_CARD_WEIGHT),
                    )
                }
                if (stats.averageReadingTimeMin != null) {
                    StatCard(
                        label = stringResource(Res.string.stats_avg_per_summary),
                        value = formatAverageTime(stats.averageReadingTimeMin),
                        modifier = Modifier.weight(SUMMARY_CARD_WEIGHT),
                    )
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .semantics(mergeDescendants = true) {}
                .background(
                    color = Carbon.theme.layer01,
                    shape = RoundedCornerShape(STAT_CARD_CORNER_RADIUS.dp),
                )
                .padding(Spacing.sm),
    ) {
        Text(
            text = value,
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Suppress("FunctionNaming")
@Composable
private fun TopicsSection(topics: List<TopicStat>) {
    Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
        Text(
            text = stringResource(Res.string.stats_top_topics),
            style = Carbon.typography.heading02,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            topics.forEach { topic ->
                TopicChip(label = topic.topic, count = topic.count)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Suppress("FunctionNaming")
@Composable
private fun DomainsSection(domains: List<DomainStat>) {
    Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
        Text(
            text = stringResource(Res.string.stats_top_sources),
            style = Carbon.typography.heading02,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            domains.forEach { domain ->
                TopicChip(label = domain.domain, count = domain.count)
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun TopicChip(
    label: String,
    count: Int,
) {
    Row(
        modifier =
            Modifier
                .background(
                    color = Carbon.theme.layer01,
                    shape = RoundedCornerShape(Dimensions.chipCornerRadius),
                )
                .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textPrimary,
        )
        Spacer(modifier = Modifier.width(Spacing.xxs))
        Text(
            text = count.toString(),
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun LanguageSection(distribution: Map<String, Int>) {
    Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
        Text(
            text = stringResource(Res.string.stats_languages),
            style = Carbon.typography.heading02,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        val total = distribution.values.sum().takeIf { it > 0 } ?: 1
        distribution.entries
            .sortedByDescending { it.value }
            .forEach { (lang, count) ->
                LanguageRow(language = lang, count = count, total = total)
                Spacer(modifier = Modifier.height(Spacing.xs))
            }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun LanguageRow(
    language: String,
    count: Int,
    total: Int,
) {
    val fraction = count.toFloat() / total
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = language.uppercase(),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
            )
            Text(
                text = "$count",
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        color = Carbon.theme.layer02,
                        shape = RoundedCornerShape(2.dp),
                    ),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(fraction)
                        .height(4.dp)
                        .background(
                            color = Carbon.theme.borderInteractive,
                            shape = RoundedCornerShape(2.dp),
                        ),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = Spacing.md),
        color = Carbon.theme.borderSubtle00,
    )
    Spacer(modifier = Modifier.height(Spacing.lg))
}

private fun formatReadingTime(minutes: Int): String {
    val hours = minutes / MINUTES_PER_HOUR
    val mins = minutes % MINUTES_PER_HOUR
    return when {
        hours > 0 && mins > 0 -> "${hours}h ${mins}m"
        hours > 0 -> "${hours}h"
        else -> "${mins}m"
    }
}

private fun formatAverageTime(minutes: Float): String {
    val totalMins = minutes.toInt()
    val secs = ((minutes - totalMins) * MINUTES_PER_HOUR).toInt()
    return when {
        totalMins > 0 && secs > 0 -> "${totalMins}m ${secs}s"
        totalMins > 0 -> "${totalMins}m"
        else -> "${secs}s"
    }
}
