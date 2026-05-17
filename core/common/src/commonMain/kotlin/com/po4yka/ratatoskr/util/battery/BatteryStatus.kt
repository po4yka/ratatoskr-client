package com.po4yka.ratatoskr.util.battery

/**
 * Lightweight battery-state probe consumed by background-refresh use cases. The full
 * Android `BatteryManager` / iOS `UIDevice.batteryState` APIs are platform-specific and
 * heavier than the use cases need; this interface exposes only the one signal they
 * care about — "is the device low enough that a background top-up should be skipped".
 *
 * Threshold semantics:
 *  - Android: `BATTERY_STATUS_DISCHARGING` && `level / scale < 0.15`, **OR**
 *    `Intent.ACTION_BATTERY_LOW` was last seen with no `BATTERY_OKAY` since.
 *  - iOS: `UIDevice.current.batteryLevel < 0.20` && `batteryState != .charging`.
 *  - Desktop: always false (development target only — no battery story).
 *
 * Implementations are expected to be cheap to call (no I/O) so the use case can probe
 * on every invocation without latency.
 */
interface BatteryStatus {
    /**
     * Returns `true` when the device is below the conservative
     * battery-refresh threshold, signalling that callers should skip
     * non-essential background work.
     */
    fun isLow(): Boolean
}
