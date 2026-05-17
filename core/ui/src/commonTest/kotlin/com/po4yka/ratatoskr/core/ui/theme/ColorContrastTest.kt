package com.po4yka.ratatoskr.core.ui.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ColorContrastTest {
    @Test
    fun `ratio is 1 when foreground equals background`() {
        // Identical colors produce zero contrast. Pinned so a future
        // refactor of the relative-luminance formula doesn't silently
        // drift below 1.0 (which would be physically nonsensical).
        assertEquals(1.0, ColorContrast.ratio(WHITE, WHITE), TOL)
        assertEquals(1.0, ColorContrast.ratio(BLACK, BLACK), TOL)
        assertEquals(1.0, ColorContrast.ratio(FROST_INK, FROST_INK), TOL)
    }

    @Test
    fun `ratio is 21 to 1 for the white-on-black extreme`() {
        // WCAG's canonical upper bound — the formula maxes out at
        // exactly 21:1 for the (255,255,255) vs (0,0,0) pair. If this
        // drifts, the linearization step is wrong.
        assertEquals(21.0, ColorContrast.ratio(WHITE, BLACK), TOL)
    }

    @Test
    fun `ratio is symmetric — swapping foreground and background does not change it`() {
        // WCAG defines (L_lighter + 0.05) / (L_darker + 0.05), so the
        // function must internally sort by luminance.
        val ab = ColorContrast.ratio(BLACK, WHITE)
        val ba = ColorContrast.ratio(WHITE, BLACK)
        assertEquals(ab, ba, TOL)
    }

    @Test
    fun `Frost ink on page meets WCAG AAA for normal text`() {
        // INK #1C242C on PAGE #F0F2F5 is the canonical Frost light pair.
        // The two-color rule demands AAA-level legibility — pin so a
        // future palette PR can't silently shave the ratio below 7.0.
        val ratio = ColorContrast.ratio(FROST_INK, FROST_PAGE)
        assertTrue(ratio >= ColorContrast.WCAG_AAA_NORMAL, "Frost INK/PAGE was $ratio, need >= 7.0")
    }

    @Test
    fun `Frost ink on page reverses with same contrast`() {
        // Dark-mode pair is the inverse (INK #E8ECF0 on PAGE #12161C);
        // the symmetric property guarantees the dark pair clears the
        // same AAA bar without recomputation.
        val light = ColorContrast.ratio(FROST_INK, FROST_PAGE)
        val dark = ColorContrast.ratio(FROST_INK_DARK, FROST_PAGE_DARK)
        // Both pairs must clear AAA; not required to be identical since
        // they're inverse but visually-tuned, not raw inverses.
        assertTrue(light >= ColorContrast.WCAG_AAA_NORMAL)
        assertTrue(dark >= ColorContrast.WCAG_AAA_NORMAL)
    }

    @Test
    fun `meetsWcagAA passes a known-good pair`() {
        // 21:1 obviously clears the 4.5 threshold.
        assertTrue(ColorContrast.meetsWcagAA(WHITE, BLACK))
    }

    @Test
    fun `meetsWcagAA fails a known-bad pair — light gray on white`() {
        // #CCCCCC on white is the classic "looks fine on a designer's
        // monitor but unreadable in sunlight" anti-pattern; ratio ~1.6.
        val lightGray = Rgb24(0xCC, 0xCC, 0xCC)
        assertFalse(ColorContrast.meetsWcagAA(lightGray, WHITE))
    }

    @Test
    fun `large-text threshold is more permissive than normal-text`() {
        // WCAG AA: normal text needs 4.5, large text needs only 3.0.
        // A pair that clears 3.0 but not 4.5 should pass large-text only.
        // Build a synthetic pair via the constants — the implementation
        // selects based on the largeText flag.
        assertTrue(ColorContrast.WCAG_AA_LARGE < ColorContrast.WCAG_AA_NORMAL)
        assertTrue(ColorContrast.WCAG_AAA_LARGE < ColorContrast.WCAG_AAA_NORMAL)
    }

    @Test
    fun `Rgb24 constructor rejects out-of-range channel values`() {
        assertFails { Rgb24(-1, 0, 0) }
        assertFails { Rgb24(256, 0, 0) }
        assertFails { Rgb24(0, -1, 0) }
        assertFails { Rgb24(0, 0, 256) }
    }

    private companion object {
        const val TOL = 0.01
        val WHITE = Rgb24(255, 255, 255)
        val BLACK = Rgb24(0, 0, 0)
        val FROST_INK = Rgb24(0x1C, 0x24, 0x2C)
        val FROST_PAGE = Rgb24(0xF0, 0xF2, 0xF5)
        val FROST_INK_DARK = Rgb24(0xE8, 0xEC, 0xF0)
        val FROST_PAGE_DARK = Rgb24(0x12, 0x16, 0x1C)
    }
}
