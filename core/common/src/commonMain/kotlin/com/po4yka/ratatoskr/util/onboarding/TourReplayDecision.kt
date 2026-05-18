package com.po4yka.ratatoskr.util.onboarding

import kotlin.math.max

/**
 * Pure decision atom for whether to show the onboarding tour overlay
 * on app start. Composes three persisted signals:
 *  - `everCompleted`        — has the user ever finished the tour?
 *  - `lastSeenTourVersion`  — the tour content version at last completion
 *  - `currentTourVersion`   — the tour content version baked into this
 *                              build (bumped when a new pane is added)
 *
 * Decision tree:
 *  - First run (`!everCompleted`) → [Action.ShowFirstRun] with the
 *    welcome framing.
 *  - Same version (`lastSeen == current`) → [Action.Skip].
 *  - New tour version (`lastSeen < current`) → [Action.ShowReplay]
 *    with the "what's new" framing.
 *  - Downgrade (`lastSeen > current`) → [Action.Skip]. Never replay
 *    something the user has already seen a strictly newer version of
 *    (staged-rollout regression / app downgrade defense).
 *
 * Defensive: inconsistent state (`!everCompleted && lastSeenVersion > 0`)
 * falls back to [Action.ShowFirstRun] so a write race doesn't silently
 * skip onboarding. Negative versions are clamped to `0`.
 *
 * Pure, side-effect-free, deterministic.
 */
object TourReplayDecision {
    enum class Action {
        ShowFirstRun,
        ShowReplay,
        Skip,
    }

    fun decide(
        everCompleted: Boolean,
        lastSeenTourVersion: Int,
        currentTourVersion: Int,
    ): Action {
        val safeLast = max(0, lastSeenTourVersion)
        val safeCurrent = max(0, currentTourVersion)

        if (!everCompleted) return Action.ShowFirstRun
        if (safeCurrent > safeLast) return Action.ShowReplay
        return Action.Skip
    }
}
