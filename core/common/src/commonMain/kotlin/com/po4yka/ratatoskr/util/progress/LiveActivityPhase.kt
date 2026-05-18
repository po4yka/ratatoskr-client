package com.po4yka.ratatoskr.util.progress

/**
 * Lifecycle phase of an iOS Live Activity / Android foreground-service
 * progress notification for a summarization job.
 *
 * Lifecycle:
 *  - [Pending]   — registered with the OS, not yet visible
 *  - [Active]    — running, accepting progress updates
 *  - [Stalled]   — running but no progress in > 30s (caller decides
 *                   the threshold); UI shows a "taking longer than
 *                   expected" banner without losing the progress bar
 *  - [Completed] — terminal: success
 *  - [Cancelled] — terminal: user-cancelled or upstream failure
 *
 * Pure state-machine transitions are exposed via the companion's
 * [next] and [isTerminal]. Used by the iOS Live Activity feature and
 * (in mirrored form) by the Android foreground-service progress
 * notification so both surfaces show identical lifecycle semantics.
 *
 * Transition table:
 *
 *  | from \ signal | Started | Progress | Stalled | Finished | Cancelled |
 *  |---------------|---------|----------|---------|----------|-----------|
 *  | Pending       | Active  | Pending  | Pending | Pending  | Cancelled |
 *  | Active        | Active  | Active   | Stalled | Completed| Cancelled |
 *  | Stalled       | Stalled | Active   | Stalled | Completed| Cancelled |
 *  | Completed     | absorbs all signals — terminal                       |
 *  | Cancelled     | absorbs all signals — terminal                       |
 *
 * Pure, side-effect-free, deterministic.
 */
enum class LiveActivityPhase {
    Pending,
    Active,
    Stalled,
    Completed,
    Cancelled,
    ;

    companion object {
        fun next(
            current: LiveActivityPhase,
            signal: LiveActivitySignal,
        ): LiveActivityPhase {
            if (isTerminal(current)) return current
            return when (current) {
                Pending ->
                    when (signal) {
                        LiveActivitySignal.Started -> Active
                        LiveActivitySignal.Cancelled -> Cancelled
                        LiveActivitySignal.Progress,
                        LiveActivitySignal.Stalled,
                        LiveActivitySignal.Finished,
                        -> Pending
                    }
                Active ->
                    when (signal) {
                        LiveActivitySignal.Started,
                        LiveActivitySignal.Progress,
                        -> Active
                        LiveActivitySignal.Stalled -> Stalled
                        LiveActivitySignal.Finished -> Completed
                        LiveActivitySignal.Cancelled -> Cancelled
                    }
                Stalled ->
                    when (signal) {
                        LiveActivitySignal.Progress -> Active
                        LiveActivitySignal.Started,
                        LiveActivitySignal.Stalled,
                        -> Stalled
                        LiveActivitySignal.Finished -> Completed
                        LiveActivitySignal.Cancelled -> Cancelled
                    }
                Completed,
                Cancelled,
                -> current
            }
        }

        fun isTerminal(phase: LiveActivityPhase): Boolean = phase == Completed || phase == Cancelled
    }
}

/** Signal emitted by the orchestrator into [LiveActivityPhase.next]. */
enum class LiveActivitySignal {
    Started,
    Progress,
    Stalled,
    Finished,
    Cancelled,
}
