package com.po4yka.ratatoskr.util.share

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SafariPagePayloadValidatorTest {
    @Test
    fun `well-formed payload with title and https url`() {
        val result =
            SafariPagePayloadValidator.validate(
                rawTitle = "Example Article",
                rawUrl = "https://example.com/articles/42",
            )
        assertEquals(
            SafariPagePayload(
                title = "Example Article",
                url = "https://example.com/articles/42",
            ),
            result,
        )
    }

    @Test
    fun `well-formed payload with title and http url`() {
        // Safari extensions occasionally surface http URLs from
        // legacy redirects; accept them — the upstream submitter
        // upgrades to https via SubmittedUrlNormalizer.
        val result =
            SafariPagePayloadValidator.validate(
                rawTitle = "Article",
                rawUrl = "http://example.com/article",
            )
        assertEquals(
            SafariPagePayload(title = "Article", url = "http://example.com/article"),
            result,
        )
    }

    @Test
    fun `null url is rejected — payload requires a URL`() {
        assertNull(
            SafariPagePayloadValidator.validate(rawTitle = "Title", rawUrl = null),
        )
    }

    @Test
    fun `empty url is rejected`() {
        assertNull(
            SafariPagePayloadValidator.validate(rawTitle = "Title", rawUrl = ""),
        )
        assertNull(
            SafariPagePayloadValidator.validate(rawTitle = "Title", rawUrl = "   "),
        )
    }

    @Test
    fun `non-http schemes are rejected — file, ftp, javascript`() {
        // Safari should never hand us a non-http scheme, but extensions
        // can be invoked with weird state. Reject defensively.
        assertNull(
            SafariPagePayloadValidator.validate(
                rawTitle = "x",
                rawUrl = "javascript:alert(1)",
            ),
        )
        assertNull(
            SafariPagePayloadValidator.validate(
                rawTitle = "x",
                rawUrl = "file:///etc/passwd",
            ),
        )
        assertNull(
            SafariPagePayloadValidator.validate(
                rawTitle = "x",
                rawUrl = "ftp://example.com/file",
            ),
        )
    }

    @Test
    fun `url scheme matching is case-insensitive — HTTPS uppercase`() {
        // Some legacy iOS extensions normalize the scheme to uppercase.
        val result =
            SafariPagePayloadValidator.validate(
                rawTitle = "Article",
                rawUrl = "HTTPS://example.com",
            )
        // Original casing is preserved in the output URL; the caller
        // normalizes via SubmittedUrlNormalizer if needed.
        assertEquals(
            SafariPagePayload(title = "Article", url = "HTTPS://example.com"),
            result,
        )
    }

    @Test
    fun `null title is accepted and stored as empty`() {
        // The page-context dict may omit the title key. Fall back to
        // empty so the caller can use the URL host instead.
        val result =
            SafariPagePayloadValidator.validate(
                rawTitle = null,
                rawUrl = "https://example.com",
            )
        assertEquals(
            SafariPagePayload(title = "", url = "https://example.com"),
            result,
        )
    }

    @Test
    fun `whitespace is trimmed from title and url`() {
        val result =
            SafariPagePayloadValidator.validate(
                rawTitle = "  Article  ",
                rawUrl = "  https://example.com  ",
            )
        assertEquals(
            SafariPagePayload(title = "Article", url = "https://example.com"),
            result,
        )
    }

    @Test
    fun `displayTitle prefers title, falls back to host`() {
        val titled =
            SafariPagePayloadValidator.validate(
                rawTitle = "Article",
                rawUrl = "https://example.com/path",
            )!!
        assertEquals("Article", titled.displayTitle())

        val untitled =
            SafariPagePayloadValidator.validate(
                rawTitle = null,
                rawUrl = "https://example.com/path",
            )!!
        assertEquals("example.com", untitled.displayTitle())
    }

    @Test
    fun `displayTitle falls back to full url when host extraction fails`() {
        val malformed =
            SafariPagePayloadValidator.validate(
                rawTitle = "",
                rawUrl = "https:///",
            )
        // Both empty title AND a degenerate URL → fall back to URL.
        // Still produces a payload (URL is non-empty and scheme matches),
        // even though it's malformed beyond the scheme check.
        // displayTitle() returns the URL itself in this case.
        assertEquals("https:///", malformed?.displayTitle())
    }

    @Test
    fun `validation is deterministic`() {
        val a =
            SafariPagePayloadValidator.validate(
                rawTitle = "Title",
                rawUrl = "https://example.com",
            )
        val b =
            SafariPagePayloadValidator.validate(
                rawTitle = "Title",
                rawUrl = "https://example.com",
            )
        assertEquals(a, b)
    }
}
