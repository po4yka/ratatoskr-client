package com.po4yka.ratatoskr.util.navigation

/**
 * What the root navigation shell should do when a child route is on top
 * and the user presses back at the bottom of the stack. Captures the
 * two production behaviors today: the route graph closes the shell, or
 * absorbs the gesture (e.g. a Settings root used standalone).
 */
enum class TerminalBehavior {
    CloseStack,
    DoNothing,
}

/**
 * Outcome of a back-handler invocation on a Decompose-style stack:
 *  - [PopChild] — pop one route, redraw the parent.
 *  - [CloseStack] — finish the activity / dismiss the SwiftUI host.
 *  - [NoOp] — absorb the gesture (empty stack or DoNothing terminal).
 */
enum class BackStackOutcome {
    PopChild,
    CloseStack,
    NoOp,
    ;

    companion object {
        /**
         * Pure decision atom for Decompose-style back-handler tests. The
         * AuthComponent / SummaryListComponent / etc. route tests can
         * assert against this without spinning up a `TestLifecycle` for
         * the trivial cases — the only state that matters is the
         * current depth and the terminal contract.
         *
         * Defensive on `currentDepth <= 0`: an empty stack never closes
         * — closing an empty stack would be a route-graph bug and the
         * shell should absorb the gesture instead of crashing.
         */
        fun decide(
            currentDepth: Int,
            terminal: TerminalBehavior,
        ): BackStackOutcome {
            if (currentDepth <= 0) return NoOp
            if (currentDepth >= 2) return PopChild
            return when (terminal) {
                TerminalBehavior.CloseStack -> CloseStack
                TerminalBehavior.DoNothing -> NoOp
            }
        }
    }
}
