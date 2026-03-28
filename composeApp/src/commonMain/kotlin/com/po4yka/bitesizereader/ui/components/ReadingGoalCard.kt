package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.common_percent
import bitesizereader.composeapp.generated.resources.reading_goal_completed
import bitesizereader.composeapp.generated.resources.reading_goal_streak
import bitesizereader.composeapp.generated.resources.reading_goal_today_progress
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.ReadingGoalProgress
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import org.jetbrains.compose.resources.stringResource

private const val COMPLETED_SYMBOL = '\u2713'

@Suppress("FunctionNaming")
@Composable
fun ReadingGoalCard(
    goalProgress: ReadingGoalProgress,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Circular progress ring
        val progressColor = Carbon.theme.linkPrimary
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(56.dp)) {
                val strokeWidth = 6.dp.toPx()
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
                    sweepAngle = 360f * goalProgress.progressFraction,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }
            Text(
                text = stringResource(Res.string.common_percent, (goalProgress.progressFraction * 100).toInt()),
                style = Carbon.typography.label01,
                color = Carbon.theme.textPrimary,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            val readMin = goalProgress.todayReadingSec / 60
            val targetMin = goalProgress.goal.dailyTargetMin
            Text(
                text = stringResource(Res.string.reading_goal_today_progress, readMin, targetMin),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
            )
            if (goalProgress.goal.currentStreakDays > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = CarbonIcons.Favorite,
                        contentDescription = null,
                        tint = Carbon.theme.textSecondary,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.reading_goal_streak, goalProgress.goal.currentStreakDays),
                        style = Carbon.typography.label01,
                        color = Carbon.theme.textSecondary,
                    )
                }
            }
        }

        if (goalProgress.isCompletedToday) {
            Text(
                text = COMPLETED_SYMBOL.toString(),
                style = Carbon.typography.heading02,
                color = Carbon.theme.supportSuccess,
            )
        }
    }
}
