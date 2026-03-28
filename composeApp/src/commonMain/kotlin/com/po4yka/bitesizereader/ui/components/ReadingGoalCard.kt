package com.po4yka.bitesizereader.ui.components

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
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.common_percent
import bitesizereader.composeapp.generated.resources.reading_goal_completed
import bitesizereader.composeapp.generated.resources.reading_goal_streak
import bitesizereader.composeapp.generated.resources.reading_goal_today_progress
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.ReadingGoalProgress
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming")
@Composable
fun ReadingGoalCard(
    goalProgress: ReadingGoalProgress,
    modifier: Modifier = Modifier,
) {
    CarbonLayerCard(
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
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textPrimary,
                )
                if (goalProgress.goal.currentStreakDays > 0) {
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = CarbonIcons.Favorite,
                            contentDescription = null,
                            tint = Carbon.theme.textSecondary,
                            modifier = Modifier.size(Dimensions.readingGoalInlineIconSize),
                        )
                        Spacer(modifier = Modifier.width(Spacing.xxs))
                        Text(
                            text = stringResource(Res.string.reading_goal_streak, goalProgress.goal.currentStreakDays),
                            style = Carbon.typography.label01,
                            color = Carbon.theme.textSecondary,
                        )
                    }
                }
            }

            if (goalProgress.isCompletedToday) {
                Icon(
                    imageVector = CarbonIcons.CheckmarkFilled,
                    contentDescription = stringResource(Res.string.reading_goal_completed),
                    tint = Carbon.theme.supportSuccess,
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
    val progressColor = Carbon.theme.linkPrimary

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
            style = Carbon.typography.label01,
            color = Carbon.theme.textPrimary,
        )
    }
}
