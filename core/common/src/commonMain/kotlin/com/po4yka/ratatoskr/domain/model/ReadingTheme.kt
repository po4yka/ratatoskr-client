package com.po4yka.ratatoskr.domain.model

/**
 * User's reading-surface palette choice. Persisted as part of
 * [ReadingPreferences] and resolved to a concrete `FrostColors` palette in
 * `core/ui` via `paletteFor(theme)`.
 *
 * Only applies inside the reading detail surface — the rest of the app stays
 * in Frost MONO regardless of this selection.
 *
 * `MONO_LIGHT` / `MONO_DARK` inherit the project default light/dark; `SEPIA`
 * and `HIGH_CONTRAST` switch to their dedicated palettes regardless of system
 * dark-mode.
 *
 * Lives in `core/common` (rather than `core/ui` where it originated) so the
 * domain layer can include it in [ReadingPreferences] without a UI-tier
 * dependency leaking into common domain code.
 */
enum class ReadingTheme {
    MONO_LIGHT,
    MONO_DARK,
    SEPIA,
    HIGH_CONTRAST,
    ;

    companion object {
        /** Round-trips through [ReadingPreferences] persistence — case-sensitive enum names. */
        fun fromStorageKey(key: String?): ReadingTheme = entries.firstOrNull { it.name == key } ?: MONO_LIGHT

        /** Mirror of [fromStorageKey] — kept as a function for symmetry at call sites. */
        fun toStorageKey(theme: ReadingTheme): String = theme.name
    }
}
