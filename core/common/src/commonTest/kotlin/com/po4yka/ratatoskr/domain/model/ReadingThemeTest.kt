package com.po4yka.ratatoskr.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ReadingThemeTest {
    @Test
    fun `every enum value round-trips through the storage key`() {
        // If a future contributor renames an enum value without bumping a migration,
        // existing users would silently fall back to MONO_LIGHT. This guards that.
        ReadingTheme.entries.forEach { theme ->
            val key = ReadingTheme.toStorageKey(theme)
            assertEquals(theme, ReadingTheme.fromStorageKey(key), "round-trip broke for $theme")
        }
    }

    @Test
    fun `unknown storage key falls back to MONO_LIGHT instead of crashing`() {
        // Future enum value got persisted, app downgraded, key no longer matches.
        // The fallback keeps the reading surface readable.
        assertEquals(ReadingTheme.MONO_LIGHT, ReadingTheme.fromStorageKey("DEUTERANOPIA_DARK"))
    }

    @Test
    fun `null storage key falls back to MONO_LIGHT`() {
        // First launch — settings returns null for an unset key.
        assertEquals(ReadingTheme.MONO_LIGHT, ReadingTheme.fromStorageKey(null))
    }

    @Test
    fun `storage keys are the bare enum names so they remain human-readable in settings dumps`() {
        // Storage format is part of the public contract. Don't accidentally switch to ordinals.
        assertEquals("MONO_LIGHT", ReadingTheme.toStorageKey(ReadingTheme.MONO_LIGHT))
        assertEquals("SEPIA", ReadingTheme.toStorageKey(ReadingTheme.SEPIA))
        assertEquals("HIGH_CONTRAST", ReadingTheme.toStorageKey(ReadingTheme.HIGH_CONTRAST))
    }
}
