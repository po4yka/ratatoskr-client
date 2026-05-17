package com.po4yka.ratatoskr.util.onboarding

/**
 * Reducer for the first-run guided tour overlay. The tour walks new users through
 * the three Frost coachmarks: (1) submit a URL via the FAB, (2) share into the app
 * from any browser, (3) pin the widget to the home screen.
 *
 * Persisted state is a single boolean — `tour_completed` in `UserPreferences`,
 * default false. The transient runtime fields (`step`, `deferred`) live only in
 * memory and are re-derived on each launch from the saved flag and whether the
 * launch originated from a deep link.
 *
 * Visibility rule: `step >= 0` means the overlay is on-screen. Setting `step = -1`
 * with `completed = true` is the "done" terminal; `step = -1` with
 * `completed = false` is the "deferred for deep-link, will surface next plain
 * launch" intermediate.
 *
 * Pure data + pure transitions — no Compose types, no platform deps. The Compose
 * overlay is a thin renderer over this state and the persistence write is a
 * one-liner on `completed`.
 */
data class OnboardingTourState(
    val step: Int,
    val completed: Boolean,
    val deferred: Boolean,
) {
    val isVisible: Boolean
        get() = step in 0 until STEP_COUNT

    fun next(): OnboardingTourState {
        if (!isVisible) return this
        val nextStep = step + 1
        return if (nextStep >= STEP_COUNT) HIDDEN_COMPLETE else copy(step = nextStep)
    }

    fun skip(): OnboardingTourState = HIDDEN_COMPLETE

    fun replay(): OnboardingTourState = VISIBLE_AT_START

    companion object {
        const val STEP_COUNT: Int = 3

        private val HIDDEN_COMPLETE =
            OnboardingTourState(step = -1, completed = true, deferred = false)
        private val HIDDEN_DEFERRED =
            OnboardingTourState(step = -1, completed = false, deferred = true)
        private val VISIBLE_AT_START =
            OnboardingTourState(step = 0, completed = false, deferred = false)

        fun initial(
            savedCompleted: Boolean,
            hasPendingDeepLink: Boolean,
        ): OnboardingTourState =
            when {
                savedCompleted -> HIDDEN_COMPLETE
                hasPendingDeepLink -> HIDDEN_DEFERRED
                else -> VISIBLE_AT_START
            }
    }
}
