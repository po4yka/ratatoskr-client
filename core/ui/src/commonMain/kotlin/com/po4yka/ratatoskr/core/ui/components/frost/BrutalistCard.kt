@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost slab card with optional critical leading spark bar.
 *
 * No corner radius, no shadow. Critical variant adds a 4dp leading spark hairline via
 * [drawBehind]. Implements DESIGN.md § Components — Brutalist-Card.
 */
@Composable
fun BrutalistCard(
    modifier: Modifier = Modifier,
    critical: Boolean = false,
    contentPadding: Dp = AppTheme.spacing.line,
    content: @Composable ColumnScope.() -> Unit,
) {
    val ink = AppTheme.frostColors.ink
    val spark = AppTheme.frostColors.spark
    val separatorAlpha = AppTheme.border.separatorAlpha
    val sparkBarWidth = AppTheme.border.sparkBar

    val sparkModifier =
        if (critical) {
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

    Column(
        modifier =
            modifier
                .background(AppTheme.frostColors.page)
                .border(AppTheme.border.hairline, ink.copy(alpha = separatorAlpha))
                .then(sparkModifier)
                .padding(contentPadding),
        content = content,
    )
}

@Preview
@Composable
private fun BrutalistCardPreview() {
    RatatoskrTheme {
        BrutalistCard(critical = true) {
            FrostText("Critical card content")
        }
    }
}
