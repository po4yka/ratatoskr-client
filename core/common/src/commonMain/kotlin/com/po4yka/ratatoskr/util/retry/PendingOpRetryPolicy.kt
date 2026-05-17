package com.po4yka.ratatoskr.util.retry

/**
 * Classification of a pending-op failure so [PendingOpRetryPolicy] can
 * pick the right backoff curve. Mapped from HTTP status codes / exception
 * kinds at the call site:
 *  - [Transient]   — network blip, 5xx, IO timeout. Exponential backoff.
 *  - [RateLimited] — 429 with or without Retry-After. Flat longer delay.
 *  - [Permanent]   — 4xx (non-429). Give up; the op will keep failing.
 */
enum class PendingOpFailure {
    Transient,
    RateLimited,
    Permanent,
}

/**
 * Outcome of a [PendingOpRetryPolicy.next] decision. Either schedule a
 * retry after `delayMillis`, or give up and drop the pending op.
 */
sealed interface RetryDecision {
    data class Retry(val delayMillis: Long) : RetryDecision

    data object GiveUp : RetryDecision
}

/**
 * Pure retry-policy decision atom for the sync pending-op drain (and
 * any other queue that benefits from the same shape).
 *
 * Inputs:
 *  - `failedAttempts` — the number of times the op has already failed,
 *    counting the failure that just occurred (so `1` on the first
 *    failure, `2` on the second).
 *  - `failure` — [PendingOpFailure] classification from the call site.
 *
 * Decision:
 *  - [PendingOpFailure.Permanent] → [RetryDecision.GiveUp] immediately,
 *    regardless of `failedAttempts`. The request will keep failing for
 *    the same reason; retrying wastes battery and queue depth.
 *  - `failedAttempts >= MAX_ATTEMPTS` → [RetryDecision.GiveUp] so the
 *    queue makes forward progress on other items.
 *  - [PendingOpFailure.RateLimited] → flat [RATE_LIMITED_DELAY_MS] delay.
 *    Matches typical `Retry-After` header values; the exponential curve
 *    is the wrong tool when the server has told us the right interval.
 *  - [PendingOpFailure.Transient] → exponential backoff:
 *    `BASE_DELAY_MS * 2^(failedAttempts - 1)`, clamped to `MAX_DELAY_MS`.
 *
 * Defensive: a negative `failedAttempts` (caller bug) is treated as `0`
 * so the left-shift can't underflow.
 *
 * Pure, side-effect-free, deterministic. Jitter is intentionally **not**
 * applied here — when a caller needs jitter it adds it at scheduling
 * time so the policy stays testable.
 */
object PendingOpRetryPolicy {
    const val MAX_ATTEMPTS: Int = 5
    const val BASE_DELAY_MS: Long = 2_000L
    const val MAX_DELAY_MS: Long = 5L * 60L * 1000L
    const val RATE_LIMITED_DELAY_MS: Long = 60_000L

    fun next(
        failedAttempts: Int,
        failure: PendingOpFailure,
    ): RetryDecision {
        if (failure == PendingOpFailure.Permanent) return RetryDecision.GiveUp
        if (failedAttempts >= MAX_ATTEMPTS) return RetryDecision.GiveUp

        val delay =
            when (failure) {
                PendingOpFailure.RateLimited -> RATE_LIMITED_DELAY_MS
                PendingOpFailure.Transient -> exponentialBackoff(failedAttempts)
                PendingOpFailure.Permanent -> RATE_LIMITED_DELAY_MS
            }
        return RetryDecision.Retry(delayMillis = delay)
    }

    private fun exponentialBackoff(failedAttempts: Int): Long {
        val safe = failedAttempts.coerceAtLeast(0)
        val shift = (safe - 1).coerceAtLeast(0)
        val capped = shift.coerceAtMost(BACKOFF_SHIFT_CEILING)
        val multiplier = 1L shl capped
        val candidate = BASE_DELAY_MS * multiplier
        return candidate.coerceAtMost(MAX_DELAY_MS)
    }

    private const val BACKOFF_SHIFT_CEILING: Int = 30
}
