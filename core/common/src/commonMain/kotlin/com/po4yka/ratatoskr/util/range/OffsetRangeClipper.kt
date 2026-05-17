package com.po4yka.ratatoskr.util.range

import kotlin.math.max
import kotlin.math.min

/**
 * Clips a half-open `[start, end)` offset range against a body of
 * length [bodyLength]. Used by the highlight pipeline (when a
 * deserialized highlight refers to a body that has since been edited
 * or truncated) and by any caller that needs a defensive bounds-check
 * before passing offsets to `String.substring`.
 *
 * Rules (in order of application):
 *  1. negative or zero body length always returns null — no glyphs to
 *     point at.
 *  2. if `start > end` the two are swapped — recover from corrupt
 *     serialization rather than throwing.
 *  3. each endpoint is clamped to `[0, bodyLength]`.
 *  4. if the clamped range is zero-length, return null — a degenerate
 *     highlight is dropped rather than persisted.
 *
 * Output is a half-open [Range] with `endExclusive` matching the
 * `String.substring(start, endExclusive)` convention so the caller
 * can pass it straight in.
 *
 * Pure, side-effect-free, idempotent.
 */
object OffsetRangeClipper {
    data class Range(val start: Int, val endExclusive: Int)

    fun clip(
        start: Int,
        end: Int,
        bodyLength: Int,
    ): Range? {
        if (bodyLength <= 0) return null
        val (lo, hi) = if (start <= end) start to end else end to start
        val clampedStart = max(0, min(lo, bodyLength))
        val clampedEnd = max(0, min(hi, bodyLength))
        if (clampedStart >= clampedEnd) return null
        return Range(clampedStart, clampedEnd)
    }
}
