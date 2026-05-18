package com.po4yka.ratatoskr.util.locale

import com.po4yka.ratatoskr.domain.model.LanguagePreference

/**
 * How the host platform's language applier behaves when handed a
 * [LanguagePreference] change. Captures the platform asymmetry without
 * reaching for a platform-specific marker:
 *  - [AppliesImmediately] — Android via `AppCompatDelegate.setApplicationLocales`,
 *    takes effect on the next composition.
 *  - [RequiresAppRestart] — iOS writes `AppleLanguages` to `UserDefaults`,
 *    which only the next process launch reads.
 *  - [NoOp] — Desktop is a dev target; nothing to apply.
 */
enum class LanguageHostBehavior {
    AppliesImmediately,
    RequiresAppRestart,
    NoOp,
}

/**
 * Outcome of feeding a proposed [LanguagePreference] change through
 * [LanguageChangeAdvisor]. The caller pattern-matches:
 *  - [NoChange] — do nothing (same preference, or host is a no-op).
 *  - [ApplyNow] — call the platform applier; UI updates this frame.
 *  - [ApplyAfterRestart] — call the platform applier and surface the polite
 *    restart-required Frost toast.
 */
sealed interface LanguageChangePlan {
    data object NoChange : LanguageChangePlan

    data class ApplyNow(val newPreference: LanguagePreference) : LanguageChangePlan

    data class ApplyAfterRestart(val newPreference: LanguagePreference) : LanguageChangePlan
}

/**
 * Pure decision atom that composes with the existing
 * [LocaleTagParser]-backed `LanguagePreferenceApplier`. The Settings →
 * Appearance picker hands its `(previous, new, host)` triple here before
 * calling the platform applier, so the platform code does not have to
 * re-derive whether a restart toast is needed.
 *
 * Pure, side-effect-free, deterministic.
 */
object LanguageChangeAdvisor {
    fun advise(
        previous: LanguagePreference,
        new: LanguagePreference,
        host: LanguageHostBehavior,
    ): LanguageChangePlan {
        if (previous == new) return LanguageChangePlan.NoChange
        return when (host) {
            LanguageHostBehavior.AppliesImmediately -> LanguageChangePlan.ApplyNow(newPreference = new)
            LanguageHostBehavior.RequiresAppRestart -> LanguageChangePlan.ApplyAfterRestart(newPreference = new)
            LanguageHostBehavior.NoOp -> LanguageChangePlan.NoChange
        }
    }
}
