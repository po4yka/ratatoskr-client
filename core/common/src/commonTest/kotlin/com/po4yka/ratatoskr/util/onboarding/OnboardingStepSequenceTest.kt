package com.po4yka.ratatoskr.util.onboarding

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnboardingStepSequenceTest {
    @Test
    fun `next from Welcome is LibrarySync`() {
        assertEquals(
            OnboardingStep.LibrarySync,
            OnboardingStepSequence.next(OnboardingStep.Welcome),
        )
    }

    @Test
    fun `next from Sharing is Done — final tour step transitions to terminal`() {
        assertEquals(
            OnboardingStep.Done,
            OnboardingStepSequence.next(OnboardingStep.Sharing),
        )
    }

    @Test
    fun `next from Done stays at Done — terminal absorbs further taps`() {
        // A "Skip" tap that races with a "Next" tap on the final
        // pane must not transition past the terminal. The state
        // machine clamps at Done.
        assertEquals(
            OnboardingStep.Done,
            OnboardingStepSequence.next(OnboardingStep.Done),
        )
    }

    @Test
    fun `previous from LibrarySync is Welcome`() {
        assertEquals(
            OnboardingStep.Welcome,
            OnboardingStepSequence.previous(OnboardingStep.LibrarySync),
        )
    }

    @Test
    fun `previous from Welcome stays at Welcome — start absorbs back gesture`() {
        // The first pane absorbs the system back gesture rather than
        // dismissing the overlay (the dismiss button handles that).
        assertEquals(
            OnboardingStep.Welcome,
            OnboardingStepSequence.previous(OnboardingStep.Welcome),
        )
    }

    @Test
    fun `previous from Done returns to last visible pane Sharing`() {
        // Allows a "back from completion" undo so a user who
        // accidentally tapped Finish can return.
        assertEquals(
            OnboardingStep.Sharing,
            OnboardingStepSequence.previous(OnboardingStep.Done),
        )
    }

    @Test
    fun `isTerminal is true only for Done`() {
        assertTrue(OnboardingStepSequence.isTerminal(OnboardingStep.Done))
        assertFalse(OnboardingStepSequence.isTerminal(OnboardingStep.Welcome))
        assertFalse(OnboardingStepSequence.isTerminal(OnboardingStep.LibrarySync))
        assertFalse(OnboardingStepSequence.isTerminal(OnboardingStep.ReadingTools))
        assertFalse(OnboardingStepSequence.isTerminal(OnboardingStep.Sharing))
    }

    @Test
    fun `isStart is true only for Welcome`() {
        assertTrue(OnboardingStepSequence.isStart(OnboardingStep.Welcome))
        assertFalse(OnboardingStepSequence.isStart(OnboardingStep.LibrarySync))
        assertFalse(OnboardingStepSequence.isStart(OnboardingStep.ReadingTools))
        assertFalse(OnboardingStepSequence.isStart(OnboardingStep.Sharing))
        assertFalse(OnboardingStepSequence.isStart(OnboardingStep.Done))
    }

    @Test
    fun `full forward traversal reaches every step in order`() {
        // Pin the canonical pane order: Welcome -> LibrarySync ->
        // ReadingTools -> Sharing -> Done. A reorder forces this
        // test to be updated alongside the UI screens.
        val visited = mutableListOf(OnboardingStep.Welcome)
        var current = OnboardingStep.Welcome
        while (!OnboardingStepSequence.isTerminal(current)) {
            current = OnboardingStepSequence.next(current)
            visited += current
        }
        assertEquals(
            listOf(
                OnboardingStep.Welcome,
                OnboardingStep.LibrarySync,
                OnboardingStep.ReadingTools,
                OnboardingStep.Sharing,
                OnboardingStep.Done,
            ),
            visited,
        )
    }

    @Test
    fun `previous-of-next is identity for non-boundary steps`() {
        // The step machine is reversible in the middle: next then
        // previous returns the original step. Boundaries are
        // intentionally not reversible (terminal absorbs taps).
        val middleSteps =
            listOf(
                OnboardingStep.Welcome,
                OnboardingStep.LibrarySync,
                OnboardingStep.ReadingTools,
                OnboardingStep.Sharing,
            )
        middleSteps.forEach { step ->
            val nexted = OnboardingStepSequence.next(step)
            val roundTripped = OnboardingStepSequence.previous(nexted)
            assertEquals(step, roundTripped, "step $step did not round-trip")
        }
    }

    @Test
    fun `all enum entries are reachable through the sequence`() {
        // Pin that `OnboardingStepSequence.all` covers the entire
        // enum — adding a new pane to the enum without adding it to
        // the sequence list would silently break navigation.
        assertEquals(
            OnboardingStep.entries.toSet(),
            OnboardingStepSequence.all.toSet(),
        )
    }
}
