package com.po4yka.ratatoskr.util.widget

/**
 * Battery / power-mode snapshot consumed by [WidgetRefreshCadence].
 *
 * `batteryPercent` is permitted to be out of `0..100` (some Android
 * `BatteryManager` polls briefly return -1 during boot or 101 right after
 * a full charge); [WidgetRefreshCadence] clamps defensively.
 */
data class BatteryState(
    val batteryPercent: Int,
    val isCharging: Boolean,
    val isInLowPowerMode: Boolean,
)

/**
 * Pure decision atom for the widget refresh interval. Maps a
 * [BatteryState] to a number of seconds until the next refresh, applied
 * by Android's `GlanceAppWidget` background worker and iOS's
 * `WKApplicationRefreshBackgroundTask`.
 *
 * Decision tree (worst-case wins):
 *  1. Low-Power-Mode (OS signal)      → 4x baseline
 *  2. Battery < 15% and not charging  → 4x baseline (critical)
 *  3. Battery 15..29% and not charging → 2x baseline (low)
 *  4. Otherwise                       → 1x baseline
 *
 * Final result is clamped to `[MIN_INTERVAL_SEC, MAX_INTERVAL_SEC]`:
 *  - the OS enforces a ~15-minute minimum on scheduled widget refresh.
 *  - a >24h refresh is worse UX than the small battery cost of a sooner
 *    refresh, so cap the upper end.
 *
 * Pure, side-effect-free, deterministic. Does not read system services;
 * the caller produces a [BatteryState] snapshot.
 */
object WidgetRefreshCadence {
    const val MIN_INTERVAL_SEC: Long = 15L * 60L
    const val MAX_INTERVAL_SEC: Long = 24L * 60L * 60L
    const val DEFAULT_BASELINE_SEC: Long = 30L * 60L

    fun intervalSeconds(
        state: BatteryState,
        baselineSeconds: Long = DEFAULT_BASELINE_SEC,
    ): Long {
        val clampedPercent = state.batteryPercent.coerceIn(0, 100)
        val multiplier =
            when {
                state.isInLowPowerMode -> 4.0
                !state.isCharging && clampedPercent < 15 -> 4.0
                !state.isCharging && clampedPercent < 30 -> 2.0
                else -> 1.0
            }
        val computed = (baselineSeconds.toDouble() * multiplier).toLong()
        return computed.coerceIn(MIN_INTERVAL_SEC, MAX_INTERVAL_SEC)
    }
}
