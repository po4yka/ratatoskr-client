package com.po4yka.ratatoskr.util.observability

/**
 * Pure decision atom for the Sentry-KMP `tracesSampleRate` value.
 *
 * Three inputs collapse to one number:
 *  - `consentGiven=false` → 0.0 (hard opt-out, no events leave the device).
 *    Wins over the build type so an unset / withdrawn consent in a debug
 *    build still sends nothing.
 *  - `isReleaseBuild=true`  → 0.1 (matches the wire-kermit-sentry plan;
 *    keeps the dashboard signal-to-noise sane and limits PII exposure).
 *  - `isReleaseBuild=false` → 1.0 (full capture during development).
 *
 * The output is always in `[0.0, 1.0]` — Sentry warns and clamps for
 * anything outside that range. The constants are exposed so the Settings
 * UI can display the current rate without recomputing the decision.
 *
 * Pure, side-effect-free, deterministic.
 */
object CrashReportSampleRate {
    const val DEBUG_RATE: Double = 1.0
    const val RELEASE_RATE: Double = 0.1
    const val DISABLED_RATE: Double = 0.0

    fun resolve(
        isReleaseBuild: Boolean,
        consentGiven: Boolean,
    ): Double {
        if (!consentGiven) return DISABLED_RATE
        return if (isReleaseBuild) RELEASE_RATE else DEBUG_RATE
    }
}
