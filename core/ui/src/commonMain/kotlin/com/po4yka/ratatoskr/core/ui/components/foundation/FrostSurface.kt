package com.po4yka.ratatoskr.core.ui.components.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost-native surface backed by [Box] with optional hairline border.
 *
 * No elevation, no corner radius. Implements DESIGN.md § Shapes — 0dp radius, 1dp hairline only.
 */
@Composable
fun FrostSurface(
    modifier: Modifier = Modifier,
    color: Color = AppTheme.frostColors.page,
    borderColor: Color? = null,
    content: @Composable () -> Unit,
) {
    val borderModifier =
        if (borderColor != null) {
            Modifier.border(AppTheme.border.hairline, borderColor)
        } else {
            Modifier
        }
    Box(modifier.then(borderModifier).background(color)) {
        content()
    }
}

@Preview
@Composable
private fun FrostSurfacePreview() {
    RatatoskrTheme {
        FrostSurface(borderColor = AppTheme.frostColors.ink) {
            FrostText("Surface content")
        }
    }
}
