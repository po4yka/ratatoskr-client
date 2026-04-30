package com.po4yka.ratatoskr.core.ui.components.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost 1dp hairline divider (horizontal or vertical).
 *
 * Color is ink at [alpha] opacity. Implements DESIGN.md § Shapes — 1dp hairline borders.
 */
@Composable
fun FrostDivider(
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    alpha: Float = AppTheme.border.separatorAlpha,
) {
    val color = AppTheme.frostColors.ink.copy(alpha = alpha)
    if (vertical) {
        Box(
            modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(color),
        )
    } else {
        Box(
            modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color),
        )
    }
}

@Preview
@Composable
private fun FrostDividerPreview() {
    RatatoskrTheme {
        FrostDivider()
    }
}
