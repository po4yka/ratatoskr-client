package com.po4yka.ratatoskr.util.progress

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProgressFractionFormatterTest {
    @Test
    fun `zero of five — empty progress bar`() {
        // The Live Activity opens at (0, total) while the
        // summarization job is queued; the bar should render empty.
        assertEquals(0.0f, ProgressFractionFormatter.ratio(completed = 0, total = 5))
        assertEquals("0 of 5", ProgressFractionFormatter.label(completed = 0, total = 5))
        assertEquals(0, ProgressFractionFormatter.percent(completed = 0, total = 5))
    }

    @Test
    fun `three of ten — middle progress`() {
        assertEquals(0.3f, ProgressFractionFormatter.ratio(completed = 3, total = 10))
        assertEquals("3 of 10", ProgressFractionFormatter.label(completed = 3, total = 10))
        assertEquals(30, ProgressFractionFormatter.percent(completed = 3, total = 10))
    }

    @Test
    fun `five of five — completion`() {
        // Pin the upper bound exactly — a Live Activity stuck at
        // 99% because of a floating-point off-by-one would be a
        // visible bug.
        assertEquals(1.0f, ProgressFractionFormatter.ratio(completed = 5, total = 5))
        assertEquals("5 of 5", ProgressFractionFormatter.label(completed = 5, total = 5))
        assertEquals(100, ProgressFractionFormatter.percent(completed = 5, total = 5))
    }

    @Test
    fun `total of zero — fully degenerate input, never divide-by-zero`() {
        // The orchestrator can briefly show a Live Activity before
        // the total count is known. Show 0/0 cleanly rather than
        // crashing with ArithmeticException.
        assertEquals(0.0f, ProgressFractionFormatter.ratio(completed = 0, total = 0))
        assertEquals("0 of 0", ProgressFractionFormatter.label(completed = 0, total = 0))
        assertEquals(0, ProgressFractionFormatter.percent(completed = 0, total = 0))
    }

    @Test
    fun `completed greater than total — clamps to total`() {
        // A racing update can push completed past total briefly;
        // clamp rather than draw a 110%-full progress bar.
        assertEquals(1.0f, ProgressFractionFormatter.ratio(completed = 10, total = 5))
        assertEquals("5 of 5", ProgressFractionFormatter.label(completed = 10, total = 5))
        assertEquals(100, ProgressFractionFormatter.percent(completed = 10, total = 5))
    }

    @Test
    fun `negative completed clamps to zero`() {
        assertEquals(0.0f, ProgressFractionFormatter.ratio(completed = -1, total = 5))
        assertEquals("0 of 5", ProgressFractionFormatter.label(completed = -1, total = 5))
        assertEquals(0, ProgressFractionFormatter.percent(completed = -1, total = 5))
    }

    @Test
    fun `negative total clamps to zero — same as undefined`() {
        assertEquals(0.0f, ProgressFractionFormatter.ratio(completed = 3, total = -5))
        assertEquals("0 of 0", ProgressFractionFormatter.label(completed = 3, total = -5))
        assertEquals(0, ProgressFractionFormatter.percent(completed = 3, total = -5))
    }

    @Test
    fun `percent stays in the 0 through 100 range`() {
        // Pin the invariant across the full input space — even with
        // negative or out-of-range inputs the percent must be in
        // [0,100] so a progress widget never renders out-of-bounds.
        val cases =
            listOf(
                -5 to -5,
                -1 to 0,
                0 to 0,
                0 to 10,
                3 to 10,
                10 to 10,
                15 to 10,
                Int.MAX_VALUE to Int.MAX_VALUE,
            )
        cases.forEach { (completed, total) ->
            val p = ProgressFractionFormatter.percent(completed, total)
            assertTrue(p in 0..100, "percent($completed, $total) = $p outside [0,100]")
        }
    }

    @Test
    fun `ratio stays in the zero through one range`() {
        val cases =
            listOf(
                -5 to -5,
                -1 to 0,
                0 to 0,
                0 to 10,
                3 to 10,
                10 to 10,
                15 to 10,
            )
        cases.forEach { (completed, total) ->
            val r = ProgressFractionFormatter.ratio(completed, total)
            assertTrue(r in 0.0f..1.0f, "ratio($completed, $total) = $r outside [0,1]")
        }
    }

    @Test
    fun `repeated calls are deterministic`() {
        val a = ProgressFractionFormatter.label(7, 12)
        val b = ProgressFractionFormatter.label(7, 12)
        assertEquals(a, b)
        assertEquals(
            ProgressFractionFormatter.percent(7, 12),
            ProgressFractionFormatter.percent(7, 12),
        )
    }
}
