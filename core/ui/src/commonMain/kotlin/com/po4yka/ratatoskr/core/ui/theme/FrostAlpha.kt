package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Immutable

/**
 * Frost alpha ladder for consistent opacity across ink/page compositing.
 *
 * Apply to [FrostColors.ink] or [FrostColors.page] via `Color.copy(alpha = FrostAlpha.X)`.
 */
@Immutable
data class FrostAlpha(
    val quiet: Float = 0.25f,
    val dot: Float = 0.40f,
    val inactive: Float = 0.50f,
    val lowSignal: Float = 0.55f,
    val meta: Float = 0.60f,
    val secondary: Float = 0.70f,
    val activeSoft: Float = 0.85f,
    val active: Float = 1.00f,
)

val frostAlphaDefault = FrostAlpha()
