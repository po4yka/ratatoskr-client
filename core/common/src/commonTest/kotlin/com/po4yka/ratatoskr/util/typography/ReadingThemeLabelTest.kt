package com.po4yka.ratatoskr.util.typography

import com.po4yka.ratatoskr.domain.model.ReadingTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReadingThemeLabelTest {
    @Test
    fun `MONO_LIGHT — canonical label MONO Light`() {
        // Pin the spec-mandated label strings from the issue. These are
        // also the EN defaults the Compose Resources file mirrors.
        assertEquals("MONO Light", ReadingThemeLabel.canonicalLabel(ReadingTheme.MONO_LIGHT))
    }

    @Test
    fun `MONO_DARK — canonical label MONO Dark`() {
        assertEquals("MONO Dark", ReadingThemeLabel.canonicalLabel(ReadingTheme.MONO_DARK))
    }

    @Test
    fun `SEPIA — canonical label Sepia`() {
        assertEquals("Sepia", ReadingThemeLabel.canonicalLabel(ReadingTheme.SEPIA))
    }

    @Test
    fun `HIGH_CONTRAST — canonical label High Contrast`() {
        assertEquals("High Contrast", ReadingThemeLabel.canonicalLabel(ReadingTheme.HIGH_CONTRAST))
    }

    @Test
    fun `every enum entry has a non-blank canonical label`() {
        // The picker auto-populates from ReadingTheme.entries; if a new
        // variant lands without a label, the picker shows an empty bracket.
        // Pin exhaustiveness here so the compile-time `when` plus this
        // assertion catch every new variant.
        ReadingTheme.entries.forEach { theme ->
            val label = ReadingThemeLabel.canonicalLabel(theme)
            assertTrue(label.isNotBlank(), "Missing canonical label for $theme")
        }
    }

    @Test
    fun `byCanonicalLabel — round-trip parses every canonical label`() {
        ReadingTheme.entries.forEach { theme ->
            val label = ReadingThemeLabel.canonicalLabel(theme)
            val parsed = ReadingThemeLabel.byCanonicalLabel(label)
            assertNotNull(parsed, "No inverse parse for $label")
            assertEquals(theme, parsed, "Inverse parse mismatch for $label")
        }
    }

    @Test
    fun `byCanonicalLabel — unknown label returns null`() {
        // The picker's localized RU label feeds back through the
        // resource layer, not this atom. Anything not in the canonical
        // EN list returns null so the caller can route to a graceful
        // fallback rather than mismatching to an arbitrary theme.
        assertNull(ReadingThemeLabel.byCanonicalLabel("Темная"))
        assertNull(ReadingThemeLabel.byCanonicalLabel(""))
        assertNull(ReadingThemeLabel.byCanonicalLabel("   "))
    }

    @Test
    fun `byCanonicalLabel — case-sensitive on canonical strings`() {
        // The labels are spec'd in mixed case. Pin so a future
        // case-insensitive shortcut does not silently accept "mono light"
        // and break the round-trip contract from canonicalLabel().
        assertNull(ReadingThemeLabel.byCanonicalLabel("mono light"))
    }

    @Test
    fun `canonicalLabel is deterministic`() {
        val a = ReadingThemeLabel.canonicalLabel(ReadingTheme.SEPIA)
        val b = ReadingThemeLabel.canonicalLabel(ReadingTheme.SEPIA)
        assertEquals(a, b)
    }
}
