package com.po4yka.ratatoskr.util.locale

import com.po4yka.ratatoskr.domain.model.LanguagePreference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocaleTagParserTest {
    @Test
    fun `canonicalize lowercases the language subtag`() {
        assertEquals("en", LocaleTagParser.canonicalize("EN"))
        assertEquals("ru", LocaleTagParser.canonicalize("RU"))
        assertEquals("en", LocaleTagParser.canonicalize("En"))
    }

    @Test
    fun `canonicalize uppercases the region subtag`() {
        // IETF BCP47: region subtag (2 letters or 3 digits) is uppercase.
        // Android intent extras sometimes arrive with the region in lower
        // case; iOS NSLocale tags arrive canonical. Normalizing here means
        // downstream comparisons can string-equal.
        assertEquals("en-US", LocaleTagParser.canonicalize("en-us"))
        assertEquals("en-US", LocaleTagParser.canonicalize("EN-US"))
        assertEquals("es-419", LocaleTagParser.canonicalize("es-419"))
    }

    @Test
    fun `canonicalize titlecases the script subtag — 4-letter subtag rule`() {
        // BCP47: script subtag is title-case (first letter uppercase, rest
        // lower). The CLDR uses tags like zh-Hans-CN and zh-Hant-HK.
        assertEquals("zh-Hans-CN", LocaleTagParser.canonicalize("zh-hans-cn"))
        assertEquals("zh-Hant-HK", LocaleTagParser.canonicalize("ZH-HANT-HK"))
    }

    @Test
    fun `canonicalize replaces Java-style underscore with BCP47 dash`() {
        // Java Locale.toString() and the Kotlin shim it inherits use `_` for
        // separators (en_US). BCP47 wants `-`. Both APIs end up as inputs to
        // the parser; canonicalization keeps the storage shape stable.
        assertEquals("en-US", LocaleTagParser.canonicalize("en_US"))
        assertEquals("zh-Hans-CN", LocaleTagParser.canonicalize("zh_hans_cn"))
    }

    @Test
    fun `canonicalize trims surrounding whitespace`() {
        assertEquals("en", LocaleTagParser.canonicalize("  en  "))
        assertEquals("ru-RU", LocaleTagParser.canonicalize("\tru-ru\n"))
    }

    @Test
    fun `canonicalize returns null for null, blank, and empty inputs — System sentinel`() {
        // The LanguagePreference.System case writes no tag to OS storage —
        // null is the wire form for "OS default".
        assertNull(LocaleTagParser.canonicalize(null))
        assertNull(LocaleTagParser.canonicalize(""))
        assertNull(LocaleTagParser.canonicalize("   "))
    }

    @Test
    fun `canonicalize preserves variant subtags as lowercase`() {
        // Locale variants (5+ letter subtags after region) are lowercase
        // per BCP47. "de-DE-1996" is the German pre-1996 orthography variant.
        assertEquals("de-DE-1996", LocaleTagParser.canonicalize("DE-DE-1996"))
    }

    @Test
    fun `resolve returns the matching LanguagePreference for an exact tag`() {
        assertEquals(LanguagePreference.English, LocaleTagParser.resolve("en"))
        assertEquals(LanguagePreference.Russian, LocaleTagParser.resolve("ru"))
    }

    @Test
    fun `resolve matches regional variants to their base-language preference`() {
        // Android's per-app locale list often arrives as "en-US" or "ru-RU".
        // The app only ships per-language Compose Resources, not per-region,
        // so the picker collapses regional variants to the base language.
        assertEquals(LanguagePreference.English, LocaleTagParser.resolve("en-US"))
        assertEquals(LanguagePreference.English, LocaleTagParser.resolve("en-GB"))
        assertEquals(LanguagePreference.Russian, LocaleTagParser.resolve("ru-RU"))
    }

    @Test
    fun `resolve is case-insensitive at the language subtag — runs canonicalize first`() {
        assertEquals(LanguagePreference.English, LocaleTagParser.resolve("EN"))
        assertEquals(LanguagePreference.Russian, LocaleTagParser.resolve("Ru"))
    }

    @Test
    fun `resolve returns System for unknown locales — fallback path`() {
        // A user whose OS reports "fr-FR" still gets the app in System mode;
        // the LanguagePreferenceApplier will hand "fr-FR" back to the OS,
        // which falls through to English (the app's default Compose Resources).
        assertEquals(LanguagePreference.System, LocaleTagParser.resolve("fr"))
        assertEquals(LanguagePreference.System, LocaleTagParser.resolve("zh-Hans-CN"))
    }

    @Test
    fun `resolve returns System for null, blank, and empty inputs`() {
        assertEquals(LanguagePreference.System, LocaleTagParser.resolve(null))
        assertEquals(LanguagePreference.System, LocaleTagParser.resolve(""))
        assertEquals(LanguagePreference.System, LocaleTagParser.resolve("   "))
    }

    @Test
    fun `canonicalize is idempotent — running it twice yields the same result`() {
        // Defensive property: a stored canonical tag round-tripping through
        // the parser must not drift. Pins that downstream caches keyed on
        // the canonical tag stay stable.
        val once = LocaleTagParser.canonicalize("EN-us")
        val twice = LocaleTagParser.canonicalize(once)
        assertEquals("en-US", once)
        assertEquals(once, twice)
    }
}
