package com.po4yka.ratatoskr.util.range

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OffsetRangeClipperTest {
    @Test
    fun `valid range passes through unchanged`() {
        // Happy path: a highlight at offsets [5..15) on a 100-char body
        // is returned verbatim — no clipping needed.
        assertEquals(
            OffsetRangeClipper.Range(5, 15),
            OffsetRangeClipper.clip(start = 5, end = 15, bodyLength = 100),
        )
    }

    @Test
    fun `range is half-open — end is exclusive of the body content`() {
        // Convention pin: Range(0, 10) means offsets 0 through 9 inclusive.
        // Matches String.substring(startIndex, endIndex) semantics so
        // callers can pass the range straight in.
        val result = OffsetRangeClipper.clip(start = 0, end = 10, bodyLength = 20)
        assertEquals(0, result?.start)
        assertEquals(10, result?.endExclusive)
    }

    @Test
    fun `negative start clamps to zero`() {
        // Defensive: a deserialized highlight with a corrupted negative
        // start should clamp to body start rather than crash.
        assertEquals(
            OffsetRangeClipper.Range(0, 15),
            OffsetRangeClipper.clip(start = -5, end = 15, bodyLength = 100),
        )
    }

    @Test
    fun `end beyond body length clamps to body length`() {
        // The body was truncated since the highlight was stored — clip
        // to the new end.
        assertEquals(
            OffsetRangeClipper.Range(5, 50),
            OffsetRangeClipper.clip(start = 5, end = 200, bodyLength = 50),
        )
    }

    @Test
    fun `both endpoints out of bounds get clamped`() {
        // The full body is selected — start clamps to 0, end clamps to N.
        assertEquals(
            OffsetRangeClipper.Range(0, 30),
            OffsetRangeClipper.clip(start = -10, end = 200, bodyLength = 30),
        )
    }

    @Test
    fun `reversed range is swapped before clipping`() {
        // Some serialization formats accidentally swap start and end —
        // recover rather than throwing. After swap (10, 5) -> (5, 10),
        // the result is a valid range.
        assertEquals(
            OffsetRangeClipper.Range(5, 10),
            OffsetRangeClipper.clip(start = 10, end = 5, bodyLength = 100),
        )
    }

    @Test
    fun `zero-length highlight collapses to null`() {
        // A highlight where start == end after all clipping carries no
        // glyphs and is degenerate — the caller should drop it rather
        // than persist a useless row.
        assertNull(OffsetRangeClipper.clip(start = 10, end = 10, bodyLength = 100))
    }

    @Test
    fun `range entirely below the body is null`() {
        // start = -20, end = -5, bodyLen = 100 — both clip to 0,
        // leaving a zero-length range that collapses to null.
        assertNull(OffsetRangeClipper.clip(start = -20, end = -5, bodyLength = 100))
    }

    @Test
    fun `range entirely above the body is null`() {
        // start = 200, end = 250, bodyLen = 100 — both clip to 100,
        // collapse to null.
        assertNull(OffsetRangeClipper.clip(start = 200, end = 250, bodyLength = 100))
    }

    @Test
    fun `body length of zero always returns null`() {
        // No body means nothing to highlight; even nominally-valid
        // ranges collapse.
        assertNull(OffsetRangeClipper.clip(start = 0, end = 0, bodyLength = 0))
        assertNull(OffsetRangeClipper.clip(start = 5, end = 10, bodyLength = 0))
    }

    @Test
    fun `negative body length returns null — defensive guard`() {
        // No legitimate caller passes a negative body length; this is
        // a paranoia check against corrupt deserialized data.
        assertNull(OffsetRangeClipper.clip(start = 0, end = 5, bodyLength = -1))
        assertNull(OffsetRangeClipper.clip(start = 0, end = 5, bodyLength = Int.MIN_VALUE))
    }

    @Test
    fun `clipping is idempotent — re-clipping a clipped range is a no-op`() {
        // A clipped range fed back through the clipper must not drift.
        // Critical for round-trip storage and for caches keyed on the
        // clipped range.
        val once = OffsetRangeClipper.clip(start = -10, end = 200, bodyLength = 50)
        val twice = once?.let { OffsetRangeClipper.clip(it.start, it.endExclusive, 50) }
        assertEquals(once, twice)
    }
}
