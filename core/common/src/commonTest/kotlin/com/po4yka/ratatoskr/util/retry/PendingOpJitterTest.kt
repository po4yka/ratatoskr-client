package com.po4yka.ratatoskr.util.retry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PendingOpJitterTest {
    @Test
    fun `zero base delay yields zero jitter`() {
        assertEquals(0L, PendingOpJitter.offset(baseDelayMs = 0, seed = 12345))
    }

    @Test
    fun `non-zero base delay yields a value within the configured jitter band`() {
        // Pin the invariant: the jitter offset never exceeds the
        // configured ratio of the base delay (default 25%). Without
        // this guard, an unbounded jitter could double the wait,
        // breaking PendingOpRetryPolicy's caller contract.
        val base = 4_000L
        val band = (base * PendingOpJitter.DEFAULT_JITTER_RATIO).toLong()
        for (seed in 0L..100L) {
            val jitter = PendingOpJitter.offset(baseDelayMs = base, seed = seed)
            assertTrue(
                jitter in 0..band,
                "offset(base=$base, seed=$seed) = $jitter outside [0, $band]",
            )
        }
    }

    @Test
    fun `same seed produces same jitter — deterministic`() {
        val a = PendingOpJitter.offset(baseDelayMs = 4_000L, seed = 42)
        val b = PendingOpJitter.offset(baseDelayMs = 4_000L, seed = 42)
        assertEquals(a, b)
    }

    @Test
    fun `different seeds produce distributed jitter — pin variance`() {
        // Pin that distinct seeds don't all collapse to the same
        // output (which would defeat the thundering-herd defense).
        val outputs = (0L..63L).map { PendingOpJitter.offset(baseDelayMs = 4_000L, seed = it) }
        val distinctCount = outputs.toSet().size
        // With 64 seeds and a band of 1000ms, expect distinctness >= 16
        // to demonstrate variance without flake.
        assertTrue(
            distinctCount >= 16,
            "only $distinctCount distinct jitter values from 64 seeds — too clustered",
        )
    }

    @Test
    fun `negative base delay returns zero — defensive`() {
        // No legitimate caller passes a negative base, but we
        // shouldn't multiply a negative through the jitter math
        // and produce negative-millisecond delays.
        assertEquals(0L, PendingOpJitter.offset(baseDelayMs = -1, seed = 42))
        assertEquals(0L, PendingOpJitter.offset(baseDelayMs = Long.MIN_VALUE, seed = 42))
    }

    @Test
    fun `custom jitter ratio is respected`() {
        // A caller can ask for tighter or looser jitter via the
        // ratio override. Pin both bands.
        val base = 4_000L
        val tight = PendingOpJitter.offset(baseDelayMs = base, seed = 1, ratio = 0.1)
        val loose = PendingOpJitter.offset(baseDelayMs = base, seed = 1, ratio = 0.5)
        val tightBand = (base * 0.1).toLong()
        val looseBand = (base * 0.5).toLong()
        assertTrue(tight in 0..tightBand, "tight jitter $tight outside [0, $tightBand]")
        assertTrue(loose in 0..looseBand, "loose jitter $loose outside [0, $looseBand]")
    }

    @Test
    fun `ratio above one is clamped`() {
        // Even with a runaway ratio, the offset must not exceed the
        // base delay (otherwise the jittered wait could be 2x base).
        val base = 4_000L
        val jitter = PendingOpJitter.offset(baseDelayMs = base, seed = 1, ratio = 5.0)
        assertTrue(jitter in 0..base, "jitter $jitter exceeds the base $base")
    }

    @Test
    fun `negative ratio is clamped to zero — defensive`() {
        assertEquals(
            0L,
            PendingOpJitter.offset(baseDelayMs = 4_000L, seed = 1, ratio = -0.5),
        )
    }
}
