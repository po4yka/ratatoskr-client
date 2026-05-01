package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BrutalistCard
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.domain.model.ReadingGoalProgress
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.common_percent
import ratatoskr.core.ui.generated.resources.reading_goal_completed
import ratatoskr.core.ui.generated.resources.reading_goal_streak
import ratatoskr.core.ui.generated.resources.reading_goal_today_progress

@Suppress("FunctionNaming")
@Composable
fun ReadingGoalCard(
    goalProgress: ReadingGoalProgress,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink

    BrutalistCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = AppTheme.spacing.line,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // TODO: full Frost mono ring in a follow-up commit; keeping Canvas ring with ink colors
            ReadingGoalProgressRing(progressFraction = goalProgress.progressFraction)

            Spacer(modifier = Modifier.width(AppTheme.spacing.line))

            Column(modifier = Modifier.weight(1f)) {
                val readMin = goalProgress.todayReadingSec / 60
                val targetMin = goalProgress.goal.dailyTargetMin
                FrostText(
                    text = stringResource(Res.string.reading_goal_today_progress, readMin, targetMin),
                    style = AppTheme.frostType.monoBody,
                    color = ink,
                )
                if (goalProgress.goal.currentStreakDays > 0) {
                    Spacer(modifier = Modifier.height(AppTheme.spacing.gapInline))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FrostIcon(
                            imageVector = AppIcons.Favorite,
                            contentDescription = null,
                            tint = ink.copy(alpha = AppTheme.alpha.secondary),
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(modifier = Modifier.width(AppTheme.spacing.gapInline))
                        FrostText(
                            text = stringResource(Res.string.reading_goal_streak, goalProgress.goal.currentStreakDays),
                            style = AppTheme.frostType.monoSm,
                            color = ink.copy(alpha = AppTheme.alpha.secondary),
                        )
                    }
                }
            }

            if (goalProgress.isCompletedToday) {
                FrostIcon(
                    imageVector = AppIcons.CheckmarkFilled,
                    contentDescription = stringResource(Res.string.reading_goal_completed),
                    tint = ink,
                    modifier = Modifier.size(IconSizes.md),
                )
            }
        }
    }
}

@Composable
private fun ReadingGoalProgressRing(
    progressFraction: Float,
    modifier: Modifier = Modifier,
) {
    // TODO: replace with ASCII [ ████──────] mono block in follow-up commit
    val ink = AppTheme.frostColors.ink
    val trackAlpha = AppTheme.alpha.quiet

    Box(
        modifier = modifier.size(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(56.dp)) {
            val strokeWidth = 6.dp.toPx()
            drawArc(
                color = ink.copy(alpha = trackAlpha),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Square),
            )
            drawArc(
                color = ink,
                startAngle = -90f,
                sweepAngle = 360f * progressFraction,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Square),
            )
        }
        FrostText(
            text = stringResource(Res.string.common_percent, (progressFraction * 100).toInt()),
            style = AppTheme.frostType.monoXs,
            color = ink,
        )
    }
}
