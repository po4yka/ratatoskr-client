package com.po4yka.ratatoskr.feature.summary.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SelectionStateTest {
    @Test
    fun `empty state is the default and not active`() {
        val state = SelectionState.empty<String>()
        assertTrue(state.selectedIds.isEmpty())
        assertEquals(0, state.count)
        assertFalse(state.isActive, "an empty selection must not render the bulk toolbar")
    }

    @Test
    fun `start activates the selection with exactly the seeded id`() {
        // Long-press is the only entrypoint into selection mode. Anything that calls
        // start() must reliably enter mode with the picked item already selected.
        val state = SelectionState.empty<String>().start("a")

        assertEquals(setOf("a"), state.selectedIds)
        assertTrue(state.isActive)
        assertTrue(state.contains("a"))
    }

    @Test
    fun `toggle adds an id when absent`() {
        val state =
            SelectionState.empty<String>()
                .start("a")
                .toggle("b")

        assertEquals(setOf("a", "b"), state.selectedIds)
        assertEquals(2, state.count)
    }

    @Test
    fun `toggle removes an id when already selected`() {
        val state =
            SelectionState.empty<String>()
                .start("a")
                .toggle("b")
                .toggle("a")

        assertEquals(setOf("b"), state.selectedIds)
        assertTrue(state.isActive)
    }

    @Test
    fun `toggling the last selected id deactivates the selection — back-button parity`() {
        // Regression guard: deselecting the final remaining item should be
        // indistinguishable from pressing Back. If isActive stayed true with an empty
        // set, the bulk toolbar would render with a count of zero — broken UI.
        val state =
            SelectionState.empty<String>()
                .start("a")
                .toggle("a")

        assertTrue(state.selectedIds.isEmpty())
        assertFalse(state.isActive)
    }

    @Test
    fun `clear deactivates regardless of selection size`() {
        val state =
            SelectionState.empty<String>()
                .start("a")
                .toggle("b")
                .toggle("c")
                .clear()

        assertTrue(state.selectedIds.isEmpty())
        assertFalse(state.isActive)
    }

    @Test
    fun `toggle is idempotent under double-tap when fired twice in a row`() {
        // Property: toggle . toggle == identity. Defends against a future change that
        // accidentally short-circuits the second call and leaves the set unchanged.
        val seed = SelectionState.empty<String>().start("a").toggle("b")
        val doubled = seed.toggle("c").toggle("c")

        assertEquals(seed.selectedIds, doubled.selectedIds)
    }

    @Test
    fun `start while active replaces the selection with just the new seed`() {
        // Behavior choice: a fresh long-press resets the selection. The alternative —
        // additive — is wrong because long-press is the entry gesture, not an
        // accumulator. The accumulator gesture is plain tap → toggle.
        val state =
            SelectionState.empty<String>()
                .start("a")
                .toggle("b")
                .start("c")

        assertEquals(setOf("c"), state.selectedIds)
    }

    @Test
    fun `state is value-equal across reconstructed copies — safe for use as Compose key`() {
        val a = SelectionState.empty<String>().start("x").toggle("y")
        val b = SelectionState.empty<String>().start("x").toggle("y")
        assertEquals(a, b, "deterministic equality is needed for Compose recomposition skipping")
    }
}
