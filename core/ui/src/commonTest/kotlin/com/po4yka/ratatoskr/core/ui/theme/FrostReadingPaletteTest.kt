package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class FrostReadingPaletteTest {
    private val spark = Color(0xFFDC3545)

    @Test
    fun `spark accent is invariant across all reading palettes`() {
        // DESIGN.md: spark never flips. If a future change loosens this, the
        // user-facing critical-action affordance breaks across themes.
        assertEquals(spark, frostLight.spark)
        assertEquals(spark, frostDark.spark)
        assertEquals(spark, frostSepia.spark)
        assertEquals(spark, frostHighContrast.spark)
    }

    @Test
    fun `sepia uses warm dark ink on cream page distinct from mono`() {
        assertNotEquals(frostLight.ink, frostSepia.ink)
        assertNotEquals(frostLight.page, frostSepia.page)
        // Sepia is a light-mode palette — not the dark variant.
        assertEquals(false, frostSepia.isDark)
    }

    @Test
    fun `high-contrast uses pure black on pure white`() {
        assertEquals(Color(0xFF000000), frostHighContrast.ink)
        assertEquals(Color(0xFFFFFFFF), frostHighContrast.page)
        assertEquals(false, frostHighContrast.isDark)
    }

    @Test
    fun `paletteFor resolves every ReadingTheme to a known palette`() {
        assertSame(frostLight, paletteFor(ReadingTheme.MONO_LIGHT))
        assertSame(frostDark, paletteFor(ReadingTheme.MONO_DARK))
        assertSame(frostSepia, paletteFor(ReadingTheme.SEPIA))
        assertSame(frostHighContrast, paletteFor(ReadingTheme.HIGH_CONTRAST))
    }

    @Test
    fun `all reading palettes carry inkPure and pagePure for spark-on-spark fallback`() {
        // Per Frost § Spark Accent Policy, spark-rendered tokens may need to
        // contrast against pure ink/pure page regardless of palette theme.
        val palettes = listOf(frostLight, frostDark, frostSepia, frostHighContrast)
        palettes.forEach { palette ->
            assertEquals(Color(0xFF000000), palette.inkPure)
            assertEquals(Color(0xFFFFFFFF), palette.pagePure)
        }
    }
}
