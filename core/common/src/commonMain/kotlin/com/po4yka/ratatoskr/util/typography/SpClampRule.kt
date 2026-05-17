package com.po4yka.ratatoskr.util.typography

/**
 * Pure rule that resolves an effective text size in `sp` from a baseline
 * value and the OS font scale, clamped to a legible range.
 *
 * Used by the Compose-side `sp-literal` migration so each `Text(...)`
 * call site can do
 * `fontSize = SpClampRule.effective(baselineSp = 16f, fontScale = LocalDensity.current.fontScale).sp`
 * instead of inlining the clamp math, which has been duplicated and
 * inconsistent across surfaces (some clamped at 0.85x, some let 2.0x
 * blow out the Frost layout, some had no clamp at all).
 *
 * Defaults capture the design-system minimum (12 sp — legibility floor
 * on phone) and maximum (40 sp — keeps the longest Frost button label
 * on one line at 2.5x system scale). Callers can override per-surface
 * for tighter chrome (tab labels, status strips) or looser body text.
 *
 * Defensive: a negative baseline or a negative scale collapses to the
 * minimum so a transient bad value during recompose / hot reload
 * doesn't render a frame with a negative text height.
 *
 * Pure, side-effect-free, deterministic.
 */
object SpClampRule {
    const val DEFAULT_MIN_SP: Float = 12f
    const val DEFAULT_MAX_SP: Float = 40f

    fun effective(
        baselineSp: Float,
        fontScale: Float,
        minSp: Float = DEFAULT_MIN_SP,
        maxSp: Float = DEFAULT_MAX_SP,
    ): Float {
        val safeBaseline = if (baselineSp < 0f) 0f else baselineSp
        val safeScale = if (fontScale < 0f) 0f else fontScale
        val scaled = safeBaseline * safeScale
        return scaled.coerceIn(minSp, maxSp)
    }
}
