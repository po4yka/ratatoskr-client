package com.po4yka.ratatoskr.util.typography

import kotlin.test.Test
import kotlin.test.assertEquals

class SpScaleBucketTest {
    @Test
    fun `default scale of 1_0 — Default bucket`() {
        assertEquals(SpScaleBucket.Default, SpScaleBucket.bucketFor(1.0f))
    }

    @Test
    fun `compact scale below 0_9 — Compact`() {
        assertEquals(SpScaleBucket.Compact, SpScaleBucket.bucketFor(0.85f))
        assertEquals(SpScaleBucket.Compact, SpScaleBucket.bucketFor(0.5f))
    }

    @Test
    fun `large scale 1_15 to 1_3 — Large`() {
        assertEquals(SpScaleBucket.Large, SpScaleBucket.bucketFor(1.15f))
        assertEquals(SpScaleBucket.Large, SpScaleBucket.bucketFor(1.3f))
    }

    @Test
    fun `extra-large scale above 1_3 — ExtraLarge`() {
        // Accessibility large-font users typically land above 1.3x;
        // pin the threshold so the Frost layout switches to its
        // tight-bound chrome at the same scale.
        assertEquals(SpScaleBucket.ExtraLarge, SpScaleBucket.bucketFor(1.5f))
        assertEquals(SpScaleBucket.ExtraLarge, SpScaleBucket.bucketFor(2.5f))
    }

    @Test
    fun `boundary at 0_9 stays Default — inclusive lower edge`() {
        // Pin the boundary so a refactor doesn't quietly move it.
        assertEquals(SpScaleBucket.Default, SpScaleBucket.bucketFor(0.9f))
        assertEquals(SpScaleBucket.Default, SpScaleBucket.bucketFor(1.14f))
    }

    @Test
    fun `boundary at 1_15 enters Large — inclusive lower edge`() {
        assertEquals(SpScaleBucket.Large, SpScaleBucket.bucketFor(1.15f))
    }

    @Test
    fun `negative scale — Compact, defensive`() {
        // A negative scale from a misbehaving density bridge should
        // collapse to the tightest bucket rather than throwing.
        assertEquals(SpScaleBucket.Compact, SpScaleBucket.bucketFor(-1.0f))
    }

    @Test
    fun `zero scale — Compact`() {
        assertEquals(SpScaleBucket.Compact, SpScaleBucket.bucketFor(0.0f))
    }

    @Test
    fun `extreme large scale — ExtraLarge boundary`() {
        // No upper clamp; pin that even runaway scales fall in the
        // top bucket rather than wrapping to a smaller one.
        assertEquals(SpScaleBucket.ExtraLarge, SpScaleBucket.bucketFor(10.0f))
        assertEquals(SpScaleBucket.ExtraLarge, SpScaleBucket.bucketFor(Float.MAX_VALUE))
    }

    @Test
    fun `bucketing is deterministic`() {
        val a = SpScaleBucket.bucketFor(1.2f)
        val b = SpScaleBucket.bucketFor(1.2f)
        assertEquals(a, b)
    }
}
