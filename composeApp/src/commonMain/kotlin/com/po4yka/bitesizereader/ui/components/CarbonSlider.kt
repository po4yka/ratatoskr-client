package com.po4yka.bitesizereader.ui.components

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gabrieldrn.carbon.Carbon

@Composable
fun CarbonSlider(
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
                thumbColor = Carbon.theme.linkPrimary,
                activeTrackColor = Carbon.theme.linkPrimary,
                inactiveTrackColor = Carbon.theme.borderSubtle00,
                disabledThumbColor = Carbon.theme.iconDisabled,
                disabledActiveTrackColor = Carbon.theme.iconDisabled,
                disabledInactiveTrackColor = Carbon.theme.borderDisabled,
            ),
    )
}
