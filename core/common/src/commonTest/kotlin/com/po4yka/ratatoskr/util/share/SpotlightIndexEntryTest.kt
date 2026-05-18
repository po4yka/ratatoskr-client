package com.po4yka.ratatoskr.util.share

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SpotlightIndexEntryTest {
    @Test
    fun `typical summary — entry has uniqueIdentifier, title, description, deepLink`() {
        val entry =
            SpotlightIndexEntry.build(
                summaryId = "abc-123",
                title = "Kotlin 2.0 release notes",
                snippet = "Kotlin 2.0 ships the new K2 compiler frontend as default.",
            )
        assertEquals("summary:abc-123", entry?.uniqueIdentifier)
        assertEquals("Kotlin 2.0 release notes", entry?.title)
        assertEquals("Kotlin 2.0 ships the new K2 compiler frontend as default.", entry?.contentDescription)
        assertEquals("ratatoskr://summary/abc-123", entry?.deepLinkUri)
    }

    @Test
    fun `blank summaryId — null entry, indexing skipped`() {
        // CoreSpotlight requires a non-empty domain identifier; a blank
        // id would crash the indexer. Atom rejects upstream so the
        // caller can `?.let { CSSearchableItem(... entry ...) }` safely.
        assertNull(
            SpotlightIndexEntry.build(
                summaryId = "",
                title = "anything",
                snippet = "",
            ),
        )
        assertNull(
            SpotlightIndexEntry.build(
                summaryId = "   ",
                title = "anything",
                snippet = "",
            ),
        )
    }

    @Test
    fun `blank title — fallback label`() {
        // Spotlight needs a display title; blank reads as "broken result"
        // in the search list, so the atom substitutes a neutral fallback.
        val entry =
            SpotlightIndexEntry.build(
                summaryId = "abc-123",
                title = "   ",
                snippet = "body",
            )
        assertEquals(SpotlightIndexEntry.UNTITLED_FALLBACK, entry?.title)
    }

    @Test
    fun `long snippet — truncated to MAX_DESCRIPTION_CHARS`() {
        // Spotlight description has a soft display budget. Truncate so
        // the result row doesn't degrade to a single illegible blob.
        val long = "a".repeat(SpotlightIndexEntry.MAX_DESCRIPTION_CHARS * 2)
        val entry =
            SpotlightIndexEntry.build(
                summaryId = "abc-123",
                title = "Title",
                snippet = long,
            )
        assertTrue(
            (entry?.contentDescription?.length ?: 0) <= SpotlightIndexEntry.MAX_DESCRIPTION_CHARS,
            "description must be capped at MAX_DESCRIPTION_CHARS",
        )
    }

    @Test
    fun `empty snippet — empty description, not null`() {
        // Avoid a nullable contentDescription so the iOS layer never has
        // to branch on null when constructing CSSearchableItemAttributeSet.
        val entry =
            SpotlightIndexEntry.build(
                summaryId = "abc-123",
                title = "Title",
                snippet = "",
            )
        assertEquals("", entry?.contentDescription)
    }

    @Test
    fun `summaryId is trimmed`() {
        val entry =
            SpotlightIndexEntry.build(
                summaryId = "  abc-123  ",
                title = "Title",
                snippet = "",
            )
        assertEquals("summary:abc-123", entry?.uniqueIdentifier)
        assertEquals("ratatoskr://summary/abc-123", entry?.deepLinkUri)
    }

    @Test
    fun `title is trimmed`() {
        val entry =
            SpotlightIndexEntry.build(
                summaryId = "abc-123",
                title = "  Kotlin notes  ",
                snippet = "",
            )
        assertEquals("Kotlin notes", entry?.title)
    }

    @Test
    fun `snippet is trimmed`() {
        val entry =
            SpotlightIndexEntry.build(
                summaryId = "abc-123",
                title = "T",
                snippet = "  body  ",
            )
        assertEquals("body", entry?.contentDescription)
    }

    @Test
    fun `build is deterministic`() {
        val a =
            SpotlightIndexEntry.build(
                summaryId = "abc-123",
                title = "T",
                snippet = "body",
            )
        val b =
            SpotlightIndexEntry.build(
                summaryId = "abc-123",
                title = "T",
                snippet = "body",
            )
        assertEquals(a, b)
    }
}
