package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.animation.core.TweenSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FrostMotionTest {
    @Test
    fun `frostMotionReduced collapses finite specs to snap or near-zero tween`() {
        // The undoFade is the only finite spec that does not snap — it uses a
        // 1ms tween so any caller that expected a non-snap spec still gets a
        // valid AnimationSpec instance back. Document the intent here so a
        // future change that flips it to snap() does so deliberately.
        val reducedUndoFade = frostMotionReduced.undoFade
        assertTrue(reducedUndoFade is TweenSpec<Float>, "expected TweenSpec, got $reducedUndoFade")
        assertEquals(1, reducedUndoFade.durationMillis)
    }

    @Test
    fun `frostMotionDefault is distinct from frostMotionReduced`() {
        assertTrue(frostMotionDefault !== frostMotionReduced)
        // Compare a representative finite spec: clickPress is tween(80) in
        // default vs snap() in reduced.
        assertTrue(frostMotionDefault.clickPress != frostMotionReduced.clickPress)
    }
}
