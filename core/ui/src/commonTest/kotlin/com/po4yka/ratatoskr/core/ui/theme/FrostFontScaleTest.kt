package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.ui.unit.Density
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class FrostFontScaleTest {
    @Test
    fun `density at or below cap is returned unchanged`() {
        val d = Density(density = 3f, fontScale = 1.0f)
        assertSame(d, clampDensity(d, maxFontScale = 1.5f))

        val atCap = Density(density = 3f, fontScale = 1.5f)
        assertSame(atCap, clampDensity(atCap, maxFontScale = 1.5f))
    }

    @Test
    fun `density above cap is clamped down preserving density`() {
        val d = Density(density = 3f, fontScale = 2.3f)
        val clamped = clampDensity(d, maxFontScale = 1.5f)
        assertEquals(3f, clamped.density)
        assertEquals(1.5f, clamped.fontScale)
    }

    @Test
    fun `default cap is the documented 1_5x`() {
        // If someone retunes this, downstream layout tests must follow.
        assertEquals(1.5f, FROST_DEFAULT_MAX_FONT_SCALE)
    }
}
