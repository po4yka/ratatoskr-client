package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.common_percent
import ratatoskr.core.ui.generated.resources.reading_goal_completed
import ratatoskr.core.ui.generated.resources.reading_goal_streak
import ratatoskr.core.ui.generated.resources.reading_goal_today_progress
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.ReadingGoalProgress
import com.po4yka.ratatoskr.core.ui.icons.CarbonIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming")
@Composable
fun ReadingGoalCard(
    goalProgress: ReadingGoalProgress,
    modifier: Modifier = Modifier,
) {
    LayerCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReadingGoalProgressRing(
                progressFraction = goalProgress.progressFraction,
            )

            Spacer(modifier = Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                val readMin = goalProgress.todayReadingSec / 60
                val targetMin = goalProgress.goal.dailyTargetMin
                Text(
                    text = stringResource(Res.string.reading_goal_today_progress, readMin, targetMin),
                    style = AppTheme.type.bodyCompact01,
                    color = AppTheme.colors.textPrimary,
                )
                if (goalProgress.goal.currentStreakDays > 0) {
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = CarbonIcons.Favorite,
                            contentDescription = null,
                            tint = AppTheme.colors.textSecondary,
                            modifier = Modifier.size(Dimensions.readingGoalInlineIconSize),
                        )
                        Spacer(modifier = Modifier.width(Spacing.xxs))
                        Text(
                            text = stringResource(Res.string.reading_goal_streak, goalProgress.goal.currentStreakDays),
                            style = AppTheme.type.label01,
                            color = AppTheme.colors.textSecondary,
                        )
                    }
                }
            }

            if (goalProgress.isCompletedToday) {
                Icon(
                    imageVector = CarbonIcons.CheckmarkFilled,
                    contentDescription = stringResource(Res.string.reading_goal_completed),
                    tint = AppTheme.colors.supportSuccess,
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
    val progressColor = AppTheme.colors.linkPrimary

    Box(
        modifier = modifier.size(Dimensions.readingGoalRingSize),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(Dimensions.readingGoalRingSize)) {
            val strokeWidth = Dimensions.readingGoalRingStrokeWidth.toPx()
            drawArc(
                color = progressColor.copy(alpha = 0.3f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progressFraction,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
        Text(
            text = stringResource(Res.string.common_percent, (progressFraction * 100).toInt()),
            style = AppTheme.type.label01,
            color = AppTheme.colors.textPrimary,
        )
    }
}
