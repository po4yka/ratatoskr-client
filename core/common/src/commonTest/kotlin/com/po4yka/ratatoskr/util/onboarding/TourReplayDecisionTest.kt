package com.po4yka.ratatoskr.util.onboarding

import kotlin.test.Test
import kotlin.test.assertEquals

class TourReplayDecisionTest {
    @Test
    fun `first launch — never completed — ShowFirstRun`() {
        // Fresh install: full tour with the welcome pane animation.
        assertEquals(
            TourReplayDecision.Action.ShowFirstRun,
            TourReplayDecision.decide(
                everCompleted = false,
                lastSeenTourVersion = 0,
                currentTourVersion = 1,
            ),
        )
    }

    @Test
    fun `completed previous tour — same version — Skip`() {
        // User already saw this version of the tour; respect that.
        assertEquals(
            TourReplayDecision.Action.Skip,
            TourReplayDecision.decide(
                everCompleted = true,
                lastSeenTourVersion = 2,
                currentTourVersion = 2,
            ),
        )
    }

    @Test
    fun `completed previous tour — new tour version — ShowReplay`() {
        // The tour content changed in this build (new pane added).
        // Replay with the "what's new" framing instead of "welcome".
        assertEquals(
            TourReplayDecision.Action.ShowReplay,
            TourReplayDecision.decide(
                everCompleted = true,
                lastSeenTourVersion = 2,
                currentTourVersion = 3,
            ),
        )
    }

    @Test
    fun `never completed but lastSeenVersion above zero — defensive ShowFirstRun`() {
        // Inconsistent state (a write race): everCompleted=false but
        // a version was recorded. Prefer the first-run path so we
        // don't silently lose the onboarding.
        assertEquals(
            TourReplayDecision.Action.ShowFirstRun,
            TourReplayDecision.decide(
                everCompleted = false,
                lastSeenTourVersion = 5,
                currentTourVersion = 5,
            ),
        )
    }

    @Test
    fun `current version below last seen — Skip — clock-skew defensive`() {
        // App downgrade or staged-rollout regression: never replay
        // because the user has already seen a strictly newer version.
        assertEquals(
            TourReplayDecision.Action.Skip,
            TourReplayDecision.decide(
                everCompleted = true,
                lastSeenTourVersion = 10,
                currentTourVersion = 5,
            ),
        )
    }

    @Test
    fun `negative version inputs collapse to zero — defensive`() {
        // Bad state should not corrupt the decision tree.
        assertEquals(
            TourReplayDecision.Action.ShowFirstRun,
            TourReplayDecision.decide(
                everCompleted = false,
                lastSeenTourVersion = -1,
                currentTourVersion = 1,
            ),
        )
        assertEquals(
            TourReplayDecision.Action.Skip,
            TourReplayDecision.decide(
                everCompleted = true,
                lastSeenTourVersion = -1,
                currentTourVersion = -2,
            ),
        )
    }

    @Test
    fun `decision is deterministic`() {
        val a =
            TourReplayDecision.decide(
                everCompleted = true,
                lastSeenTourVersion = 2,
                currentTourVersion = 3,
            )
        val b =
            TourReplayDecision.decide(
                everCompleted = true,
                lastSeenTourVersion = 2,
                currentTourVersion = 3,
            )
        assertEquals(a, b)
    }
}
