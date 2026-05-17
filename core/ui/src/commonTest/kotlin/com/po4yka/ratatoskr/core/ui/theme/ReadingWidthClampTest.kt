package com.po4yka.ratatoskr.core.ui.theme

import kotlin.test.Test
import kotlin.test.assertEquals

class ReadingWidthClampTest {
    @Test
    fun `clamp returns the default when the input is the default`() {
        assertEquals(ReadingWidthClamp.DEFAULT_CH, ReadingWidthClamp.clamp(ReadingWidthClamp.DEFAULT_CH))
    }

    @Test
    fun `clamp pins below-minimum values to the minimum`() {
        // The slider should never produce values below the minimum, but defensive
        // clamping protects against migrated preference rows from older builds and
        // any future caller that hands us a raw user-supplied number.
        assertEquals(ReadingWidthClamp.MIN_CH, ReadingWidthClamp.clamp(20))
        assertEquals(ReadingWidthClamp.MIN_CH, ReadingWidthClamp.clamp(0))
        assertEquals(ReadingWidthClamp.MIN_CH, ReadingWidthClamp.clamp(-100))
    }

    @Test
    fun `clamp pins above-maximum values to the maximum`() {
        assertEquals(ReadingWidthClamp.MAX_CH, ReadingWidthClamp.clamp(120))
        assertEquals(ReadingWidthClamp.MAX_CH, ReadingWidthClamp.clamp(Int.MAX_VALUE))
    }

    @Test
    fun `clamp range is 45 to 75 characters per spec`() {
        // Regression guard: the spec is explicit about the 45..75 ch range. A future
        // contributor who widens it without thought would break the design intent.
        assertEquals(45, ReadingWidthClamp.MIN_CH)
        assertEquals(75, ReadingWidthClamp.MAX_CH)
        assertEquals(65, ReadingWidthClamp.DEFAULT_CH)
    }

    @Test
    fun `maxBodyDp multiplies clamped ch by character width when the screen is wider`() {
        // Tablet / desktop: screen is wider than the computed max, so the body is
        // clamped to the typographic preference.
        val result =
            ReadingWidthClamp.maxBodyDp(
                lineWidthCh = 65,
                characterWidthDp = 8.0,
                screenWidthDp = 1024.0,
            )

        assertEquals(520.0, result, "65ch × 8dp/ch = 520dp; screen is 1024dp so the clamp wins")
    }

    @Test
    fun `maxBodyDp falls back to the screen width when the device is narrower than the preference`() {
        // Phone width: spec says "Defaults must look identical to today's layout on
        // phone-sized screens" — meaning the preference is *ignored* when the screen
        // is narrower than the computed max, not stretched.
        val result =
            ReadingWidthClamp.maxBodyDp(
                lineWidthCh = 65,
                characterWidthDp = 8.0,
                screenWidthDp = 360.0,
            )

        assertEquals(360.0, result, "computed 520dp exceeds screen 360dp; the screen wins")
    }

    @Test
    fun `maxBodyDp clamps the input ch before multiplying`() {
        // Regression guard: an out-of-range preference value must not slip through and
        // produce an absurdly wide body. The clamp is applied first, then multiplied.
        val tooWide =
            ReadingWidthClamp.maxBodyDp(
                lineWidthCh = 200,
                characterWidthDp = 8.0,
                screenWidthDp = 2000.0,
            )

        assertEquals(75.0 * 8.0, tooWide, "input 200ch must be clamped to 75ch first")
    }

    @Test
    fun `maxBodyDp at exactly the boundary uses the preference, not the screen`() {
        // Boundary: when computed == screen, both branches produce the same value,
        // but the intent is "use the preference". Verifies minOf semantics.
        val boundary =
            ReadingWidthClamp.maxBodyDp(
                lineWidthCh = 50,
                characterWidthDp = 8.0,
                screenWidthDp = 400.0,
            )

        assertEquals(400.0, boundary)
    }
}
