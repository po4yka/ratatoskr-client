package com.po4yka.ratatoskr.util.widget

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WidgetRefreshSchedulerTest {
    @Test
    fun `next-fire is now plus the cadence-derived interval`() {
        val now = 1_700_000_000_000L
        val state =
            BatteryState(
                batteryPercent = 80,
                isCharging = true,
                isInLowPowerMode = false,
            )
        val baseline = 1800L
        val next =
            WidgetRefreshScheduler.nextFireEpochMillis(
                state = state,
                nowEpochMillis = now,
                baselineSeconds = baseline,
            )
        assertEquals(now + baseline * 1000L, next)
    }

    @Test
    fun `low-power mode pushes the next-fire 4x further out`() {
        val now = 1_700_000_000_000L
        val state =
            BatteryState(
                batteryPercent = 80,
                isCharging = true,
                isInLowPowerMode = true,
            )
        val baseline = 1800L
        val next =
            WidgetRefreshScheduler.nextFireEpochMillis(
                state = state,
                nowEpochMillis = now,
                baselineSeconds = baseline,
            )
        assertEquals(now + 4 * baseline * 1000L, next)
    }

    @Test
    fun `next-fire is strictly in the future`() {
        // Even at the MIN_INTERVAL clamp, the result must be after now
        // — never schedule a refresh in the past.
        val now = 1_700_000_000_000L
        val state =
            BatteryState(
                batteryPercent = 100,
                isCharging = true,
                isInLowPowerMode = false,
            )
        val next =
            WidgetRefreshScheduler.nextFireEpochMillis(
                state = state,
                nowEpochMillis = now,
                baselineSeconds = 60,
            )
        assertTrue(next > now, "next-fire $next must be after now $now")
    }

    @Test
    fun `negative now is passed through — caller is responsible for the clock`() {
        // The atom doesn't moralize about negative epoch; that's a
        // clock contract violation upstream. But it should not crash.
        val state =
            BatteryState(
                batteryPercent = 80,
                isCharging = true,
                isInLowPowerMode = false,
            )
        val next =
            WidgetRefreshScheduler.nextFireEpochMillis(
                state = state,
                nowEpochMillis = -1,
                baselineSeconds = 1800,
            )
        // Output is -1 + computed-delay-in-millis; just verify the
        // shape (no exception) and that the offset matches the cadence
        // atom's decision.
        assertEquals(-1L + 1800L * 1000L, next)
    }

    @Test
    fun `decision is deterministic`() {
        val now = 1_700_000_000_000L
        val state =
            BatteryState(
                batteryPercent = 25,
                isCharging = false,
                isInLowPowerMode = false,
            )
        val a =
            WidgetRefreshScheduler.nextFireEpochMillis(
                state = state,
                nowEpochMillis = now,
            )
        val b =
            WidgetRefreshScheduler.nextFireEpochMillis(
                state = state,
                nowEpochMillis = now,
            )
        assertEquals(a, b)
    }

    @Test
    fun `next-fire never overflows when now is far in the future`() {
        // Pin Long.MAX-bound safety: even with a far-future clock the
        // result shouldn't wrap.
        val now = Long.MAX_VALUE - 1L
        val state =
            BatteryState(
                batteryPercent = 80,
                isCharging = true,
                isInLowPowerMode = false,
            )
        val next =
            WidgetRefreshScheduler.nextFireEpochMillis(
                state = state,
                nowEpochMillis = now,
                baselineSeconds = 1800,
            )
        // Saturated at Long.MAX_VALUE rather than wrapping negative.
        assertTrue(next > 0, "next-fire $next wrapped negative (overflow)")
    }
}
