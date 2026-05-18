package com.po4yka.ratatoskr.util.typography

import com.po4yka.ratatoskr.domain.model.ReadingTheme

/**
 * Canonical English label per [ReadingTheme] variant. These strings are
 * the EN defaults that mirror the `values/strings.xml` Compose Resources
 * entries — putting them in `commonMain` lets KMP tests assert the
 * picker spec without spinning up Compose Resources.
 *
 * Spec wording comes from
 * `wire-reading-theme-picker-bottom-sheet-and-design-md`:
 *  - MONO_LIGHT     → "MONO Light"
 *  - MONO_DARK      → "MONO Dark"
 *  - SEPIA          → "Sepia"
 *  - HIGH_CONTRAST  → "High Contrast"
 *
 * The picker UI is expected to read the localized string resource at
 * render time; this atom is the source of truth for the canonical form
 * and the inverse parse used by tests that exercise the round-trip.
 *
 * Pure, side-effect-free, deterministic.
 */
object ReadingThemeLabel {
    fun canonicalLabel(theme: ReadingTheme): String =
        when (theme) {
            ReadingTheme.MONO_LIGHT -> "MONO Light"
            ReadingTheme.MONO_DARK -> "MONO Dark"
            ReadingTheme.SEPIA -> "Sepia"
            ReadingTheme.HIGH_CONTRAST -> "High Contrast"
        }

    /**
     * Inverse of [canonicalLabel]. Case-sensitive on the spec'd label
     * strings; returns null for anything else (including localized RU
     * variants, blank input, and arbitrary user text). The localized-
     * label lookup belongs in the Compose Resources layer, not here.
     */
    fun byCanonicalLabel(label: String): ReadingTheme? =
        ReadingTheme.entries.firstOrNull { canonicalLabel(it) == label }
}
