package com.po4yka.ratatoskr.util.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class BackStackOutcomeTest {
    @Test
    fun `depth 0 — NoOp regardless of terminal behavior`() {
        // Defensive: an empty stack should never trigger a close — the
        // route graph is responsible for there always being at least one
        // entry, but if a recreate or save/restore glitch produces an
        // empty stack, the back press must not crash the shell.
        assertEquals(
            BackStackOutcome.NoOp,
            BackStackOutcome.decide(currentDepth = 0, terminal = TerminalBehavior.CloseStack),
        )
        assertEquals(
            BackStackOutcome.NoOp,
            BackStackOutcome.decide(currentDepth = 0, terminal = TerminalBehavior.DoNothing),
        )
    }

    @Test
    fun `depth 1 with CloseStack — CloseStack`() {
        // The terminal back from the root route closes the activity
        // (Android: finish; iOS: dismiss the SwiftUI host). Pin so the
        // root-route test asserts the same outcome the production wiring
        // produces.
        assertEquals(
            BackStackOutcome.CloseStack,
            BackStackOutcome.decide(currentDepth = 1, terminal = TerminalBehavior.CloseStack),
        )
    }

    @Test
    fun `depth 1 with DoNothing — NoOp`() {
        // The Settings root route (or any nested component used standalone)
        // may intercept back without closing; the atom honors the
        // terminal-behavior switch.
        assertEquals(
            BackStackOutcome.NoOp,
            BackStackOutcome.decide(currentDepth = 1, terminal = TerminalBehavior.DoNothing),
        )
    }

    @Test
    fun `depth 2 — PopChild`() {
        assertEquals(
            BackStackOutcome.PopChild,
            BackStackOutcome.decide(currentDepth = 2, terminal = TerminalBehavior.CloseStack),
        )
    }

    @Test
    fun `depth 5 — PopChild — terminal switch irrelevant when above root`() {
        // Above the root, terminal behavior never applies — back always
        // pops a child. Pin so the deep-stack test doesn't accidentally
        // assert CloseStack at depth 5 because somebody passed the wrong
        // terminal marker.
        assertEquals(
            BackStackOutcome.PopChild,
            BackStackOutcome.decide(currentDepth = 5, terminal = TerminalBehavior.CloseStack),
        )
        assertEquals(
            BackStackOutcome.PopChild,
            BackStackOutcome.decide(currentDepth = 5, terminal = TerminalBehavior.DoNothing),
        )
    }

    @Test
    fun `negative depth — NoOp`() {
        // Defensive against arithmetic-error inputs (off-by-one in the
        // caller). Treat as the empty-stack case.
        assertEquals(
            BackStackOutcome.NoOp,
            BackStackOutcome.decide(currentDepth = -1, terminal = TerminalBehavior.CloseStack),
        )
    }

    @Test
    fun `decide is deterministic`() {
        val a = BackStackOutcome.decide(currentDepth = 3, terminal = TerminalBehavior.CloseStack)
        val b = BackStackOutcome.decide(currentDepth = 3, terminal = TerminalBehavior.CloseStack)
        assertEquals(a, b)
    }
}
