package com.po4yka.ratatoskr.util.widget

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WidgetRefreshCadenceTest {
    @Test
    fun `charged plus normal battery — baseline cadence`() {
        // Plugged in on wall power with a healthy battery: refresh
        // exactly at the configured baseline.
        val interval =
            WidgetRefreshCadence.intervalSeconds(
                state =
                    BatteryState(
                        batteryPercent = 80,
                        isCharging = true,
                        isInLowPowerMode = false,
                    ),
                baselineSeconds = 1800,
            )
        assertEquals(1800L, interval)
    }

    @Test
    fun `low-power mode quadruples the baseline regardless of charge`() {
        // OS-level Low Power Mode is the strongest signal: respect
        // the user's intent and back off even if they happen to be
        // plugged in. 4x is the cap from the wire-widget-refresh
        // plan.
        val interval =
            WidgetRefreshCadence.intervalSeconds(
                state =
                    BatteryState(
                        batteryPercent = 90,
                        isCharging = true,
                        isInLowPowerMode = true,
                    ),
                baselineSeconds = 1800,
            )
        assertEquals(7200L, interval)
    }

    @Test
    fun `critical battery not charging — quadruple the baseline`() {
        // Below 15% without charger: aggressive backoff so the widget
        // refresh isn't the reason the device dies.
        val interval =
            WidgetRefreshCadence.intervalSeconds(
                state =
                    BatteryState(
                        batteryPercent = 10,
                        isCharging = false,
                        isInLowPowerMode = false,
                    ),
                baselineSeconds = 1800,
            )
        assertEquals(7200L, interval)
    }

    @Test
    fun `low battery not charging — double the baseline`() {
        // 15-29% without charger: moderate backoff.
        val interval =
            WidgetRefreshCadence.intervalSeconds(
                state =
                    BatteryState(
                        batteryPercent = 25,
                        isCharging = false,
                        isInLowPowerMode = false,
                    ),
                baselineSeconds = 1800,
            )
        assertEquals(3600L, interval)
    }

    @Test
    fun `normal battery not charging — baseline cadence`() {
        // 30%+ on battery: still refresh at baseline. The device
        // can afford it.
        val interval =
            WidgetRefreshCadence.intervalSeconds(
                state =
                    BatteryState(
                        batteryPercent = 60,
                        isCharging = false,
                        isInLowPowerMode = false,
                    ),
                baselineSeconds = 1800,
            )
        assertEquals(1800L, interval)
    }

    @Test
    fun `result is clamped to the minimum interval`() {
        // The OS enforces a 15-minute minimum on scheduled widget
        // refreshes (Android Glance / iOS WidgetKit). Refuse to
        // schedule sooner.
        val interval =
            WidgetRefreshCadence.intervalSeconds(
                state =
                    BatteryState(
                        batteryPercent = 100,
                        isCharging = true,
                        isInLowPowerMode = false,
                    ),
                baselineSeconds = 60,
            )
        assertTrue(
            interval >= WidgetRefreshCadence.MIN_INTERVAL_SEC,
            "interval=$interval below MIN=${WidgetRefreshCadence.MIN_INTERVAL_SEC}",
        )
    }

    @Test
    fun `result is clamped to the maximum interval`() {
        // Don't schedule a refresh more than 24h out — a stale
        // widget for longer than that is a worse experience than
        // a few extra battery drain points.
        val interval =
            WidgetRefreshCadence.intervalSeconds(
                state =
                    BatteryState(
                        batteryPercent = 5,
                        isCharging = false,
                        isInLowPowerMode = true,
                    ),
                baselineSeconds = WidgetRefreshCadence.MAX_INTERVAL_SEC,
            )
        assertEquals(WidgetRefreshCadence.MAX_INTERVAL_SEC, interval)
    }

    @Test
    fun `low-power mode overrides charging — worst-case wins`() {
        // A plugged-in device in Low Power Mode is still expressing
        // intent to conserve. Honor the LPM signal over the
        // charging signal.
        val lpmCharging =
            WidgetRefreshCadence.intervalSeconds(
                state =
                    BatteryState(
                        batteryPercent = 90,
                        isCharging = true,
                        isInLowPowerMode = true,
                    ),
                baselineSeconds = 1800,
            )
        val plainCharging =
            WidgetRefreshCadence.intervalSeconds(
                state =
                    BatteryState(
                        batteryPercent = 90,
                        isCharging = true,
                        isInLowPowerMode = false,
                    ),
                baselineSeconds = 1800,
            )
        assertTrue(lpmCharging > plainCharging, "LPM=$lpmCharging should exceed plain charging=$plainCharging")
    }

    @Test
    fun `battery percent below zero is treated as critical`() {
        // Some Android devices briefly report negative percent during
        // boot. Be defensive: treat as critical (worst-case backoff).
        val interval =
            WidgetRefreshCadence.intervalSeconds(
                state =
                    BatteryState(
                        batteryPercent = -10,
                        isCharging = false,
                        isInLowPowerMode = false,
                    ),
                baselineSeconds = 1800,
            )
        assertEquals(7200L, interval)
    }

    @Test
    fun `battery percent above 100 is clamped to 100`() {
        // Defensive: some BatteryManager polls return 101 briefly.
        val interval =
            WidgetRefreshCadence.intervalSeconds(
                state =
                    BatteryState(
                        batteryPercent = 150,
                        isCharging = true,
                        isInLowPowerMode = false,
                    ),
                baselineSeconds = 1800,
            )
        // Same as a 100% charged state.
        assertEquals(1800L, interval)
    }

    @Test
    fun `decision is deterministic — same state maps to same interval`() {
        val state = BatteryState(batteryPercent = 25, isCharging = false, isInLowPowerMode = false)
        val a = WidgetRefreshCadence.intervalSeconds(state)
        val b = WidgetRefreshCadence.intervalSeconds(state)
        assertEquals(a, b)
    }
}
