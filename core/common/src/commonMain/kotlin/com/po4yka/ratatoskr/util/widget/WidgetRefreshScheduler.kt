package com.po4yka.ratatoskr.util.widget

/**
 * Pure scheduler that turns the per-cadence interval from
 * [WidgetRefreshCadence] into an absolute epoch-millis deadline the
 * platform schedulers can dispatch:
 *  - Android Glance `GlanceAppWidget` background worker `enqueueUniqueWork`
 *  - iOS `WidgetCenter.shared.getCurrentConfigurations` reloadTimelines
 *    expiry hint
 *
 * Composes [WidgetRefreshCadence.intervalSeconds] with the caller-supplied
 * clock; the atom itself does not read system time so it stays
 * deterministic and testable.
 *
 * Overflow guard: if `now + interval` would exceed `Long.MAX_VALUE`
 * (impossible in practice but cheap to defend), saturate at
 * `Long.MAX_VALUE` so the result never wraps negative.
 *
 * Pure, side-effect-free, deterministic.
 */
object WidgetRefreshScheduler {
    fun nextFireEpochMillis(
        state: BatteryState,
        nowEpochMillis: Long,
        baselineSeconds: Long = WidgetRefreshCadence.DEFAULT_BASELINE_SEC,
    ): Long {
        val intervalSec =
            WidgetRefreshCadence.intervalSeconds(
                state = state,
                baselineSeconds = baselineSeconds,
            )
        val intervalMs = intervalSec * 1000L
        val sum = nowEpochMillis + intervalMs
        // Overflow guard: only relevant when both operands are positive
        // and the naive addition wrapped negative. Negative `now`
        // (caller's clock contract violation) is passed straight
        // through — the atom doesn't moralize.
        return if (nowEpochMillis > 0 && intervalMs > 0 && sum < 0) {
            Long.MAX_VALUE
        } else {
            sum
        }
    }
}
