package com.po4yka.ratatoskr.core.ui.theme

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Discrete font-scale buckets used by the typography preview suite. The OS
 * exposes a continuous [androidx.compose.ui.unit.Density.fontScale] value;
 * the preview suite renders representative screens at four canonical points
 * (0.85x, 1.0x, 1.3x, 1.7x). The XLARGE bucket is intentionally above the
 * [FROST_DEFAULT_MAX_FONT_SCALE] cap so the preview exercises the cap
 * behavior — the screenshot a user actually sees at AX5 is the clamped
 * 1.5x, but the input is the requested 1.7x and the preview suite must
 * cover that boundary explicitly.
 *
 * [nearest] classifies a continuous scale into the closest bucket using
 * integer math (raw float distance comparison would let 1.15f flicker
 * between NORMAL and LARGE depending on the trailing float error). Ties
 * prefer the smaller bucket — conservative bias that under-states cap
 * behavior in screenshots rather than over-states it.
 *
 * Ascending order is part of the contract: [previewScales] / [entries]
 * iteration order drives screenshot file names; a refactor that swaps
 * order would rename the entire golden image set.
 */
enum class FontScaleBucket(val rawScale: Float) {
    SMALL(0.85f),
    NORMAL(1.0f),
    LARGE(1.3f),
    XLARGE(1.7f),
    ;

    fun clampedScale(maxFontScale: Float = FROST_DEFAULT_MAX_FONT_SCALE): Float =
        minOf(rawScale, maxFontScale)

    companion object {
        fun nearest(scale: Float): FontScaleBucket {
            val scaled = (scale * 100f).roundToInt()
            var best = entries.first()
            var bestDist = abs(scaled - (best.rawScale * 100f).roundToInt())
            for (bucket in entries.drop(1)) {
                val dist = abs(scaled - (bucket.rawScale * 100f).roundToInt())
                if (dist < bestDist) {
                    best = bucket
                    bestDist = dist
                }
            }
            return best
        }

        fun previewScales(): List<Float> = entries.map { it.rawScale }

        fun previewClampedScales(maxFontScale: Float = FROST_DEFAULT_MAX_FONT_SCALE): List<Float> =
            entries.map { it.clampedScale(maxFontScale) }
    }
}
