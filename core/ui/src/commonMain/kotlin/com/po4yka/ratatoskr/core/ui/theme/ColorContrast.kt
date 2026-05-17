package com.po4yka.ratatoskr.core.ui.theme

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * An sRGB 24-bit color (R, G, B each 0..255). Used by [ColorContrast]
 * for the WCAG relative-luminance calculation. The init guard guarantees
 * the math never sees an out-of-range channel — the formula is undefined
 * outside 0..1 after normalization.
 */
data class Rgb24(val r: Int, val g: Int, val b: Int) {
    init {
        require(r in 0..255) { "R out of range: $r" }
        require(g in 0..255) { "G out of range: $g" }
        require(b in 0..255) { "B out of range: $b" }
    }
}

/**
 * WCAG 2.1 contrast-ratio math against the sRGB color space. Used by
 * the accessibility audit (`audit-screen-semantics-for-talkback-and-voiceover`)
 * and by the Frost palette regression suite to pin that the two-color
 * INK/PAGE rule clears AAA-level legibility.
 *
 * Formula:
 *  1. Linearize each sRGB channel:
 *     `c <= 0.03928 ? c / 12.92 : ((c + 0.055) / 1.055) ^ 2.4`
 *  2. Relative luminance: `L = 0.2126 * R + 0.7152 * G + 0.0722 * B`
 *  3. Contrast: `(L_lighter + 0.05) / (L_darker + 0.05)`
 *
 * The function is symmetric — swapping foreground and background is a
 * no-op — because it internally sorts the two luminances. The result
 * is in the closed interval [1.0, 21.0].
 *
 * Thresholds mirror WCAG 2.1:
 *  - AA:  4.5 normal text / 3.0 large text
 *  - AAA: 7.0 normal text / 4.5 large text
 *
 * Pure double-precision math; no allocation on the hot path.
 */
object ColorContrast {
    const val WCAG_AA_NORMAL = 4.5
    const val WCAG_AA_LARGE = 3.0
    const val WCAG_AAA_NORMAL = 7.0
    const val WCAG_AAA_LARGE = 4.5

    fun ratio(
        a: Rgb24,
        b: Rgb24,
    ): Double {
        val la = relativeLuminance(a)
        val lb = relativeLuminance(b)
        val lighter = max(la, lb)
        val darker = min(la, lb)
        return (lighter + 0.05) / (darker + 0.05)
    }

    fun meetsWcagAA(
        fg: Rgb24,
        bg: Rgb24,
        largeText: Boolean = false,
    ): Boolean {
        val threshold = if (largeText) WCAG_AA_LARGE else WCAG_AA_NORMAL
        return ratio(fg, bg) >= threshold
    }

    private fun relativeLuminance(c: Rgb24): Double {
        val r = linearizeChannel(c.r)
        val g = linearizeChannel(c.g)
        val b = linearizeChannel(c.b)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun linearizeChannel(value: Int): Double {
        val normalized = value / 255.0
        return if (normalized <= 0.03928) {
            normalized / 12.92
        } else {
            ((normalized + 0.055) / 1.055).pow(2.4)
        }
    }
}
