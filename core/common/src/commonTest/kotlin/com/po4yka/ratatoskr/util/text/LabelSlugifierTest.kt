package com.po4yka.ratatoskr.util.text

import kotlin.test.Test
import kotlin.test.assertEquals

class LabelSlugifierTest {
    @Test
    fun `lowercases and joins ASCII words with hyphens`() {
        assertEquals("reading-list-2026", LabelSlugifier.slugify("Reading List 2026"))
        assertEquals("hello-world", LabelSlugifier.slugify("Hello World"))
    }

    @Test
    fun `collapses runs of whitespace into a single hyphen`() {
        // Pasted titles often have double spaces or tabs — the slug
        // should not contain "--" once collapsed.
        assertEquals("multi-space", LabelSlugifier.slugify("multi    space"))
        assertEquals("tab-name", LabelSlugifier.slugify("tab\tname"))
    }

    @Test
    fun `strips leading and trailing non-alphanumerics`() {
        // The hyphen between non-alphanumerics around the edge would
        // otherwise leak as a leading or trailing dash.
        assertEquals("title", LabelSlugifier.slugify("---title---"))
        assertEquals("title", LabelSlugifier.slugify("///title///"))
        assertEquals("title", LabelSlugifier.slugify(" ! title ! "))
    }

    @Test
    fun `replaces punctuation with hyphens and collapses runs`() {
        // Colons, slashes, parens, and other punctuation all collapse to
        // the single inter-word separator.
        assertEquals(
            "poy-123-bug-fix",
            LabelSlugifier.slugify("POY-123: bug fix"),
        )
        assertEquals(
            "reading-personal",
            LabelSlugifier.slugify("Reading (Personal)"),
        )
    }

    @Test
    fun `null, empty, and fully-stripped inputs collapse to fallback`() {
        assertEquals("untitled", LabelSlugifier.slugify(null))
        assertEquals("untitled", LabelSlugifier.slugify(""))
        assertEquals("untitled", LabelSlugifier.slugify("   "))
        assertEquals("untitled", LabelSlugifier.slugify("------"))
        assertEquals("untitled", LabelSlugifier.slugify("!!!@@@###"))
    }

    @Test
    fun `caller can override the fallback string`() {
        assertEquals("без-названия", LabelSlugifier.slugify("", fallback = "без-названия"))
        assertEquals("без-названия", LabelSlugifier.slugify("---", fallback = "без-названия"))
    }

    @Test
    fun `preserves non-Latin letters as the slug script`() {
        // Russian-locale users get Cyrillic slugs, not transliterated
        // ASCII; the app's collection IDs are not exposed in URLs that
        // need ASCII normalization. CJK and emoji-adjacent letters
        // follow the same rule.
        assertEquals("заметки-2026", LabelSlugifier.slugify("Заметки 2026"))
        assertEquals("笔记-一月", LabelSlugifier.slugify("笔记 一月"))
    }

    @Test
    fun `emoji and symbols are stripped — they are not letters or digits`() {
        // Emojis are not Char.isLetterOrDigit, so they're treated as
        // word separators. "Library" survives, emoji collapses away.
        assertEquals("library", LabelSlugifier.slugify("📚 Library"))
    }

    @Test
    fun `digit-only inputs are accepted as valid slugs`() {
        // "2026" or "v2" are legitimate collection names — the slug
        // contract permits digit-only output rather than coercing to
        // the fallback.
        assertEquals("2026", LabelSlugifier.slugify("2026"))
        assertEquals("v2", LabelSlugifier.slugify("v2"))
    }

    @Test
    fun `is idempotent — slugifying a slug returns the same slug`() {
        // Defensive property: caching a slug and re-slugifying on
        // round-trip must not drift. Critical for keying caches.
        val once = LabelSlugifier.slugify("My Reading List — 2026!")
        val twice = LabelSlugifier.slugify(once)
        assertEquals("my-reading-list-2026", once)
        assertEquals(once, twice)
    }
}
