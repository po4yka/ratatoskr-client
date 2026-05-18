package com.po4yka.ratatoskr.util.share

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClipboardBannerLabelTest {
    @Test
    fun `null url — iOS-style unknown fallback`() {
        // iOS Show(urlIfKnown = null) path: the system has confirmed the
        // clipboard holds a URL but reading it would trigger the paste
        // prompt, so the banner shows a generic call-to-action.
        assertEquals(
            "Submit URL from clipboard",
            ClipboardBannerLabel.format(urlIfKnown = null),
        )
    }

    @Test
    fun `blank url — same fallback as null`() {
        assertEquals(
            "Submit URL from clipboard",
            ClipboardBannerLabel.format(urlIfKnown = ""),
        )
        assertEquals(
            "Submit URL from clipboard",
            ClipboardBannerLabel.format(urlIfKnown = "   "),
        )
    }

    @Test
    fun `short https URL — scheme stripped, prefixed Submit`() {
        assertEquals(
            "Submit example.com",
            ClipboardBannerLabel.format(urlIfKnown = "https://example.com"),
        )
    }

    @Test
    fun `short http URL — http scheme stripped too`() {
        assertEquals(
            "Submit example.com",
            ClipboardBannerLabel.format(urlIfKnown = "http://example.com"),
        )
    }

    @Test
    fun `mixed-case scheme — stripped case-insensitively`() {
        assertEquals(
            "Submit example.com",
            ClipboardBannerLabel.format(urlIfKnown = "HTTPS://example.com"),
        )
    }

    @Test
    fun `trailing slash — stripped for compactness`() {
        assertEquals(
            "Submit example.com",
            ClipboardBannerLabel.format(urlIfKnown = "https://example.com/"),
        )
    }

    @Test
    fun `long URL — truncated with ellipsis, total length bounded`() {
        val long = "https://example.com/articles/2026/05/very-long-slug-that-runs-on-and-on-and-on"
        val label = ClipboardBannerLabel.format(urlIfKnown = long, maxLabelLength = 32)
        assertTrue(label.length <= 32, "label '$label' exceeded budget 32")
        assertTrue(label.startsWith("Submit "), "label '$label' must start with 'Submit '")
        assertTrue(label.endsWith("…"), "label '$label' must end with ellipsis when truncated")
    }

    @Test
    fun `URL fits exactly in budget — no ellipsis`() {
        // "Submit example.com" is 18 chars. Budget 18 must not truncate.
        val label = ClipboardBannerLabel.format(urlIfKnown = "https://example.com", maxLabelLength = 18)
        assertEquals("Submit example.com", label)
        assertTrue(!label.endsWith("…"))
    }

    @Test
    fun `tiny budget below minimum — coerced up to keep label legible`() {
        // A degenerate caller passes maxLabelLength = 3. The atom must
        // never return empty or "Su…" — coerce up so the bracket reads
        // as something the user can act on.
        val label = ClipboardBannerLabel.format(urlIfKnown = "https://example.com/path", maxLabelLength = 3)
        assertTrue(label.startsWith("Submit "), "label '$label' must keep 'Submit ' prefix")
        assertTrue(label.length >= "Submit X…".length)
    }

    @Test
    fun `format is deterministic`() {
        val a = ClipboardBannerLabel.format(urlIfKnown = "https://example.com/article")
        val b = ClipboardBannerLabel.format(urlIfKnown = "https://example.com/article")
        assertEquals(a, b)
    }
}
