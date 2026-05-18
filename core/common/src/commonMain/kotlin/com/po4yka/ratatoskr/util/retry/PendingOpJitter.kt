package com.po4yka.ratatoskr.util.retry

import kotlin.math.absoluteValue

/**
 * Pure jitter computation for retry scheduling. Produces a deterministic
 * offset in `[0, baseDelayMs * ratio]` from a caller-supplied seed.
 *
 * The atom does **not** generate the seed itself so tests stay reproducible
 * and so callers can choose their own source (a fresh `Random.nextLong()`
 * in production; a fixed value in tests). The deterministic hash converts
 * the seed into a value within the jitter band without depending on the
 * JVM/JS/native `Random` implementation.
 *
 * Why this exists: [PendingOpRetryPolicy] deliberately omits jitter from
 * its own decision so that the policy stays testable; the caller composes
 * this atom at scheduling time, e.g.
 * `val total = decision.delayMillis + PendingOpJitter.offset(decision.delayMillis, seed)`.
 *
 * Defensive: `baseDelayMs <= 0` → `0`. `ratio <= 0` → `0`. `ratio > 1.0`
 * is clamped to `1.0` so the offset never exceeds the base delay (which
 * would otherwise double the scheduled wait under pathological inputs).
 *
 * Pure, side-effect-free, deterministic.
 */
object PendingOpJitter {
    const val DEFAULT_JITTER_RATIO: Double = 0.25

    fun offset(
        baseDelayMs: Long,
        seed: Long,
        ratio: Double = DEFAULT_JITTER_RATIO,
    ): Long {
        if (baseDelayMs <= 0) return 0L
        val safeRatio = ratio.coerceIn(0.0, 1.0)
        if (safeRatio == 0.0) return 0L
        val band = (baseDelayMs * safeRatio).toLong()
        if (band <= 0) return 0L
        val normalized = (seed.hashed()).absoluteValue
        return normalized % (band + 1)
    }

    private fun Long.hashed(): Long {
        // Stable 64-bit mix (xorshift-style) so distinct seeds spread
        // across the band without depending on `Random`'s impl.
        var x = this
        x = (x xor (x ushr 30)) * -0x40a7b892e9d3a049L
        x = (x xor (x ushr 27)) * -0x6b2fb644ecceee15L
        x = x xor (x ushr 31)
        return x
    }
}
