@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost status badge with severity level.
 *
 * Alarm severity adds a 4dp leading spark bar. Alarm text is NEVER red — ink only.
 * Implements DESIGN.md § Components — Status-Badge, Spark Accent Policy.
 */
enum class StatusBadgeSeverity { Info, Warn, Alarm }

@Composable
fun StatusBadge(
    label: String,
    severity: StatusBadgeSeverity = StatusBadgeSeverity.Info,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink
    val spark = AppTheme.frostColors.spark
    val separatorAlpha = AppTheme.border.separatorAlpha
    val sparkBarWidth = AppTheme.border.sparkBar

    val sparkModifier =
        if (severity == StatusBadgeSeverity.Alarm) {
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
        text = label.uppercase(),
        style = AppTheme.frostType.monoXs,
        color = ink, // Never red text, even for alarm
        modifier =
            modifier
                .border(AppTheme.border.hairline, ink.copy(alpha = separatorAlpha))
                .then(sparkModifier)
                .padding(horizontal = AppTheme.spacing.cell, vertical = 4.dp),
    )
}

@Preview
@Composable
private fun StatusBadgePreview() {
    RatatoskrTheme {
        StatusBadge(label = "Failed", severity = StatusBadgeSeverity.Alarm)
    }
}
