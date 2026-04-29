package com.po4yka.ratatoskr.feature.settings.ui.screens

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
import com.po4yka.ratatoskr.core.ui.components.AppSpinner
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.DomainStat
import com.po4yka.ratatoskr.domain.model.GoalProgress
import com.po4yka.ratatoskr.domain.model.Streak
import com.po4yka.ratatoskr.domain.model.TopicStat
import com.po4yka.ratatoskr.domain.model.UserStats
import com.po4yka.ratatoskr.presentation.navigation.StatsComponent
import com.po4yka.ratatoskr.core.ui.icons.CarbonIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.a11y_goal_in_progress
import ratatoskr.core.ui.generated.resources.a11y_goal_progress
import ratatoskr.core.ui.generated.resources.a11y_loading
import ratatoskr.core.ui.generated.resources.a11y_streak_description
import ratatoskr.core.ui.generated.resources.stats_avg_per_summary
import ratatoskr.core.ui.generated.resources.stats_best_streak
import ratatoskr.core.ui.generated.resources.stats_current_streak
import ratatoskr.core.ui.generated.resources.stats_error_default
import ratatoskr.core.ui.generated.resources.stats_error_icon
import ratatoskr.core.ui.generated.resources.stats_goal_achieved
import ratatoskr.core.ui.generated.resources.stats_goal_label
import ratatoskr.core.ui.generated.resources.stats_goal_type_daily
import ratatoskr.core.ui.generated.resources.stats_goal_type_monthly
import ratatoskr.core.ui.generated.resources.stats_goal_type_weekly
import ratatoskr.core.ui.generated.resources.stats_goals_and_streaks
import ratatoskr.core.ui.generated.resources.stats_hours_minutes_short
import ratatoskr.core.ui.generated.resources.stats_hours_short
import ratatoskr.core.ui.generated.resources.stats_languages
import ratatoskr.core.ui.generated.resources.stats_minutes_seconds_short
import ratatoskr.core.ui.generated.resources.stats_minutes_short
import ratatoskr.core.ui.generated.resources.stats_overview
import ratatoskr.core.ui.generated.resources.stats_read
import ratatoskr.core.ui.generated.resources.stats_seconds_short
import ratatoskr.core.ui.generated.resources.stats_this_month
import ratatoskr.core.ui.generated.resources.stats_this_week
import ratatoskr.core.ui.generated.resources.stats_title
import ratatoskr.core.ui.generated.resources.stats_top_sources
import ratatoskr.core.ui.generated.resources.stats_top_topics
import ratatoskr.core.ui.generated.resources.stats_total
import ratatoskr.core.ui.generated.resources.stats_total_reading_time
import ratatoskr.core.ui.generated.resources.stats_unread
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
                .background(AppTheme.colors.background),
    ) {
        StatsHeader()

        when {
            state.isLoading -> {
                val loadingDesc = stringResource(Res.string.a11y_loading)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    AppSpinner(
                        modifier =
                            Modifier.size(PROGRESS_INDICATOR_SIZE.dp)
                                .semantics { contentDescription = loadingDesc },
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
                            tint = AppTheme.colors.supportError,
                            modifier = Modifier.size(Spacing.xl),
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = state.error ?: stringResource(Res.string.stats_error_default),
                            style = AppTheme.type.bodyCompact01,
                            color = AppTheme.colors.supportError,
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
                .background(AppTheme.colors.layer01)
                .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.stats_title),
            style = AppTheme.type.heading03,
            color = AppTheme.colors.textPrimary,
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
            style = AppTheme.type.heading02,
            color = AppTheme.colors.textPrimary,
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
                    color = AppTheme.colors.layer01,
                    shape = RoundedCornerShape(STAT_CARD_CORNER_RADIUS.dp),
                )
                .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = CarbonIcons.Star,
            contentDescription = null,
            tint = AppTheme.colors.supportWarning,
            modifier = Modifier.size(24.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.stats_current_streak, streak.currentStreak),
                style = AppTheme.type.heading03,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                text = stringResource(Res.string.stats_best_streak, streak.longestStreak),
                style = AppTheme.type.label01,
                color = AppTheme.colors.textSecondary,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(Res.string.stats_this_week, streak.weekCount),
                style = AppTheme.type.label01,
                color = AppTheme.colors.textSecondary,
            )
            Text(
                text = stringResource(Res.string.stats_this_month, streak.monthCount),
                style = AppTheme.type.label01,
                color = AppTheme.colors.textSecondary,
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
    val goalDesc =
        stringResource(
            Res.string.a11y_goal_progress,
            goalTypeLabel(goalProgress.goalType),
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
                    color = AppTheme.colors.layer01,
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
                stringResource(
                    Res.string.stats_goal_label,
                    goalTypeLabel(goalProgress.goalType),
                    goalProgress.currentCount,
                    goalProgress.targetCount,
                )
            Text(
                text = goalLabel,
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textPrimary,
            )
            if (goalProgress.achieved) {
                Icon(
                    imageVector = CarbonIcons.CheckmarkFilled,
                    contentDescription = stringResource(Res.string.stats_goal_achieved),
                    tint = AppTheme.colors.supportSuccess,
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
                        color = AppTheme.colors.layer02,
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
                                    AppTheme.colors.supportSuccess
                                } else {
                                    AppTheme.colors.borderInteractive
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
            style = AppTheme.type.heading02,
            color = AppTheme.colors.textPrimary,
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
                val totalTime = stats.totalReadingTimeMin
                if (totalTime != null) {
                    StatCard(
                        label = stringResource(Res.string.stats_total_reading_time),
                        value = formatReadingTime(totalTime),
                        modifier = Modifier.weight(SUMMARY_CARD_WEIGHT),
                    )
                }
                val avgTime = stats.averageReadingTimeMin
                if (avgTime != null) {
                    StatCard(
                        label = stringResource(Res.string.stats_avg_per_summary),
                        value = formatAverageTime(avgTime),
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
                    color = AppTheme.colors.layer01,
                    shape = RoundedCornerShape(STAT_CARD_CORNER_RADIUS.dp),
                )
                .padding(Spacing.sm),
    ) {
        Text(
            text = value,
            style = AppTheme.type.heading03,
            color = AppTheme.colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = AppTheme.type.label01,
            color = AppTheme.colors.textSecondary,
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
            style = AppTheme.type.heading02,
            color = AppTheme.colors.textPrimary,
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
            style = AppTheme.type.heading02,
            color = AppTheme.colors.textPrimary,
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
                    color = AppTheme.colors.layer01,
                    shape = RoundedCornerShape(Dimensions.chipCornerRadius),
                )
                .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = AppTheme.type.bodyCompact01,
            color = AppTheme.colors.textPrimary,
        )
        Spacer(modifier = Modifier.width(Spacing.xxs))
        Text(
            text = count.toString(),
            style = AppTheme.type.label01,
            color = AppTheme.colors.textSecondary,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun LanguageSection(distribution: Map<String, Int>) {
    Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
        Text(
            text = stringResource(Res.string.stats_languages),
            style = AppTheme.type.heading02,
            color = AppTheme.colors.textPrimary,
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
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                text = count.toString(),
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        color = AppTheme.colors.layer02,
                        shape = RoundedCornerShape(2.dp),
                    ),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(fraction)
                        .height(4.dp)
                        .background(
                            color = AppTheme.colors.borderInteractive,
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
        color = AppTheme.colors.borderSubtle00,
    )
    Spacer(modifier = Modifier.height(Spacing.lg))
}

@Composable
private fun formatReadingTime(minutes: Int): String {
    val hours = minutes / MINUTES_PER_HOUR
    val mins = minutes % MINUTES_PER_HOUR
    return when {
        hours > 0 && mins > 0 -> stringResource(Res.string.stats_hours_minutes_short, hours, mins)
        hours > 0 -> stringResource(Res.string.stats_hours_short, hours)
        else -> stringResource(Res.string.stats_minutes_short, mins)
    }
}

@Composable
private fun formatAverageTime(minutes: Float): String {
    val totalMins = minutes.toInt()
    val secs = ((minutes - totalMins) * MINUTES_PER_HOUR).toInt()
    return when {
        totalMins > 0 && secs > 0 -> stringResource(Res.string.stats_minutes_seconds_short, totalMins, secs)
        totalMins > 0 -> stringResource(Res.string.stats_minutes_short, totalMins)
        else -> stringResource(Res.string.stats_seconds_short, secs)
    }
}

@Composable
private fun goalTypeLabel(goalType: String): String =
    when (goalType.lowercase()) {
        "daily" -> stringResource(Res.string.stats_goal_type_daily)
        "weekly" -> stringResource(Res.string.stats_goal_type_weekly)
        "monthly" -> stringResource(Res.string.stats_goal_type_monthly)
        else -> goalType.replaceFirstChar { it.uppercase() }
    }
