package com.po4yka.ratatoskr.util.locale

import com.po4yka.ratatoskr.domain.model.LanguagePreference

/**
 * Canonicalizes IETF BCP47 locale tags and maps them to the app's
 * [LanguagePreference] enum. Used by the upcoming language-picker applier
 * (Android `AppCompatDelegate.setApplicationLocales`, iOS `AppleLanguages`
 * UserDefaults) to keep stored tags stable across casing / separator quirks
 * coming in from the OS and from share-extension intents.
 *
 * Canonicalization follows the BCP47 casing rules:
 *  - Language subtag (first): lowercase (`en`, `ru`)
 *  - Script subtag (4-letter, follows language): title case (`Hans`, `Latn`)
 *  - Region subtag (2-letter or 3-digit): uppercase letters / digits-as-is
 *    (`US`, `419`)
 *  - Variant subtag (anything longer): lowercase (`1996`, `fonipa`)
 *  - Java-style `_` separators are normalized to BCP47 `-`.
 *
 * Resolution collapses regional variants to the base-language preference:
 * `en-US` and `en-GB` both resolve to [LanguagePreference.English] because
 * the app ships per-language Compose Resources, not per-region. Unknown
 * locales fall through to [LanguagePreference.System] — the OS-level
 * locale list serves as the next-best default.
 *
 * Null / blank input collapses to "no override" — `null` for [canonicalize],
 * [LanguagePreference.System] for [resolve].
 */
object LocaleTagParser {
    fun canonicalize(rawTag: String?): String? {
        val trimmed = rawTag?.trim().orEmpty()
        if (trimmed.isEmpty()) return null
        return trimmed
            .replace('_', '-')
            .split('-')
            .filter { it.isNotEmpty() }
            .mapIndexed(::canonicalizeSubtag)
            .joinToString("-")
    }

    fun resolve(rawTag: String?): LanguagePreference {
        val canonical = canonicalize(rawTag) ?: return LanguagePreference.System
        val languageSubtag = canonical.substringBefore('-').lowercase()
        return LanguagePreference.entries.firstOrNull { it.tag == languageSubtag }
            ?: LanguagePreference.System
    }

    private fun canonicalizeSubtag(
        index: Int,
        subtag: String,
    ): String =
        when {
            index == 0 -> subtag.lowercase()
            subtag.length == 4 && subtag.all { it.isLetter() } ->
                subtag[0].uppercaseChar() + subtag.substring(1).lowercase()
            subtag.length == 2 && subtag.all { it.isLetter() } -> subtag.uppercase()
            subtag.length == 3 && subtag.all { it.isDigit() } -> subtag
            else -> subtag.lowercase()
        }
}
