@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Ingest pipeline state for [IngestLine].
 */
enum class IngestState { Idle, Active, Error }

/**
 * Frost ingest line: mono status row with animated dots when active, spark bar when error.
 *
 * Active state animates dot opacity via [AppTheme.motion.pulse].
 * Error state shows a 4dp leading spark bar.
 * Implements DESIGN.md § Components — IngestLine.
 */
@Composable
fun IngestLine(
    state: IngestState,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink
    val spark = AppTheme.frostColors.spark
    val sparkBarWidth = AppTheme.border.sparkBar

    val infiniteTransition = rememberInfiniteTransition(label = "ingest_pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = AppTheme.alpha.secondary,
        targetValue = AppTheme.alpha.active,
        animationSpec = AppTheme.motion.pulse,
        label = "dot_alpha",
    )

    val label =
        when (state) {
            IngestState.Idle -> "[ IDLE ●●●—— ]"
            IngestState.Active -> "[ ACTIVE ●●●—— ]"
            IngestState.Error -> "[ ERROR ●●●—— ]"
        }

    val textAlpha =
        when (state) {
            IngestState.Active -> dotAlpha
            IngestState.Error -> AppTheme.alpha.active
            IngestState.Idle -> AppTheme.alpha.secondary
        }

    val sparkModifier =
        if (state == IngestState.Error) {
            Modifier.drawBehind {
                val barWidthPx = sparkBarWidth.toPx()
                drawRect(
                    color = spark,
                    topLeft = Offset.Zero,
                    size = size.copy(width = barWidthPx),
                )
            }
        } else {
            Modifier
        }

    FrostText(
        text = label,
        style = AppTheme.frostType.monoSm,
        color = ink.copy(alpha = textAlpha),
        modifier = modifier.then(sparkModifier),
    )
}

@Preview
@Composable
private fun IngestLinePreview() {
    RatatoskrTheme {
        IngestLine(state = IngestState.Active)
    }
}
