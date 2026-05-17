package com.po4yka.ratatoskr.util.retry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PendingOpRetryPolicyTest {
    @Test
    fun `permanent failure gives up immediately — no retries`() {
        // 4xx-class server response (other than 429) means the request
        // will keep failing for the same reason. Don't waste battery
        // on retries.
        assertEquals(
            RetryDecision.GiveUp,
            PendingOpRetryPolicy.next(
                failedAttempts = 1,
                failure = PendingOpFailure.Permanent,
            ),
        )
    }

    @Test
    fun `permanent failure gives up even on the very first attempt`() {
        // failedAttempts = 0 is the initial call; pin that Permanent
        // short-circuits before the attempt-count check.
        assertEquals(
            RetryDecision.GiveUp,
            PendingOpRetryPolicy.next(
                failedAttempts = 0,
                failure = PendingOpFailure.Permanent,
            ),
        )
    }

    @Test
    fun `transient failure on first failure schedules a base-delay retry`() {
        val decision =
            PendingOpRetryPolicy.next(
                failedAttempts = 1,
                failure = PendingOpFailure.Transient,
            )
        assertEquals(
            RetryDecision.Retry(delayMillis = PendingOpRetryPolicy.BASE_DELAY_MS),
            decision,
        )
    }

    @Test
    fun `transient failure delay doubles with each subsequent attempt`() {
        // Pin the exponential-backoff curve: BASE, 2*BASE, 4*BASE, ...
        val base = PendingOpRetryPolicy.BASE_DELAY_MS
        assertEquals(
            RetryDecision.Retry(delayMillis = base * 2),
            PendingOpRetryPolicy.next(failedAttempts = 2, failure = PendingOpFailure.Transient),
        )
        assertEquals(
            RetryDecision.Retry(delayMillis = base * 4),
            PendingOpRetryPolicy.next(failedAttempts = 3, failure = PendingOpFailure.Transient),
        )
        assertEquals(
            RetryDecision.Retry(delayMillis = base * 8),
            PendingOpRetryPolicy.next(failedAttempts = 4, failure = PendingOpFailure.Transient),
        )
    }

    @Test
    fun `transient failure gives up at the max-attempts ceiling`() {
        // After MAX_ATTEMPTS failures, drop the pending op so the
        // queue makes forward progress on other items.
        assertEquals(
            RetryDecision.GiveUp,
            PendingOpRetryPolicy.next(
                failedAttempts = PendingOpRetryPolicy.MAX_ATTEMPTS,
                failure = PendingOpFailure.Transient,
            ),
        )
        // And anything beyond.
        assertEquals(
            RetryDecision.GiveUp,
            PendingOpRetryPolicy.next(
                failedAttempts = PendingOpRetryPolicy.MAX_ATTEMPTS + 5,
                failure = PendingOpFailure.Transient,
            ),
        )
    }

    @Test
    fun `rate-limited failure uses a flat longer delay regardless of attempt`() {
        // Server explicitly told us to back off (429). The exponential
        // curve is the wrong tool here — a flat one-minute delay
        // matches the typical Retry-After header values.
        val expected = RetryDecision.Retry(delayMillis = PendingOpRetryPolicy.RATE_LIMITED_DELAY_MS)
        assertEquals(
            expected,
            PendingOpRetryPolicy.next(failedAttempts = 1, failure = PendingOpFailure.RateLimited),
        )
        assertEquals(
            expected,
            PendingOpRetryPolicy.next(failedAttempts = 3, failure = PendingOpFailure.RateLimited),
        )
    }

    @Test
    fun `rate-limited respects the max-attempts ceiling`() {
        // Even rate-limited retries give up eventually — otherwise a
        // misconfigured server could keep a pending op pinned forever.
        assertEquals(
            RetryDecision.GiveUp,
            PendingOpRetryPolicy.next(
                failedAttempts = PendingOpRetryPolicy.MAX_ATTEMPTS,
                failure = PendingOpFailure.RateLimited,
            ),
        )
    }

    @Test
    fun `transient delay is clamped to the configured maximum`() {
        // Doubling can grow unbounded; cap at MAX_DELAY_MS so a
        // pending op doesn't sit silent for hours.
        val decision =
            PendingOpRetryPolicy.next(
                failedAttempts = 30,
                failure = PendingOpFailure.Transient,
            )
        // failedAttempts >= MAX_ATTEMPTS → GiveUp; pin that path
        // first since MAX_ATTEMPTS is small.
        assertEquals(RetryDecision.GiveUp, decision)

        // Force the curve into the clamp by raising the failed count
        // but staying under the ceiling — confirm the delay never
        // exceeds MAX_DELAY_MS.
        for (attempt in 1..PendingOpRetryPolicy.MAX_ATTEMPTS - 1) {
            val d =
                PendingOpRetryPolicy.next(
                    failedAttempts = attempt,
                    failure = PendingOpFailure.Transient,
                )
            assertTrue(d is RetryDecision.Retry, "expected Retry at attempt=$attempt")
            assertTrue(
                d.delayMillis <= PendingOpRetryPolicy.MAX_DELAY_MS,
                "delay ${d.delayMillis} at attempt=$attempt exceeds MAX_DELAY_MS=${PendingOpRetryPolicy.MAX_DELAY_MS}",
            )
        }
    }

    @Test
    fun `negative attempts are treated as zero — defensive`() {
        // A buggy caller passing -1 should not produce a negative
        // delay or a left-shift on a negative integer.
        val a =
            PendingOpRetryPolicy.next(
                failedAttempts = -1,
                failure = PendingOpFailure.Transient,
            )
        val b =
            PendingOpRetryPolicy.next(
                failedAttempts = 0,
                failure = PendingOpFailure.Transient,
            )
        assertEquals(a, b)
    }

    @Test
    fun `decision is deterministic — same inputs map to same output`() {
        val a =
            PendingOpRetryPolicy.next(
                failedAttempts = 2,
                failure = PendingOpFailure.Transient,
            )
        val b =
            PendingOpRetryPolicy.next(
                failedAttempts = 2,
                failure = PendingOpFailure.Transient,
            )
        assertEquals(a, b)
    }
}
