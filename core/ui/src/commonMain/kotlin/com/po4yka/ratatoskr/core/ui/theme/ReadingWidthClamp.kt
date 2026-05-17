package com.po4yka.ratatoskr.core.ui.theme

/**
 * Pure math behind the reading-width preference slider on SummaryDetailScreen.
 *
 * The user picks a typographic preference for max line length in characters
 * (45..75 ch, default 65); the body composable converts that to a dp width via the
 * mono font's character metric and applies it through `Modifier.widthIn(max = N.dp)`.
 *
 * On phone-sized screens that are already narrower than the computed max, the
 * preference is intentionally ignored — the body uses the available width as today,
 * matching the spec's "Defaults must look identical to today's layout on phone-sized
 * screens" constraint.
 *
 * Returns raw `Double` values so this stays Compose-free and unit-testable; the
 * caller wraps the result with `.dp` at the call site.
 */
object ReadingWidthClamp {
    const val MIN_CH: Int = 45
    const val MAX_CH: Int = 75
    const val DEFAULT_CH: Int = 65

    fun clamp(ch: Int): Int = ch.coerceIn(MIN_CH, MAX_CH)

    fun maxBodyDp(
        lineWidthCh: Int,
        characterWidthDp: Double,
        screenWidthDp: Double,
    ): Double {
        val computed = clamp(lineWidthCh) * characterWidthDp
        return minOf(computed, screenWidthDp)
    }
}
