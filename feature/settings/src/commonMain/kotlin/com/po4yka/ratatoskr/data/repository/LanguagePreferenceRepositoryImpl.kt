package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.domain.model.LanguagePreference
import com.po4yka.ratatoskr.domain.repository.LanguagePreferenceRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

/**
 * Persists the in-app language choice via `multiplatform-settings`. Storage key is
 * deliberately the bare enum name (`System`, `English`, `Russian`) so settings dumps
 * stay human-readable across platforms and a future migration can `rg` for the key
 * without parsing.
 */
@OptIn(ExperimentalSettingsApi::class)
@Single(binds = [LanguagePreferenceRepository::class])
class LanguagePreferenceRepositoryImpl(
    private val settings: ObservableSettings,
) : LanguagePreferenceRepository {
    override fun getLanguagePreference(): Flow<LanguagePreference> =
        settings.getStringFlow(KEY_LANGUAGE_PREFERENCE, LanguagePreference.System.name)
            .map(LanguagePreference::fromStorageKey)

    override suspend fun updateLanguagePreference(preference: LanguagePreference) {
        settings.putString(KEY_LANGUAGE_PREFERENCE, LanguagePreference.toStorageKey(preference))
    }

    companion object {
        private const val KEY_LANGUAGE_PREFERENCE = "in_app_language_preference"
    }
}
