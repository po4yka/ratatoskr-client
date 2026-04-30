package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.frost.BracketSlider

// TODO: callers should migrate to BracketSlider directly; AppSlider is a transitional shim
@Composable
fun AppSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
) {
    BracketSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        range = valueRange,
        steps = steps,
        enabled = enabled,
    )
}
