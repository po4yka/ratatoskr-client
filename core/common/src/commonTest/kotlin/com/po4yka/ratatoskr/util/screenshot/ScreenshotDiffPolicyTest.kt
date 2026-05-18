package com.po4yka.ratatoskr.util.screenshot

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScreenshotDiffPolicyTest {
    @Test
    fun `Record mode — always UpdateGolden regardless of diff`() {
        // The `--record` invocation overwrites goldens with the current
        // render. Diff ratio is irrelevant — the contract is "trust the
        // current render". Pin so the Roborazzi runner never silently
        // fails a record run.
        assertEquals(
            ScreenshotOutcome.UpdateGolden,
            ScreenshotDiffPolicy.decide(
                diffRatio = 0.42,
                threshold = 0.01,
                mode = ScreenshotMode.Record,
            ),
        )
        assertEquals(
            ScreenshotOutcome.UpdateGolden,
            ScreenshotDiffPolicy.decide(
                diffRatio = 0.0,
                threshold = 0.01,
                mode = ScreenshotMode.Record,
            ),
        )
    }

    @Test
    fun `Verify mode — exact match passes at threshold zero`() {
        // Tightest possible threshold — pixel-identical to the golden.
        // Frost atoms with no AA / no shadow should default here.
        assertEquals(
            ScreenshotOutcome.Pass,
            ScreenshotDiffPolicy.decide(
                diffRatio = 0.0,
                threshold = 0.0,
                mode = ScreenshotMode.Verify,
            ),
        )
    }

    @Test
    fun `Verify mode — any drift fails when threshold is zero`() {
        val outcome =
            ScreenshotDiffPolicy.decide(
                diffRatio = 0.00001,
                threshold = 0.0,
                mode = ScreenshotMode.Verify,
            )
        assertTrue(outcome is ScreenshotOutcome.Fail)
    }

    @Test
    fun `Verify mode — at threshold passes inclusively`() {
        // Pin inclusive comparison: diffRatio == threshold passes. This
        // makes thresholds set in 0.005 increments behave as the design
        // sheet expects ("up to one half-percent drift is OK").
        assertEquals(
            ScreenshotOutcome.Pass,
            ScreenshotDiffPolicy.decide(
                diffRatio = 0.005,
                threshold = 0.005,
                mode = ScreenshotMode.Verify,
            ),
        )
    }

    @Test
    fun `Verify mode — just above threshold fails`() {
        val outcome =
            ScreenshotDiffPolicy.decide(
                diffRatio = 0.00501,
                threshold = 0.005,
                mode = ScreenshotMode.Verify,
            )
        assertTrue(outcome is ScreenshotOutcome.Fail)
    }

    @Test
    fun `Verify mode — threshold one accepts any non-NaN diff`() {
        // Per-atom relaxed threshold for surfaces with intentional
        // anti-aliasing variability (none today in Frost, but the policy
        // must support it).
        assertEquals(
            ScreenshotOutcome.Pass,
            ScreenshotDiffPolicy.decide(
                diffRatio = 0.999,
                threshold = 1.0,
                mode = ScreenshotMode.Verify,
            ),
        )
    }

    @Test
    fun `Verify mode — NaN diff fails as malformed input`() {
        // Defensive: a NaN diff ratio means the diff engine itself
        // failed to compute. Treat as Fail so the test surfaces the
        // problem rather than silently passing.
        val outcome =
            ScreenshotDiffPolicy.decide(
                diffRatio = Double.NaN,
                threshold = 0.01,
                mode = ScreenshotMode.Verify,
            )
        assertTrue(outcome is ScreenshotOutcome.Fail)
    }

    @Test
    fun `Verify mode — negative diff coerced to zero, passes`() {
        // Some diff engines report -0.0 or a tiny negative epsilon for
        // identical images. Coerce to zero so the comparison behaves
        // intuitively.
        assertEquals(
            ScreenshotOutcome.Pass,
            ScreenshotDiffPolicy.decide(
                diffRatio = -0.0001,
                threshold = 0.0,
                mode = ScreenshotMode.Verify,
            ),
        )
    }

    @Test
    fun `decide is deterministic`() {
        val a = ScreenshotDiffPolicy.decide(0.003, 0.005, ScreenshotMode.Verify)
        val b = ScreenshotDiffPolicy.decide(0.003, 0.005, ScreenshotMode.Verify)
        assertEquals(a, b)
    }
}
