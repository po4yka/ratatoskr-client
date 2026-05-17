package com.po4yka.ratatoskr.util.progress

/**
 * Pure progress-pair formatter for the iOS Live Activity, Android
 * notification progress bar, and any widget that shows a "completed of
 * total" count. Centralizes the clamp + divide-by-zero defenses so each
 * surface doesn't reinvent them inconsistently.
 *
 * Defenses:
 *  - `total <= 0` returns `0.0f / "0 of 0" / 0` instead of throwing
 *    `ArithmeticException` — the orchestrator can show a Live Activity
 *    briefly before the total is known.
 *  - `completed > total` clamps to total so the bar never renders past
 *    100% even during a racing update.
 *  - Negative `completed` or negative `total` clamp to 0.
 *
 * Pure, side-effect-free, deterministic. All three outputs are
 * derived from the same clamped pair so they never disagree.
 */
object ProgressFractionFormatter {
    fun ratio(
        completed: Int,
        total: Int,
    ): Float {
        val safeTotal = total.coerceAtLeast(0)
        if (safeTotal == 0) return 0.0f
        val safeCompleted = completed.coerceIn(0, safeTotal)
        return safeCompleted.toFloat() / safeTotal.toFloat()
    }

    fun label(
        completed: Int,
        total: Int,
    ): String {
        val safeTotal = total.coerceAtLeast(0)
        val safeCompleted = completed.coerceIn(0, safeTotal)
        return "$safeCompleted of $safeTotal"
    }

    fun percent(
        completed: Int,
        total: Int,
    ): Int = (ratio(completed, total) * 100f).toInt()
}
