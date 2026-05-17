package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.domain.model.LanguagePreference
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSettingsApi::class)
class LanguagePreferenceRepositoryImplTest {
    private fun newRepo(
        seed: Map<String, Any> = emptyMap(),
    ): Pair<LanguagePreferenceRepositoryImpl, ObservableSettings> {
        val settings = MapSettings()
        seed.forEach { (k, v) -> if (v is String) settings.putString(k, v) }
        return LanguagePreferenceRepositoryImpl(settings) to settings
    }

    @Test
    fun `default on first launch is System when nothing has been written`() =
        runTest {
            val (repo, _) = newRepo()
            assertEquals(LanguagePreference.System, repo.getLanguagePreference().first())
        }

    @Test
    fun `write then read round-trips the preference`() =
        runTest {
            val (repo, _) = newRepo()
            repo.updateLanguagePreference(LanguagePreference.Russian)
            assertEquals(LanguagePreference.Russian, repo.getLanguagePreference().first())

            repo.updateLanguagePreference(LanguagePreference.English)
            assertEquals(LanguagePreference.English, repo.getLanguagePreference().first())
        }

    @Test
    fun `picking System clears the override and the flow reports it`() =
        runTest {
            val (repo, _) = newRepo()
            repo.updateLanguagePreference(LanguagePreference.Russian)
            repo.updateLanguagePreference(LanguagePreference.System)
            assertEquals(LanguagePreference.System, repo.getLanguagePreference().first())
        }

    @Test
    fun `unknown stored key collapses to System rather than throwing`() =
        runTest {
            // Future enum value got persisted, app downgraded — fall back to OS-driven locale.
            val (repo, _) = newRepo(seed = mapOf("in_app_language_preference" to "Klingon"))
            assertEquals(LanguagePreference.System, repo.getLanguagePreference().first())
        }

    @Test
    fun `storage key is the bare enum name so dumps stay human-readable`() =
        runTest {
            // Storage format is part of the public contract — guard against an
            // accidental switch to ordinals which would silently rotate user choices
            // on the next enum-value addition.
            val (repo, settings) = newRepo()
            repo.updateLanguagePreference(LanguagePreference.Russian)
            assertEquals("Russian", settings.getString("in_app_language_preference", ""))
        }
}
