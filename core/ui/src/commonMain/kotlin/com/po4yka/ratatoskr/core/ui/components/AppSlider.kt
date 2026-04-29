package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.theme.AppTheme

@Composable
fun AppSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        colors =
            SliderDefaults.colors(
                thumbColor = AppTheme.colors.linkPrimary,
                activeTrackColor = AppTheme.colors.linkPrimary,
                inactiveTrackColor = AppTheme.colors.borderSubtle00,
                disabledThumbColor = AppTheme.colors.iconDisabled,
                disabledActiveTrackColor = AppTheme.colors.iconDisabled,
                disabledInactiveTrackColor = AppTheme.colors.borderDisabled,
            ),
    )
}
