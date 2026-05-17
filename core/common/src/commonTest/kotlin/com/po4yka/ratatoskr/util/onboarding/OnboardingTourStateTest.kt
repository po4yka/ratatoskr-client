package com.po4yka.ratatoskr.util.onboarding

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnboardingTourStateTest {
    @Test
    fun `fresh install without deep link starts at step zero and visible`() {
        val state = OnboardingTourState.initial(savedCompleted = false, hasPendingDeepLink = false)

        assertEquals(0, state.step)
        assertTrue(state.isVisible)
        assertFalse(state.completed)
        assertFalse(state.deferred)
    }

    @Test
    fun `fresh install with pending deep link defers the tour — spec says do not block deep-link entry`() {
        // Spec: "Cannot block deep-link entry (if user enters via share intent,
        // tour is deferred to next plain launch)."
        val state = OnboardingTourState.initial(savedCompleted = false, hasPendingDeepLink = true)

        assertFalse(state.isVisible, "tour is hidden when entered via deep link")
        assertFalse(state.completed, "but persistence stays at not-completed so next launch shows it")
        assertTrue(state.deferred, "the deferred flag distinguishes this from a completed tour")
    }

    @Test
    fun `previously completed tour stays hidden regardless of deep-link state`() {
        val plain = OnboardingTourState.initial(savedCompleted = true, hasPendingDeepLink = false)
        val viaDeepLink = OnboardingTourState.initial(savedCompleted = true, hasPendingDeepLink = true)

        assertFalse(plain.isVisible)
        assertTrue(plain.completed)
        assertFalse(viaDeepLink.isVisible)
        assertTrue(viaDeepLink.completed)
    }

    @Test
    fun `next advances through every step before marking completed`() {
        var state = OnboardingTourState.initial(savedCompleted = false, hasPendingDeepLink = false)
        assertEquals(0, state.step)

        state = state.next()
        assertEquals(1, state.step)
        assertFalse(state.completed)

        state = state.next()
        assertEquals(2, state.step)
        assertFalse(state.completed)

        state = state.next()
        // Last step completes. Index -1 marks "not visible", completed flag persists.
        assertFalse(state.isVisible)
        assertTrue(state.completed)
    }

    @Test
    fun `skip from any step marks the tour completed and hides it forever`() {
        // Spec: "'Skip' hides forever (until manually re-triggered)."
        val state = OnboardingTourState.initial(savedCompleted = false, hasPendingDeepLink = false)
        val skipped = state.skip()

        assertFalse(skipped.isVisible)
        assertTrue(skipped.completed)
    }

    @Test
    fun `replay resets the tour to step zero even when previously completed`() {
        // Spec: "Tour can be reset from Settings → Help → Replay tour."
        val completed = OnboardingTourState.initial(savedCompleted = true, hasPendingDeepLink = false)
        val replayed = completed.replay()

        assertEquals(0, replayed.step)
        assertTrue(replayed.isVisible)
        assertFalse(replayed.completed)
    }

    @Test
    fun `next on a hidden state is a no-op so stale taps after completion cannot regress state`() {
        // Defends against a UI race where the user fires a "Next" tap while the
        // overlay is dismissing. Without this guard, completed could flip back
        // to a visible step.
        val completed = OnboardingTourState.initial(savedCompleted = true, hasPendingDeepLink = false)

        assertEquals(completed, completed.next())
    }

    @Test
    fun `step count matches the canonical three Frost overlay steps`() {
        // Spec calls out three steps: (1) submit URL via FAB, (2) share into the
        // app, (3) pin the widget. The constant pins that contract so a UI
        // refactor adding/removing pages must update this number deliberately.
        assertEquals(3, OnboardingTourState.STEP_COUNT)
    }

    @Test
    fun `deferred tour transitions to visible on next plain launch`() {
        // After a deep-link first launch deferred the tour, the next call to
        // initial(savedCompleted = false, hasPendingDeepLink = false) must
        // produce the visible state — the deferred flag does not persist beyond
        // the launch that observed it.
        val deferred = OnboardingTourState.initial(savedCompleted = false, hasPendingDeepLink = true)
        assertTrue(deferred.deferred)

        val nextLaunch = OnboardingTourState.initial(savedCompleted = false, hasPendingDeepLink = false)
        assertTrue(nextLaunch.isVisible)
        assertEquals(0, nextLaunch.step)
    }
}
