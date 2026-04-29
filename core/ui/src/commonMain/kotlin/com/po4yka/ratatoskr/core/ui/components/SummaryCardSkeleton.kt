package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.Spacing

@Suppress("FunctionNaming")
@Composable
fun SummaryCardSkeleton(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "shimmer_alpha",
    )

    val shimmerColor = AppTheme.colors.layer02.copy(alpha = alpha)

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimensions.cardCornerRadius))
                .background(AppTheme.colors.layer01)
                .padding(Spacing.md),
    ) {
        // Thumbnail placeholder
        SkeletonBox(
            modifier = Modifier.size(80.dp),
            color = shimmerColor,
        )

        Spacer(modifier = Modifier.width(Spacing.md))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            // Title placeholder (2 lines)
            SkeletonBox(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                color = shimmerColor,
            )
            SkeletonBox(
                modifier =
                    Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp),
                color = shimmerColor,
            )

            Spacer(modifier = Modifier.height(Spacing.xxs))

            // Source placeholder
            SkeletonBox(
                modifier =
                    Modifier
                        .width(100.dp)
                        .height(12.dp),
                color = shimmerColor,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SkeletonBox(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(Dimensions.cardCornerRadius))
                .background(color),
    )
}
