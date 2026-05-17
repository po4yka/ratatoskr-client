package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

/**
 * Maximum font-scale Frost will honor before clamping. Frost's editorial monospace stack
 * (JetBrains Mono with tabular figures, narrow letter-spacing) breaks at extreme accessibility
 * font scales — letters smear past padding, brackets clip, and the spark bar overlaps text.
 *
 * Clamping at `1.5×` keeps the curve readable for the "Large" / "Larger" accessibility steps
 * on Android Settings → Display → Font size, and for iOS Dynamic Type up to `Accessibility 1`,
 * without distorting the layout grid for the more extreme accessibility steps.
 *
 * Apps that need a different cap can override via [LocalFrostMaxFontScale] before composing
 * [RatatoskrTheme], or wrap a specific subtree in [ClampFontScale].
 */
const val FROST_DEFAULT_MAX_FONT_SCALE: Float = 1.5f

/**
 * Composition-local cap for the user-driven font scale. [RatatoskrTheme] reads this and
 * clamps `LocalDensity.fontScale` accordingly.
 */
val LocalFrostMaxFontScale = staticCompositionLocalOf { FROST_DEFAULT_MAX_FONT_SCALE }

/**
 * Wraps [content] in a clamped [LocalDensity] so every `sp` literal inside is scaled by no
 * more than [maxFontScale]. Use this when wrapping a subtree that should opt out of the
 * theme-wide cap (e.g., a screen that wants to allow larger text than the default).
 *
 * Wiring this directly inside [RatatoskrTheme] would defeat the override path; expose the
 * helper publicly so consumers can swap the cap locally without rebuilding the theme.
 */
@Composable
fun ClampFontScale(
    maxFontScale: Float = LocalFrostMaxFontScale.current,
    content: @Composable () -> Unit,
) {
    val current = LocalDensity.current
    val clamped =
        remember(current, maxFontScale) {
            clampDensity(current, maxFontScale)
        }
    CompositionLocalProvider(LocalDensity provides clamped, content = content)
}

/**
 * Pure helper exposed for testing. Returns [density] unchanged when `fontScale` is already
 * within the cap, otherwise returns a new [Density] with `fontScale` clamped down.
 */
fun clampDensity(
    density: Density,
    maxFontScale: Float,
): Density =
    if (density.fontScale <= maxFontScale) {
        density
    } else {
        Density(density = density.density, fontScale = maxFontScale)
    }
