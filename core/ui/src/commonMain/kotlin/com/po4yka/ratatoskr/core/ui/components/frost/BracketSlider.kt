@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme
import kotlin.math.roundToInt

/**
 * Frost bracket slider: custom draggable mono-track layout.
 *
 * Frost two-color rule: track uses ink at quiet alpha, filled portion uses ink.
 * No Material 3 dependency.
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
    val fraction = ((value - range.start) / (range.endInclusive - range.start)).coerceIn(0f, 1f)
    var trackWidthPx by remember { mutableFloatStateOf(1f) }

    fun snapFraction(frac: Float): Float =
        if (steps > 0) {
            val step = 1f / (steps + 1)
            (frac / step).roundToInt() * step
        } else {
            frac
        }

    fun fractionToValue(frac: Float): Float =
        range.start + snapFraction(frac).coerceIn(0f, 1f) * (range.endInclusive - range.start)

    val dragModifier =
        if (enabled) {
            Modifier
                .pointerInput(range, steps) {
                    detectTapGestures { offset ->
                        onValueChange(fractionToValue(offset.x / trackWidthPx))
                    }
                }
                .pointerInput(range, steps) {
                    detectHorizontalDragGestures { change, _ ->
                        onValueChange(fractionToValue(change.position.x / trackWidthPx))
                    }
                }
        } else {
            Modifier
        }

    Box(
        modifier =
            modifier
                .height(20.dp)
                .onSizeChanged { trackWidthPx = it.width.toFloat().coerceAtLeast(1f) }
                .then(dragModifier),
        contentAlignment = Alignment.CenterStart,
    ) {
        // Track background
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(ink.copy(alpha = if (enabled) AppTheme.alpha.quiet else AppTheme.alpha.inactive)),
        )
        // Filled portion
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(fraction)
                    .height(2.dp)
                    .background(ink.copy(alpha = if (enabled) AppTheme.alpha.active else AppTheme.alpha.inactive)),
        )
        // Thumb — small square aligned to filled end (Frost: no rounding)
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            val thumbWidthFraction = if (trackWidthPx > 0f) 12f / trackWidthPx else 0f
            Box(
                modifier =
                    Modifier
                        .height(12.dp)
                        .fillMaxWidth(thumbWidthFraction)
                        .background(ink.copy(alpha = if (enabled) AppTheme.alpha.active else AppTheme.alpha.inactive)),
            )
        }
    }
}

@Preview
@Composable
private fun BracketSliderPreview() {
    RatatoskrTheme {
        BracketSlider(value = 0.4f, onValueChange = {})
    }
}
