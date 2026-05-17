package com.po4yka.ratatoskr.domain.model

/**
 * User's preferred app language choice. Persisted via [LanguagePreferenceRepository] and
 * applied at the platform layer:
 *  - Android: `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))`,
 *    or `getEmptyLocaleList()` for [System].
 *  - iOS: writes `["<tag>"]` to `UserDefaults.standard.set(_:forKey: "AppleLanguages")`, or
 *    removes the key entirely for [System].
 *
 * [System] is the default for new installs — no override is written, so the OS chooses the
 * locale based on the device's preferred-languages list.
 *
 * Concrete language values stay narrow on purpose: the app ships English + Russian Compose
 * Resources today; broadening the enum means dropping in matching `values-*` directories
 * first. The picker UI auto-populates from the entries here, so adding a new language is
 * a one-line change plus the resource directory.
 */
enum class LanguagePreference(val tag: String?) {
    /** No override — Android / iOS choose the locale from the OS preferred-languages list. */
    System(tag = null),
    English(tag = "en"),
    Russian(tag = "ru"),
    ;

    companion object {
        /**
         * Round-trips through the [LanguagePreferenceRepository] persistence layer. Unknown
         * keys (e.g., a language that was removed in a later release) collapse to [System]
         * rather than crashing — the OS-level locale list still serves as a sensible default.
         */
        fun fromStorageKey(key: String?): LanguagePreference = entries.firstOrNull { it.name == key } ?: System

        /** Mirror of [fromStorageKey] — kept as a function for symmetry at call sites. */
        fun toStorageKey(preference: LanguagePreference): String = preference.name
    }
}
