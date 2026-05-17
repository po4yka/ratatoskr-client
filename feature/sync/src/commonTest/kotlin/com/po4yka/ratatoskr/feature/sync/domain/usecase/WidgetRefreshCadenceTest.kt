package com.po4yka.ratatoskr.feature.sync.domain.usecase

import com.po4yka.ratatoskr.feature.sync.domain.usecase.WidgetRefreshCadence.Decision
import com.po4yka.ratatoskr.feature.sync.domain.usecase.WidgetRefreshCadence.DeferReason
import com.po4yka.ratatoskr.feature.sync.domain.usecase.WidgetRefreshCadence.NetworkClass
import com.po4yka.ratatoskr.feature.sync.domain.usecase.WidgetRefreshCadence.NetworkPolicy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class WidgetRefreshCadenceTest {
    private val now = Instant.fromEpochSeconds(1_700_000_000)

    @Test
    fun `healthy unmetered first run is allowed`() {
        val decision =
            WidgetRefreshCadence.decide(
                isBatteryLow = false,
                networkClass = NetworkClass.UNMETERED,
                lastSuccessAt = null,
                now = now,
                policy = NetworkPolicy.UNMETERED_ONLY,
            )

        assertEquals(Decision.Allow, decision)
    }

    @Test
    fun `battery-low has highest precedence — defers even when network and freshness are fine`() {
        // The widget refresh runs on a 30-minute cadence. We do not want a
        // background poll to push the user past the morning battery-warning
        // threshold; the WorkManager constraint is a soft hint, this is the
        // hard guarantee.
        val decision =
            WidgetRefreshCadence.decide(
                isBatteryLow = true,
                networkClass = NetworkClass.UNMETERED,
                lastSuccessAt = now - 2.hours,
                now = now,
                policy = NetworkPolicy.UNMETERED_ONLY,
            )

        assertEquals(Decision.Defer(DeferReason.BATTERY_LOW), decision)
    }

    @Test
    fun `offline defers regardless of policy — cannot refresh without network`() {
        // Precedence: BATTERY_LOW first, then OFFLINE. We test OFFLINE on its
        // own here so we know it's not being shadowed by the metered check.
        val decision =
            WidgetRefreshCadence.decide(
                isBatteryLow = false,
                networkClass = NetworkClass.OFFLINE,
                lastSuccessAt = now - 2.hours,
                now = now,
                policy = NetworkPolicy.ANY_NETWORK,
            )

        assertEquals(Decision.Defer(DeferReason.OFFLINE), decision)
    }

    @Test
    fun `metered network defers under UNMETERED_ONLY policy`() {
        // Mirror of WorkManager NetworkType.UNMETERED — but enforced in code so
        // an iOS BGAppRefreshTask that doesn't get a comparable OS constraint
        // can rely on the same gate.
        val decision =
            WidgetRefreshCadence.decide(
                isBatteryLow = false,
                networkClass = NetworkClass.METERED,
                lastSuccessAt = now - 2.hours,
                now = now,
                policy = NetworkPolicy.UNMETERED_ONLY,
            )

        assertEquals(Decision.Defer(DeferReason.METERED_BLOCKED), decision)
    }

    @Test
    fun `metered network is allowed under ANY_NETWORK policy`() {
        // The user can override to "Always refresh" — small payload, they
        // accept the data cost. Verifies the policy actually matters.
        val decision =
            WidgetRefreshCadence.decide(
                isBatteryLow = false,
                networkClass = NetworkClass.METERED,
                lastSuccessAt = now - 2.hours,
                now = now,
                policy = NetworkPolicy.ANY_NETWORK,
            )

        assertEquals(Decision.Allow, decision)
    }

    @Test
    fun `recently-refreshed defers — OS may fire the worker more often than the cadence allows`() {
        // WorkManager periodic intervals have flex windows; iOS BGTaskScheduler
        // batches with other work. Both can fire the worker more often than
        // our 30-minute cadence. The freshness guard is the source of truth
        // for "did we actually need to refresh".
        val decision =
            WidgetRefreshCadence.decide(
                isBatteryLow = false,
                networkClass = NetworkClass.UNMETERED,
                lastSuccessAt = now - 10.minutes,
                now = now,
                policy = NetworkPolicy.UNMETERED_ONLY,
            )

        assertEquals(Decision.Defer(DeferReason.RECENTLY_REFRESHED), decision)
    }

    @Test
    fun `boundary check — exactly at the cadence threshold is allowed`() {
        // The cadence is "minimum minutes between two refreshes". A run that
        // is exactly MIN_INTERVAL_MINUTES old has met the threshold; it must
        // run, otherwise the boundary case turns into perpetual deferral.
        val decision =
            WidgetRefreshCadence.decide(
                isBatteryLow = false,
                networkClass = NetworkClass.UNMETERED,
                lastSuccessAt = now - WidgetRefreshCadence.MIN_INTERVAL_MINUTES.minutes,
                now = now,
                policy = NetworkPolicy.UNMETERED_ONLY,
            )

        assertEquals(Decision.Allow, decision)
    }

    @Test
    fun `precedence — battery beats offline beats metered beats freshness`() {
        // All four defer conditions active at once; only BATTERY_LOW surfaces.
        // Pins the precedence so a refactor that swaps the order in decide()
        // fails this test.
        val decision =
            WidgetRefreshCadence.decide(
                isBatteryLow = true,
                networkClass = NetworkClass.OFFLINE,
                lastSuccessAt = now - 1.minutes,
                now = now,
                policy = NetworkPolicy.UNMETERED_ONLY,
            )

        assertEquals(Decision.Defer(DeferReason.BATTERY_LOW), decision)
    }

    @Test
    fun `null lastSuccessAt is treated as never-refreshed and bypasses freshness check`() {
        // A fresh install has lastSuccessAt = null. The freshness rule must
        // not falsely block the first refresh.
        val decision =
            WidgetRefreshCadence.decide(
                isBatteryLow = false,
                networkClass = NetworkClass.UNMETERED,
                lastSuccessAt = null,
                now = now,
                policy = NetworkPolicy.UNMETERED_ONLY,
            )

        assertEquals(Decision.Allow, decision)
    }

    @Test
    fun `negative duration since lastSuccessAt — clock skew — does not falsely defer`() {
        // If the OS clock rolled backwards (e.g. timezone resync), lastSuccessAt
        // can be in the future. The cadence should treat that as fresh-enough
        // → recently-refreshed defer, not as never-refreshed → run.
        val decision =
            WidgetRefreshCadence.decide(
                isBatteryLow = false,
                networkClass = NetworkClass.UNMETERED,
                lastSuccessAt = now + 5.minutes,
                now = now,
                policy = NetworkPolicy.UNMETERED_ONLY,
            )

        assertEquals(Decision.Defer(DeferReason.RECENTLY_REFRESHED), decision)
    }
}
