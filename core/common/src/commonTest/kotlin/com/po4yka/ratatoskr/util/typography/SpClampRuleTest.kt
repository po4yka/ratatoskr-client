package com.po4yka.ratatoskr.util.typography

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpClampRuleTest {
    @Test
    fun `font scale of one yields the baseline unchanged`() {
        // Sanity: at the OS default font scale, the baseline value
        // passes through.
        assertEquals(
            16f,
            SpClampRule.effective(baselineSp = 16f, fontScale = 1.0f),
        )
    }

    @Test
    fun `font scale doubles the baseline at scale 2`() {
        // Accessibility large-font users typically run between 1.3
        // and 2.0; pin the middle of that range.
        assertEquals(
            32f,
            SpClampRule.effective(baselineSp = 16f, fontScale = 2.0f),
        )
    }

    @Test
    fun `extreme scale is clamped to the max bound`() {
        // Some Android OEMs allow font scales up to 2.5x or beyond
        // via accessibility services; without a clamp, button-row
        // text would blow out the Frost layout. Pin the upper bound.
        val effective =
            SpClampRule.effective(
                baselineSp = 16f,
                fontScale = 4.0f,
                maxSp = 48f,
            )
        assertEquals(48f, effective)
    }

    @Test
    fun `tiny scale is clamped to the min bound`() {
        // 0.5x scale on a 16sp baseline = 8sp, which is below the
        // legible-text floor on phone screens. Clamp.
        val effective =
            SpClampRule.effective(
                baselineSp = 16f,
                fontScale = 0.1f,
                minSp = 12f,
            )
        assertEquals(12f, effective)
    }

    @Test
    fun `negative font scale is clamped to min — defensive`() {
        // No legitimate caller passes a negative scale, but Compose
        // density bridges have surfaced -1f briefly during recompose
        // on hot reload. Be defensive.
        val effective =
            SpClampRule.effective(
                baselineSp = 16f,
                fontScale = -1.0f,
            )
        assertEquals(SpClampRule.DEFAULT_MIN_SP, effective)
    }

    @Test
    fun `negative baseline is clamped to min — defensive`() {
        // A negative baseline is a caller bug; surface the min as the
        // safe default so a frame doesn't render with a negative
        // text height.
        val effective =
            SpClampRule.effective(
                baselineSp = -1f,
                fontScale = 1.0f,
            )
        assertEquals(SpClampRule.DEFAULT_MIN_SP, effective)
    }

    @Test
    fun `result always lands inside the configured bounds`() {
        // Pin the invariant across the input space — no combination
        // of (baseline, scale) produces a value outside [min, max].
        val baselines = listOf(0f, 8f, 16f, 24f, 100f, -5f)
        val scales = listOf(0f, 0.5f, 1f, 1.5f, 2.0f, 4f, -1f)
        for (baseline in baselines) {
            for (scale in scales) {
                val v =
                    SpClampRule.effective(
                        baselineSp = baseline,
                        fontScale = scale,
                    )
                assertTrue(
                    v in SpClampRule.DEFAULT_MIN_SP..SpClampRule.DEFAULT_MAX_SP,
                    "effective(baseline=$baseline, scale=$scale)=$v outside default bounds",
                )
            }
        }
    }

    @Test
    fun `custom min and max bounds are honored`() {
        // A constrained chrome surface (e.g. tab labels) may want
        // tighter bounds — pin the override.
        assertEquals(
            14f,
            SpClampRule.effective(
                baselineSp = 12f,
                fontScale = 3.0f,
                minSp = 11f,
                maxSp = 14f,
            ),
        )
        assertEquals(
            11f,
            SpClampRule.effective(
                baselineSp = 12f,
                fontScale = 0.1f,
                minSp = 11f,
                maxSp = 14f,
            ),
        )
    }

    @Test
    fun `repeated calls are deterministic`() {
        val a = SpClampRule.effective(baselineSp = 16f, fontScale = 1.3f)
        val b = SpClampRule.effective(baselineSp = 16f, fontScale = 1.3f)
        assertEquals(a, b)
    }
}
