package com.po4yka.ratatoskr.core.ui.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FontScaleBucketTest {
    @Test
    fun `the four canonical preview buckets match the spec`() {
        // Spec: "render representative screens at 0.85×, 1.0×, 1.3×, and 1.7×
        // font scales (clamped value will be 1.5×, but exercising 1.7× proves
        // the cap holds)."
        assertEquals(0.85f, FontScaleBucket.SMALL.rawScale)
        assertEquals(1.0f, FontScaleBucket.NORMAL.rawScale)
        assertEquals(1.3f, FontScaleBucket.LARGE.rawScale)
        assertEquals(1.7f, FontScaleBucket.XLARGE.rawScale)
    }

    @Test
    fun `clampedScale honors the FROST_DEFAULT_MAX_FONT_SCALE cap at the XLARGE bucket`() {
        // Pins the cap interaction. XLARGE is intentionally above the
        // 1.5x cap so the preview suite exercises both the request value
        // (1.7x) and the clamped value (1.5x) the user actually sees.
        assertEquals(FROST_DEFAULT_MAX_FONT_SCALE, FontScaleBucket.XLARGE.clampedScale())
    }

    @Test
    fun `clampedScale passes through scales already within the cap`() {
        assertEquals(0.85f, FontScaleBucket.SMALL.clampedScale())
        assertEquals(1.0f, FontScaleBucket.NORMAL.clampedScale())
        assertEquals(1.3f, FontScaleBucket.LARGE.clampedScale())
    }

    @Test
    fun `clampedScale respects a caller-supplied cap`() {
        // An app that wants a tighter cap for a specific preview can pass it
        // through — verifies the cap parameter actually clamps.
        assertEquals(1.1f, FontScaleBucket.LARGE.clampedScale(maxFontScale = 1.1f))
        assertEquals(1.0f, FontScaleBucket.NORMAL.clampedScale(maxFontScale = 1.2f))
    }

    @Test
    fun `nearest classifies an exact match to its own bucket`() {
        assertEquals(FontScaleBucket.SMALL, FontScaleBucket.nearest(0.85f))
        assertEquals(FontScaleBucket.NORMAL, FontScaleBucket.nearest(1.0f))
        assertEquals(FontScaleBucket.LARGE, FontScaleBucket.nearest(1.3f))
        assertEquals(FontScaleBucket.XLARGE, FontScaleBucket.nearest(1.7f))
    }

    @Test
    fun `nearest classifies an off-bucket scale to the closest bucket`() {
        // Real OS font scale values land between buckets. The classifier picks
        // the visually-closest bucket so the preview suite still represents
        // the user's actual setting.
        assertEquals(FontScaleBucket.NORMAL, FontScaleBucket.nearest(1.05f))
        assertEquals(FontScaleBucket.LARGE, FontScaleBucket.nearest(1.25f))
        assertEquals(FontScaleBucket.LARGE, FontScaleBucket.nearest(1.4f))
        assertEquals(FontScaleBucket.XLARGE, FontScaleBucket.nearest(1.65f))
    }

    @Test
    fun `nearest clamps below SMALL to SMALL — accessibility-shrunk text is rare but possible`() {
        // Android allows font sizes below 1.0x ("Small"/"Small"). We don't
        // ship sub-0.85x previews but the classifier still has to bucket
        // those users into the closest variant.
        assertEquals(FontScaleBucket.SMALL, FontScaleBucket.nearest(0.5f))
        assertEquals(FontScaleBucket.SMALL, FontScaleBucket.nearest(0.0f))
    }

    @Test
    fun `nearest clamps above XLARGE to XLARGE — accessibility-grown text`() {
        // iOS Dynamic Type Accessibility 5 (AX5) is around 2.0x; our XLARGE
        // bucket still represents the cap behavior even for those users.
        assertEquals(FontScaleBucket.XLARGE, FontScaleBucket.nearest(2.0f))
        assertEquals(FontScaleBucket.XLARGE, FontScaleBucket.nearest(3.5f))
    }

    @Test
    fun `nearest with a tie prefers the smaller bucket — conservative bias toward less clipping`() {
        // Tied distance (e.g. 1.15f is 0.15 from both NORMAL and LARGE) —
        // preferring the smaller bucket avoids over-stating the cap behavior
        // in the preview suite.
        assertEquals(FontScaleBucket.NORMAL, FontScaleBucket.nearest(1.15f))
        assertEquals(FontScaleBucket.LARGE, FontScaleBucket.nearest(1.5f))
    }

    @Test
    fun `previewScales returns the four canonical buckets in ascending order`() {
        // The preview suite iterates this list — order matters for the
        // generated screenshot file names so a refactor that swaps order
        // would rename hundreds of golden images.
        val scales = FontScaleBucket.previewScales()
        assertEquals(listOf(0.85f, 1.0f, 1.3f, 1.7f), scales)
    }

    @Test
    fun `previewClampedScales returns the post-clamp values applied by Frost`() {
        // The actual rendered scales after the cap kicks in. The XLARGE
        // entry shows 1.5x, not 1.7x. Used by preview tooling that wants to
        // label screenshots with what the user actually saw.
        val scales = FontScaleBucket.previewClampedScales()
        assertEquals(listOf(0.85f, 1.0f, 1.3f, FROST_DEFAULT_MAX_FONT_SCALE), scales)
    }

    @Test
    fun `the bucket entries enumerate every preview scale — guards against silent drops`() {
        // A refactor that removes a bucket would break the preview matrix
        // silently. The entries list must stay at 4.
        assertEquals(4, FontScaleBucket.entries.size)
        assertTrue(FontScaleBucket.entries.contains(FontScaleBucket.XLARGE))
    }
}
