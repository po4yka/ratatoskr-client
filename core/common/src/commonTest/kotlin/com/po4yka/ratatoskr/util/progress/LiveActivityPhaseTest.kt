package com.po4yka.ratatoskr.util.progress

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LiveActivityPhaseTest {
    @Test
    fun `pending advances to active on first started signal`() {
        assertEquals(
            LiveActivityPhase.Active,
            LiveActivityPhase.next(
                current = LiveActivityPhase.Pending,
                signal = LiveActivitySignal.Started,
            ),
        )
    }

    @Test
    fun `active advances to completed on Finished signal`() {
        assertEquals(
            LiveActivityPhase.Completed,
            LiveActivityPhase.next(
                current = LiveActivityPhase.Active,
                signal = LiveActivitySignal.Finished,
            ),
        )
    }

    @Test
    fun `active advances to cancelled on Cancelled signal`() {
        assertEquals(
            LiveActivityPhase.Cancelled,
            LiveActivityPhase.next(
                current = LiveActivityPhase.Active,
                signal = LiveActivitySignal.Cancelled,
            ),
        )
    }

    @Test
    fun `active advances to stalled on Stalled signal`() {
        // The orchestrator surfaces Stalled when no progress event has
        // arrived for > 30s; the live activity shows a "this is taking
        // longer than expected" banner without losing the in-flight
        // progress bar.
        assertEquals(
            LiveActivityPhase.Stalled,
            LiveActivityPhase.next(
                current = LiveActivityPhase.Active,
                signal = LiveActivitySignal.Stalled,
            ),
        )
    }

    @Test
    fun `stalled recovers to active on a Progress signal`() {
        // A late progress event after a Stalled banner returns the
        // activity to Active rather than completing.
        assertEquals(
            LiveActivityPhase.Active,
            LiveActivityPhase.next(
                current = LiveActivityPhase.Stalled,
                signal = LiveActivitySignal.Progress,
            ),
        )
    }

    @Test
    fun `terminal Completed absorbs further signals`() {
        for (signal in LiveActivitySignal.entries) {
            assertEquals(
                LiveActivityPhase.Completed,
                LiveActivityPhase.next(LiveActivityPhase.Completed, signal),
                "Completed must absorb $signal",
            )
        }
    }

    @Test
    fun `terminal Cancelled absorbs further signals`() {
        for (signal in LiveActivitySignal.entries) {
            assertEquals(
                LiveActivityPhase.Cancelled,
                LiveActivityPhase.next(LiveActivityPhase.Cancelled, signal),
                "Cancelled must absorb $signal",
            )
        }
    }

    @Test
    fun `isTerminal is true only for Completed and Cancelled`() {
        assertTrue(LiveActivityPhase.isTerminal(LiveActivityPhase.Completed))
        assertTrue(LiveActivityPhase.isTerminal(LiveActivityPhase.Cancelled))
        for (phase in setOf(
            LiveActivityPhase.Pending,
            LiveActivityPhase.Active,
            LiveActivityPhase.Stalled,
        )) {
            assertEquals(
                false,
                LiveActivityPhase.isTerminal(phase),
                "$phase must not be terminal",
            )
        }
    }

    @Test
    fun `progress signal on Active stays Active — pure update path`() {
        // Progress events don't change phase; the LiveActivity widget
        // re-renders via the data model, not via phase transitions.
        assertEquals(
            LiveActivityPhase.Active,
            LiveActivityPhase.next(LiveActivityPhase.Active, LiveActivitySignal.Progress),
        )
    }

    @Test
    fun `pending absorbs unrelated signals — only Started transitions`() {
        // A Progress event before Started is a race; ignore it.
        assertEquals(
            LiveActivityPhase.Pending,
            LiveActivityPhase.next(LiveActivityPhase.Pending, LiveActivitySignal.Progress),
        )
        // A Stalled event before Started is similarly ignored.
        assertEquals(
            LiveActivityPhase.Pending,
            LiveActivityPhase.next(LiveActivityPhase.Pending, LiveActivitySignal.Stalled),
        )
    }

    @Test
    fun `state machine is deterministic`() {
        val a = LiveActivityPhase.next(LiveActivityPhase.Active, LiveActivitySignal.Progress)
        val b = LiveActivityPhase.next(LiveActivityPhase.Active, LiveActivitySignal.Progress)
        assertEquals(a, b)
    }
}
