package com.po4yka.ratatoskr.util.url

import kotlin.test.Test
import kotlin.test.assertEquals

class DisplayUrlTrimmerTest {
    @Test
    fun `returns empty string for null, blank, and empty inputs`() {
        assertEquals("", DisplayUrlTrimmer.trim(null, MAX))
        assertEquals("", DisplayUrlTrimmer.trim("", MAX))
        assertEquals("", DisplayUrlTrimmer.trim("   ", MAX))
    }

    @Test
    fun `returns empty string when maxLength is zero or negative`() {
        // Defensive: a caller wiring this to a dynamic width must get a
        // safe value back, not a substring exception.
        assertEquals("", DisplayUrlTrimmer.trim("https://example.com", 0))
        assertEquals("", DisplayUrlTrimmer.trim("https://example.com", -5))
    }

    @Test
    fun `strips the https protocol prefix`() {
        assertEquals("example.com", DisplayUrlTrimmer.trim("https://example.com", MAX))
    }

    @Test
    fun `strips the http protocol prefix`() {
        assertEquals("example.com", DisplayUrlTrimmer.trim("http://example.com", MAX))
    }

    @Test
    fun `strips a leading www subdomain after the protocol`() {
        // The banner button has a tight glyph budget — "www." is wasted
        // display space and adds nothing meaningful for the user.
        assertEquals("example.com", DisplayUrlTrimmer.trim("https://www.example.com", MAX))
    }

    @Test
    fun `strips a trailing slash on the path`() {
        assertEquals("example.com", DisplayUrlTrimmer.trim("https://example.com/", MAX))
    }

    @Test
    fun `passes through a short URL with path when it fits the budget`() {
        assertEquals("example.com/post", DisplayUrlTrimmer.trim("https://example.com/post", MAX))
    }

    @Test
    fun `inserts ellipsis between host and last segment for long paths`() {
        // Long URLs collapse to host + "/…/" + last-path-segment so the
        // banner still tells the user which page they're about to submit.
        val long = "https://example.com/blog/2026/05/17/very-long-article-slug"
        val trimmed = DisplayUrlTrimmer.trim(long, 40)
        assertEquals("example.com/…/very-long-article-slug", trimmed)
    }

    @Test
    fun `falls back to host when host plus ellipsis plus last segment still overflows`() {
        // If even the collapsed form is wider than the budget, drop the
        // ellipsis suffix and render the host alone — better than a
        // confusing partial path.
        val long = "https://example.com/very/long/extremely-long-final-segment-name"
        val trimmed = DisplayUrlTrimmer.trim(long, 14)
        assertEquals("example.com", trimmed)
    }

    @Test
    fun `hard truncates with ellipsis when even the host overflows`() {
        // Pathological case: a 60-char hostname against a 10-char budget.
        // The terminal "…" reserves one glyph so the budget includes it.
        val long = "https://averyverylonghostname.example.com"
        val trimmed = DisplayUrlTrimmer.trim(long, 10)
        assertEquals("averyvery…", trimmed)
        assertEquals(10, trimmed.length)
    }

    @Test
    fun `preserves an explicit www prefix that is part of the hostname`() {
        // "www2.example.com" is a real subdomain, not a vanity "www." — the
        // stripper must only fire on the literal "www." prefix.
        assertEquals("www2.example.com", DisplayUrlTrimmer.trim("https://www2.example.com", MAX))
    }

    @Test
    fun `preserves an explicit query string when the budget allows`() {
        val url = "https://example.com/search?q=ratatoskr"
        assertEquals("example.com/search?q=ratatoskr", DisplayUrlTrimmer.trim(url, MAX))
    }

    @Test
    fun `is idempotent — running twice yields the same display string`() {
        // Defensive property: a stored trimmed URL round-tripping through
        // the trimmer must not drift. Stable shape simplifies caching.
        val once = DisplayUrlTrimmer.trim("https://www.example.com/posts/42", MAX)
        val twice = DisplayUrlTrimmer.trim(once, MAX)
        assertEquals(once, twice)
    }

    private companion object {
        const val MAX = 80
    }
}
