package com.po4yka.ratatoskr.util.observability

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CrashReportSampleRateTest {
    @Test
    fun `release build with consent samples at the release rate`() {
        // Per the wire-kermit-sentry plan: release builds sample at 0.1
        // so dashboards aren't flooded with low-signal duplicate crashes.
        assertEquals(
            CrashReportSampleRate.RELEASE_RATE,
            CrashReportSampleRate.resolve(isReleaseBuild = true, consentGiven = true),
        )
    }

    @Test
    fun `debug build with consent samples every event`() {
        // In debug, developers want to see every event for diagnosis.
        assertEquals(
            CrashReportSampleRate.DEBUG_RATE,
            CrashReportSampleRate.resolve(isReleaseBuild = false, consentGiven = true),
        )
    }

    @Test
    fun `consent withdrawn — release — sample rate is zero`() {
        // User opt-out is the hard gate. No events leave the device
        // regardless of build type.
        assertEquals(
            CrashReportSampleRate.DISABLED_RATE,
            CrashReportSampleRate.resolve(isReleaseBuild = true, consentGiven = false),
        )
    }

    @Test
    fun `consent withdrawn — debug — sample rate is zero`() {
        // The debug-build full-capture rate must not override consent.
        assertEquals(
            CrashReportSampleRate.DISABLED_RATE,
            CrashReportSampleRate.resolve(isReleaseBuild = false, consentGiven = false),
        )
    }

    @Test
    fun `disabled rate is exactly 0_0 — Sentry treats anything _gt 0 as send`() {
        // Pin the constant so a future refactor can't accidentally
        // set DISABLED_RATE = 0.001 (which Sentry would interpret as
        // "send 0.1% of events").
        assertEquals(0.0, CrashReportSampleRate.DISABLED_RATE)
    }

    @Test
    fun `release rate is exactly 0_1 — matches the wire-kermit-sentry contract`() {
        assertEquals(0.1, CrashReportSampleRate.RELEASE_RATE)
    }

    @Test
    fun `debug rate is exactly 1_0`() {
        assertEquals(1.0, CrashReportSampleRate.DEBUG_RATE)
    }

    @Test
    fun `every resolution lands in the valid Sentry range`() {
        // Sentry's tracesSampleRate clamps to [0.0, 1.0]; values
        // outside cause SDK warnings. Pin the invariant across all
        // four input combinations.
        for (release in listOf(true, false)) {
            for (consent in listOf(true, false)) {
                val rate =
                    CrashReportSampleRate.resolve(
                        isReleaseBuild = release,
                        consentGiven = consent,
                    )
                assertTrue(
                    rate in 0.0..1.0,
                    "resolve(release=$release, consent=$consent) = $rate is outside [0,1]",
                )
            }
        }
    }

    @Test
    fun `release rate at or below 0_1 — privacy + dashboard health gate`() {
        // The Kermit+Sentry issue specifies "sample rate ≤ 0.1 in
        // release". If a future change raises this above 0.1, the
        // test fails so the change is forced through review.
        assertTrue(
            CrashReportSampleRate.RELEASE_RATE <= 0.1,
            "RELEASE_RATE=${CrashReportSampleRate.RELEASE_RATE} exceeds the 0.1 ceiling",
        )
    }
}
