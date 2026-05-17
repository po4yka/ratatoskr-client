package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.LanguagePreference
import kotlinx.coroutines.flow.Flow

/**
 * Persists the user's in-app language choice locally. Separate from
 * [UserPreferencesRepository] which round-trips to the server `/v1/user/preferences`
 * endpoint — the language toggle is a client-side affordance that does not need to be
 * known to the backend (the iOS / Android OS sources of truth differ enough that
 * trying to sync them creates more problems than it solves).
 *
 * Implementations back this with `multiplatform-settings` so the choice survives
 * process death and (where the user has enabled cloud-backed Settings sync) reinstall.
 *
 * The repository **only** persists. Applying the choice — calling
 * `AppCompatDelegate.setApplicationLocales(...)` on Android or writing
 * `UserDefaults.AppleLanguages` on iOS — is the responsibility of the platform-side
 * applier that consumes this preference, kept separate so the local store stays
 * commonMain-pure and testable.
 */
interface LanguagePreferenceRepository {
    /**
     * Cold flow of the user's current preference. First emission is always synchronous
     * (no I/O on the resolution path) so callers can read it during composition.
     */
    fun getLanguagePreference(): Flow<LanguagePreference>

    suspend fun updateLanguagePreference(preference: LanguagePreference)
}
