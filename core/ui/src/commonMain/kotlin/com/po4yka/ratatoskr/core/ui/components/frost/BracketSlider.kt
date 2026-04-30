@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost bracket slider: wraps Material 3 [Slider] with Frost ink/page colors.
 *
 * TODO: replace with a custom draggable Layout rendering `[ ─◼─── ]` mono track once
 *   the M3 dependency is stripped in the final migration commit (Phase B4).
 * Implements DESIGN.md § Components — BracketSlider.
 */
@Composable
fun BracketSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
) {
    val ink = AppTheme.frostColors.ink
    val page = AppTheme.frostColors.page

    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = range,
        steps = steps,
        colors =
            SliderDefaults.colors(
                thumbColor = ink,
                activeTrackColor = ink,
                inactiveTrackColor = ink.copy(alpha = AppTheme.alpha.inactive),
                disabledThumbColor = ink.copy(alpha = AppTheme.alpha.inactive),
                disabledActiveTrackColor = ink.copy(alpha = AppTheme.alpha.inactive),
                disabledInactiveTrackColor = ink.copy(alpha = AppTheme.alpha.quiet),
                activeTickColor = page,
                inactiveTickColor = ink.copy(alpha = AppTheme.alpha.secondary),
            ),
    )
}

@Preview
@Composable
private fun BracketSliderPreview() {
    RatatoskrTheme {
        BracketSlider(value = 0.4f, onValueChange = {})
    }
}
