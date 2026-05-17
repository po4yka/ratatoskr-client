package com.po4yka.ratatoskr.util.share

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ClipboardUrlParserTest {
    @Test
    fun returnsHttpsUrlFromPlainText() {
        assertEquals(
            "https://example.com/post/42",
            ClipboardUrlParser.firstHttpUrl("Check this out: https://example.com/post/42 — cool."),
        )
    }

    @Test
    fun returnsHttpUrlFromMixedContent() {
        assertEquals(
            "http://example.org/x",
            ClipboardUrlParser.firstHttpUrl("first http://example.org/x then nothing"),
        )
    }

    @Test
    fun stripsTrailingSentencePunctuation() {
        assertEquals("https://example.com/x", ClipboardUrlParser.firstHttpUrl("read https://example.com/x."))
        assertEquals("https://example.com/x", ClipboardUrlParser.firstHttpUrl("see (https://example.com/x)"))
        assertEquals("https://example.com/x", ClipboardUrlParser.firstHttpUrl("[https://example.com/x]"))
        assertEquals("https://example.com/x", ClipboardUrlParser.firstHttpUrl("look https://example.com/x;"))
    }

    @Test
    fun returnsNullWhenNoHttpUrlPresent() {
        assertNull(ClipboardUrlParser.firstHttpUrl("just text"))
        assertNull(ClipboardUrlParser.firstHttpUrl(""))
        assertNull(ClipboardUrlParser.firstHttpUrl("ftp://example.com/x"))
        assertNull(ClipboardUrlParser.firstHttpUrl("mailto:foo@example.com"))
    }
}
