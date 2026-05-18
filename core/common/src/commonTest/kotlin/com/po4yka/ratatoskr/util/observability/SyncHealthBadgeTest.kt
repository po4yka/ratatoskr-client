package com.po4yka.ratatoskr.util.observability

import kotlin.test.Test
import kotlin.test.assertEquals

class SyncHealthBadgeTest {
    @Test
    fun `no failures + recent sync + zero pending — Healthy`() {
        // Happy path: just synced within the freshness window, no
        // pending ops queued, no errors -> green badge.
        assertEquals(
            SyncHealthBadge.Status.Healthy,
            SyncHealthBadge.classify(
                anyFailure = false,
                lastSyncAgeSeconds = 30,
                pendingOpDepth = 0,
            ),
        )
    }

    @Test
    fun `no failures + stale sync — Degraded`() {
        // Stale data is degraded, not failing — the user might just
        // be offline. Surface yellow so they know but don't alarm.
        assertEquals(
            SyncHealthBadge.Status.Degraded,
            SyncHealthBadge.classify(
                anyFailure = false,
                lastSyncAgeSeconds = SyncHealthBadge.STALE_AFTER_SECONDS + 5,
                pendingOpDepth = 0,
            ),
        )
    }

    @Test
    fun `any failure — Failing — wins over staleness or pending depth`() {
        // The applier reported at least one categorized failure;
        // that's the strongest signal — red regardless of other flags.
        assertEquals(
            SyncHealthBadge.Status.Failing,
            SyncHealthBadge.classify(
                anyFailure = true,
                lastSyncAgeSeconds = 10,
                pendingOpDepth = 0,
            ),
        )
        assertEquals(
            SyncHealthBadge.Status.Failing,
            SyncHealthBadge.classify(
                anyFailure = true,
                lastSyncAgeSeconds = SyncHealthBadge.STALE_AFTER_SECONDS + 100,
                pendingOpDepth = 50,
            ),
        )
    }

    @Test
    fun `pending ops without failure — Degraded`() {
        // The user took an offline action waiting to drain; not red,
        // but yellow so they know there's work outstanding.
        assertEquals(
            SyncHealthBadge.Status.Degraded,
            SyncHealthBadge.classify(
                anyFailure = false,
                lastSyncAgeSeconds = 10,
                pendingOpDepth = 3,
            ),
        )
    }

    @Test
    fun `staleness boundary — exactly at the threshold stays Healthy`() {
        // Pin the inclusive edge: ageSeconds == STALE_AFTER means
        // still within the window. The strict inequality matters
        // because the user-visible relative-time bucket renders
        // "Just now" up to the same threshold.
        assertEquals(
            SyncHealthBadge.Status.Healthy,
            SyncHealthBadge.classify(
                anyFailure = false,
                lastSyncAgeSeconds = SyncHealthBadge.STALE_AFTER_SECONDS,
                pendingOpDepth = 0,
            ),
        )
        // One second past -> Degraded.
        assertEquals(
            SyncHealthBadge.Status.Degraded,
            SyncHealthBadge.classify(
                anyFailure = false,
                lastSyncAgeSeconds = SyncHealthBadge.STALE_AFTER_SECONDS + 1,
                pendingOpDepth = 0,
            ),
        )
    }

    @Test
    fun `negative age clamps to zero — defensive against clock skew`() {
        // A negative age (clock skew during sync completion) should
        // never produce Failing — clamp and treat as Healthy.
        assertEquals(
            SyncHealthBadge.Status.Healthy,
            SyncHealthBadge.classify(
                anyFailure = false,
                lastSyncAgeSeconds = -10,
                pendingOpDepth = 0,
            ),
        )
    }

    @Test
    fun `negative pending depth treated as zero — defensive`() {
        // A buggy SQL count can briefly return -1. Don't classify a
        // healthy sync as Degraded because of arithmetic noise.
        assertEquals(
            SyncHealthBadge.Status.Healthy,
            SyncHealthBadge.classify(
                anyFailure = false,
                lastSyncAgeSeconds = 10,
                pendingOpDepth = -5,
            ),
        )
    }

    @Test
    fun `classification is deterministic`() {
        val a =
            SyncHealthBadge.classify(
                anyFailure = false,
                lastSyncAgeSeconds = 10,
                pendingOpDepth = 0,
            )
        val b =
            SyncHealthBadge.classify(
                anyFailure = false,
                lastSyncAgeSeconds = 10,
                pendingOpDepth = 0,
            )
        assertEquals(a, b)
    }
}
