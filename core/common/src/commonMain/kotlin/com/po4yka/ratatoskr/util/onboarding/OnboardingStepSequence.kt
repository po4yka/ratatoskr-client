package com.po4yka.ratatoskr.util.onboarding

/**
 * Sealed-but-enum step set for the first-run onboarding tour overlay.
 * The five entries map to the five Compose panes (Welcome card,
 * library-sync explainer, reading-tools demo, sharing/export demo,
 * completion). [Done] is the terminal state — once reached, the
 * overlay closes and a `tourCompleted = true` flag is persisted.
 */
enum class OnboardingStep {
    Welcome,
    LibrarySync,
    ReadingTools,
    Sharing,
    Done,
}

/**
 * Pure state-machine for [OnboardingStep] transitions consumed by
 * `wire-onboarding-tour-overlay`. Keeps the navigation logic out of
 * the Compose pane composables so they stay declarative.
 *
 * Boundary semantics:
 *  - [previous] from [OnboardingStep.Welcome] stays at Welcome — the
 *    first pane absorbs the system back gesture; the overlay's
 *    dismiss button handles closing.
 *  - [next] from [OnboardingStep.Done] stays at Done — the terminal
 *    absorbs a racing "Skip" / "Next" tap on the final pane so
 *    rapid double-tap can't transition past completion.
 *  - The middle of the sequence is fully reversible — `previous(next(x))
 *    == x` for any non-boundary `x`.
 *
 * Pure, side-effect-free, deterministic.
 */
object OnboardingStepSequence {
    val all: List<OnboardingStep> = OnboardingStep.entries.toList()

    fun next(current: OnboardingStep): OnboardingStep {
        val index = all.indexOf(current)
        return if (index == all.size - 1) current else all[index + 1]
    }

    fun previous(current: OnboardingStep): OnboardingStep {
        val index = all.indexOf(current)
        return if (index <= 0) current else all[index - 1]
    }

    fun isTerminal(step: OnboardingStep): Boolean = step == OnboardingStep.Done

    fun isStart(step: OnboardingStep): Boolean = step == OnboardingStep.Welcome
}
