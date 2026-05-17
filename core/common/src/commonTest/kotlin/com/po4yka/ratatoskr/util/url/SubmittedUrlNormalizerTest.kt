package com.po4yka.ratatoskr.util.url

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SubmittedUrlNormalizerTest {
    @Test
    fun `null, empty, and whitespace-only inputs are Invalid`() {
        assertEquals(SubmittedUrlNormalizer.Result.Invalid, SubmittedUrlNormalizer.normalize(null))
        assertEquals(SubmittedUrlNormalizer.Result.Invalid, SubmittedUrlNormalizer.normalize(""))
        assertEquals(SubmittedUrlNormalizer.Result.Invalid, SubmittedUrlNormalizer.normalize("   "))
        assertEquals(SubmittedUrlNormalizer.Result.Invalid, SubmittedUrlNormalizer.normalize("\t\n"))
    }

    @Test
    fun `https URLs pass through verbatim after whitespace trim`() {
        val result = SubmittedUrlNormalizer.normalize("  https://example.com/post  ")
        assertIs<SubmittedUrlNormalizer.Result.Normalized>(result)
        assertEquals("https://example.com/post", result.url)
    }

    @Test
    fun `http is upgraded to https — the app never submits cleartext`() {
        // The backend rejects cleartext submissions; the client upgrades
        // silently so a user who types "http://example.com" gets a
        // successful summary rather than a confusing "network" error.
        val result = SubmittedUrlNormalizer.normalize("http://example.com/post")
        assertIs<SubmittedUrlNormalizer.Result.Normalized>(result)
        assertEquals("https://example.com/post", result.url)
    }

    @Test
    fun `scheme matching is case-insensitive`() {
        // Browsers normalize the scheme to lower; we match the same
        // semantics so a pasted "HTTPS://" still parses correctly.
        val a = SubmittedUrlNormalizer.normalize("HTTPS://example.com")
        assertIs<SubmittedUrlNormalizer.Result.Normalized>(a)
        assertEquals("https://example.com", a.url)

        val b = SubmittedUrlNormalizer.normalize("Http://example.com/x")
        assertIs<SubmittedUrlNormalizer.Result.Normalized>(b)
        assertEquals("https://example.com/x", b.url)
    }

    @Test
    fun `bare host gets the https scheme prepended`() {
        // The submit screen accepts pasted hostnames without protocol —
        // the friendliest behavior is to assume https rather than reject.
        val result = SubmittedUrlNormalizer.normalize("example.com/article")
        assertIs<SubmittedUrlNormalizer.Result.Normalized>(result)
        assertEquals("https://example.com/article", result.url)
    }

    @Test
    fun `ratatoskr scheme is preserved as a registered deep-link`() {
        // The app registers `ratatoskr://` for inbound deep-links; a
        // user pasting one into the submit field should not have it
        // rewritten to https.
        val result = SubmittedUrlNormalizer.normalize("ratatoskr://summary/abc-123")
        assertIs<SubmittedUrlNormalizer.Result.Normalized>(result)
        assertEquals("ratatoskr://summary/abc-123", result.url)
    }

    @Test
    fun `javascript scheme is Unsupported — XSS defense`() {
        // Any `javascript:` URL is an injection vector. Refuse to submit
        // it; the WebView fallback would otherwise execute the payload.
        val result = SubmittedUrlNormalizer.normalize("javascript:alert(1)")
        assertEquals(SubmittedUrlNormalizer.Result.Unsupported, result)
    }

    @Test
    fun `file and data URLs are Unsupported`() {
        // file:// and data: URLs are not valid sources for the summarizer
        // and pose privacy / size risks if accidentally submitted.
        assertEquals(
            SubmittedUrlNormalizer.Result.Unsupported,
            SubmittedUrlNormalizer.normalize("file:///etc/passwd"),
        )
        assertEquals(
            SubmittedUrlNormalizer.Result.Unsupported,
            SubmittedUrlNormalizer.normalize("data:text/html,foo"),
        )
    }

    @Test
    fun `ftp and other historical schemes are Unsupported`() {
        assertEquals(
            SubmittedUrlNormalizer.Result.Unsupported,
            SubmittedUrlNormalizer.normalize("ftp://server/file"),
        )
        assertEquals(
            SubmittedUrlNormalizer.Result.Unsupported,
            SubmittedUrlNormalizer.normalize("mailto:user@example.com"),
        )
    }

    @Test
    fun `inputs containing a space are Invalid — not a URL`() {
        // The submit screen accepts a single URL; a multi-word paste is
        // almost certainly the user typing a search query.
        assertEquals(
            SubmittedUrlNormalizer.Result.Invalid,
            SubmittedUrlNormalizer.normalize("not a url"),
        )
        assertEquals(
            SubmittedUrlNormalizer.Result.Invalid,
            SubmittedUrlNormalizer.normalize("multiple spaces here"),
        )
    }

    @Test
    fun `bare hosts without dots are Invalid — not a real domain`() {
        // "localhost" is technically reachable on a dev device, but the
        // production app submits to a remote backend; a host without
        // a dot is almost certainly user typo not a URL.
        assertEquals(
            SubmittedUrlNormalizer.Result.Invalid,
            SubmittedUrlNormalizer.normalize("localhost"),
        )
        assertEquals(
            SubmittedUrlNormalizer.Result.Invalid,
            SubmittedUrlNormalizer.normalize("notadomain"),
        )
    }
}
