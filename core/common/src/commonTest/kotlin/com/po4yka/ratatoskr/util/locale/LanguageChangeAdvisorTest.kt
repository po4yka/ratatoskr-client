package com.po4yka.ratatoskr.util.locale

import com.po4yka.ratatoskr.domain.model.LanguagePreference
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageChangeAdvisorTest {
    @Test
    fun `same preference — NoChange regardless of host`() {
        // Pin the no-op short-circuit: if the user re-selects the current
        // preference we never fire the platform applier, never show the
        // restart toast, never spend an event.
        assertEquals(
            LanguageChangePlan.NoChange,
            LanguageChangeAdvisor.advise(
                previous = LanguagePreference.English,
                new = LanguagePreference.English,
                host = LanguageHostBehavior.AppliesImmediately,
            ),
        )
        assertEquals(
            LanguageChangePlan.NoChange,
            LanguageChangeAdvisor.advise(
                previous = LanguagePreference.Russian,
                new = LanguagePreference.Russian,
                host = LanguageHostBehavior.RequiresAppRestart,
            ),
        )
        assertEquals(
            LanguageChangePlan.NoChange,
            LanguageChangeAdvisor.advise(
                previous = LanguagePreference.System,
                new = LanguagePreference.System,
                host = LanguageHostBehavior.NoOp,
            ),
        )
    }

    @Test
    fun `different preference on Android-style host — ApplyNow`() {
        // Android's AppCompatDelegate.setApplicationLocales takes effect
        // immediately, so the advisor commits to ApplyNow.
        assertEquals(
            LanguageChangePlan.ApplyNow(newPreference = LanguagePreference.Russian),
            LanguageChangeAdvisor.advise(
                previous = LanguagePreference.English,
                new = LanguagePreference.Russian,
                host = LanguageHostBehavior.AppliesImmediately,
            ),
        )
    }

    @Test
    fun `different preference on iOS-style host — ApplyAfterRestart`() {
        // iOS writes AppleLanguages to UserDefaults — picked up only on the
        // next process launch. The advisor flags ApplyAfterRestart so the UI
        // can surface the polite restart toast.
        assertEquals(
            LanguageChangePlan.ApplyAfterRestart(newPreference = LanguagePreference.Russian),
            LanguageChangeAdvisor.advise(
                previous = LanguagePreference.English,
                new = LanguagePreference.Russian,
                host = LanguageHostBehavior.RequiresAppRestart,
            ),
        )
    }

    @Test
    fun `different preference on Desktop-style host — NoChange`() {
        // Desktop is a dev target only; the applier is a no-op. Treat as
        // NoChange so the picker UI does not even surface a confirmation
        // affordance — there is nothing to confirm.
        assertEquals(
            LanguageChangePlan.NoChange,
            LanguageChangeAdvisor.advise(
                previous = LanguagePreference.English,
                new = LanguagePreference.Russian,
                host = LanguageHostBehavior.NoOp,
            ),
        )
    }

    @Test
    fun `System to concrete on Android — ApplyNow`() {
        assertEquals(
            LanguageChangePlan.ApplyNow(newPreference = LanguagePreference.Russian),
            LanguageChangeAdvisor.advise(
                previous = LanguagePreference.System,
                new = LanguagePreference.Russian,
                host = LanguageHostBehavior.AppliesImmediately,
            ),
        )
    }

    @Test
    fun `concrete back to System on iOS — ApplyAfterRestart`() {
        // Resetting to System removes the AppleLanguages key on iOS, which
        // also only takes effect on next launch.
        assertEquals(
            LanguageChangePlan.ApplyAfterRestart(newPreference = LanguagePreference.System),
            LanguageChangeAdvisor.advise(
                previous = LanguagePreference.English,
                new = LanguagePreference.System,
                host = LanguageHostBehavior.RequiresAppRestart,
            ),
        )
    }

    @Test
    fun `advise is deterministic`() {
        val a =
            LanguageChangeAdvisor.advise(
                previous = LanguagePreference.English,
                new = LanguagePreference.Russian,
                host = LanguageHostBehavior.AppliesImmediately,
            )
        val b =
            LanguageChangeAdvisor.advise(
                previous = LanguagePreference.English,
                new = LanguagePreference.Russian,
                host = LanguageHostBehavior.AppliesImmediately,
            )
        assertEquals(a, b)
    }
}
