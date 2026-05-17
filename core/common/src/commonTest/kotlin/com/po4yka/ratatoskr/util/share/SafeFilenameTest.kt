package com.po4yka.ratatoskr.util.share

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SafeFilenameTest {
    @Test
    fun `passes through a clean title unchanged`() {
        assertEquals("Reading Notes", SafeFilename.sanitize("Reading Notes"))
    }

    @Test
    fun `strips Windows-forbidden punctuation`() {
        // Windows refuses to create files containing < > : " / \ | ? *
        // — the canonical NTFS reserved set. The sanitizer drops them
        // outright rather than substituting, so two consecutive forbidden
        // chars collapse rather than producing a noise-filled name.
        assertEquals("titlewithslashes", SafeFilename.sanitize("title/with\\slashes"))
        assertEquals("colon and pipe", SafeFilename.sanitize("colon: and| pipe"))
        assertEquals("question mark star", SafeFilename.sanitize("question? mark* star"))
        assertEquals("angle brackets", SafeFilename.sanitize("<angle> <brackets>"))
        assertEquals("doublequote", SafeFilename.sanitize("double\"quote"))
    }

    @Test
    fun `strips control characters and DEL`() {
        // Pasted CRLF or null bytes have no business in a filename — they
        // crash some platform file APIs and confuse shell tooling.
        val withControl = "Hello" + Char(0x09) + "World" + Char(0x0A) + "End" + Char(0x7F)
        assertEquals("HelloWorldEnd", SafeFilename.sanitize(withControl))
    }

    @Test
    fun `trims leading and trailing spaces`() {
        assertEquals("Title", SafeFilename.sanitize("   Title   "))
    }

    @Test
    fun `trims leading and trailing dots`() {
        // Windows refuses to create a file with a trailing dot — the dot
        // is stripped on creation, which silently changes the name.
        // Strip both leading and trailing for consistency.
        assertEquals("Title", SafeFilename.sanitize("...Title..."))
    }

    @Test
    fun `preserves dots in the middle of the name`() {
        // The "extension dot" is a legal part of the filename — only
        // edge dots are stripped.
        assertEquals("v1.0.beta", SafeFilename.sanitize("v1.0.beta"))
    }

    @Test
    fun `null, empty, blank, all-forbidden, and all-dot inputs collapse to fallback`() {
        // Without a meaningful sanitized form, the file would land at
        // disk path ending in "/" — the caller chooses the fallback so
        // it can localize ("untitled" vs "без названия").
        assertEquals("untitled", SafeFilename.sanitize(null))
        assertEquals("untitled", SafeFilename.sanitize(""))
        assertEquals("untitled", SafeFilename.sanitize("    "))
        assertEquals("untitled", SafeFilename.sanitize("///\\\\???"))
        assertEquals("untitled", SafeFilename.sanitize("........."))
    }

    @Test
    fun `allows the caller to override the fallback string`() {
        assertEquals("без названия", SafeFilename.sanitize("", fallback = "без названия"))
        assertEquals("без названия", SafeFilename.sanitize(null, fallback = "без названия"))
    }

    @Test
    fun `Windows reserved names get an underscore suffix — case insensitive`() {
        // CON, PRN, AUX, NUL, COM1-9, LPT1-9 are reserved device names.
        // Even today's Windows refuses to open them as plain files.
        assertEquals("CON_", SafeFilename.sanitize("CON"))
        assertEquals("PRN_", SafeFilename.sanitize("PRN"))
        assertEquals("aux_", SafeFilename.sanitize("aux"))
        assertEquals("NuL_", SafeFilename.sanitize("NuL"))
        assertEquals("COM1_", SafeFilename.sanitize("COM1"))
        assertEquals("lpt9_", SafeFilename.sanitize("lpt9"))
    }

    @Test
    fun `Windows reserved names matching only the prefix are not modified`() {
        // "CONFIG" starts with CON but is not reserved — only the exact
        // device name triggers the suffix. False-positive guard.
        assertEquals("CONFIG", SafeFilename.sanitize("CONFIG"))
        assertEquals("COM10", SafeFilename.sanitize("COM10"))
        assertEquals("LPT0", SafeFilename.sanitize("LPT0"))
    }

    @Test
    fun `result is capped at the platform-safe max length`() {
        val long = "a".repeat(300)
        val sanitized = SafeFilename.sanitize(long)
        assertEquals(SafeFilename.MAX_LENGTH, sanitized.length)
        assertTrue(sanitized.all { it == 'a' })
    }

    @Test
    fun `non-Latin characters survive sanitization`() {
        // Cyrillic, CJK, emoji are all legal on modern filesystems.
        // The sanitizer must not blanket-reject Unicode.
        assertEquals("Заметки 2026", SafeFilename.sanitize("Заметки 2026"))
        assertEquals("笔记 一月", SafeFilename.sanitize("笔记 一月"))
        assertEquals("📚 Library", SafeFilename.sanitize("📚 Library"))
    }

    @Test
    fun `sanitize is idempotent — running twice yields the same name`() {
        val once = SafeFilename.sanitize("...My/Bad\\Title???   ")
        val twice = SafeFilename.sanitize(once)
        assertEquals(once, twice)
        assertEquals("MyBadTitle", once)
    }
}
