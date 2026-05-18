package com.po4yka.ratatoskr.util.share

import com.po4yka.ratatoskr.util.deeplink.DeeplinkNavIntent
import com.po4yka.ratatoskr.util.url.SubmittedUrlNormalizer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SafariPayloadIngestorTest {
    @Test
    fun `well-formed https payload becomes PrefillSubmitUrl intent`() {
        val intent =
            SafariPayloadIngestor.ingest(
                rawTitle = "Article",
                rawUrl = "https://example.com/article",
            )
        assertEquals(
            DeeplinkNavIntent.PrefillSubmitUrl(url = "https://example.com/article"),
            intent,
        )
    }

    @Test
    fun `http payload is silently upgraded to https via SubmittedUrlNormalizer`() {
        // The normalizer atom promotes http to https; the ingestor
        // composes that so the downstream submit flow always sees
        // https.
        val intent =
            SafariPayloadIngestor.ingest(
                rawTitle = "Article",
                rawUrl = "http://example.com/article",
            )
        assertEquals(
            DeeplinkNavIntent.PrefillSubmitUrl(url = "https://example.com/article"),
            intent,
        )
    }

    @Test
    fun `null url is rejected — Drop`() {
        // The validator rejects null url; ingestor turns that into
        // Drop (rather than crashing) so the share-extension caller
        // can just dispatch the intent without further checks.
        assertEquals(
            DeeplinkNavIntent.Drop,
            SafariPayloadIngestor.ingest(rawTitle = "Title", rawUrl = null),
        )
    }

    @Test
    fun `non-http scheme rejected — Drop`() {
        assertEquals(
            DeeplinkNavIntent.Drop,
            SafariPayloadIngestor.ingest(rawTitle = "x", rawUrl = "javascript:alert(1)"),
        )
        assertEquals(
            DeeplinkNavIntent.Drop,
            SafariPayloadIngestor.ingest(rawTitle = "x", rawUrl = "file:///etc/passwd"),
        )
    }

    @Test
    fun `https with bare host passes through — SubmittedUrlNormalizer only blocks bare hosts without scheme`() {
        // SubmittedUrlNormalizer rejects single-label hosts only when
        // they arrive without an http(s) scheme prefix. The Safari
        // payload always supplies the scheme, so a single-label host
        // (e.g. an internal test server like https://localhost) passes
        // through. Pin this behavior so the ingestor's contract is
        // documented — the upstream submitter is the layer that can
        // gate on host policies.
        val intent =
            SafariPayloadIngestor.ingest(
                rawTitle = "x",
                rawUrl = "https://localhost",
            )
        assertEquals(
            DeeplinkNavIntent.PrefillSubmitUrl(url = "https://localhost"),
            intent,
        )
    }

    @Test
    fun `whitespace is trimmed during the validate stage`() {
        // The validator trims; the normalizer normalizes scheme casing;
        // the ingestor composes both.
        val intent =
            SafariPayloadIngestor.ingest(
                rawTitle = "  Article  ",
                rawUrl = "  HTTPS://example.com/article  ",
            )
        assertEquals(
            DeeplinkNavIntent.PrefillSubmitUrl(url = "https://example.com/article"),
            intent,
        )
    }

    @Test
    fun `title is intentionally discarded — submit-url flow re-fetches it`() {
        // The submit flow re-fetches the canonical title server-side;
        // the title from the Safari page-context is a hint, not the
        // value persisted. Pin that the ingestor only carries url
        // through to the nav intent.
        val intent =
            SafariPayloadIngestor.ingest(
                rawTitle = "Stale Title",
                rawUrl = "https://example.com",
            )
        if (intent is DeeplinkNavIntent.PrefillSubmitUrl) {
            assertEquals("https://example.com", intent.url)
        } else {
            error("expected PrefillSubmitUrl, got $intent")
        }
    }

    @Test
    fun `direct underlying access via validate returns the payload`() {
        // The ingestor's public surface is the nav intent, but the
        // validator atom remains independently callable for callers
        // that need the title for analytics. Pin the round-trip.
        val payload =
            SafariPagePayloadValidator.validate(
                rawTitle = "Article",
                rawUrl = "https://example.com",
            )
        assertEquals(
            SafariPagePayload(title = "Article", url = "https://example.com"),
            payload,
        )
        // Bonus: confirm the normalizer call site is sane by re-deriving.
        val normalized = SubmittedUrlNormalizer.normalize(payload?.url)
        val normalizedUrl = (normalized as? SubmittedUrlNormalizer.Result.Normalized)?.url
        assertEquals("https://example.com", normalizedUrl)
    }

    @Test
    fun `ingest is deterministic`() {
        val a =
            SafariPayloadIngestor.ingest(
                rawTitle = "x",
                rawUrl = "https://example.com",
            )
        val b =
            SafariPayloadIngestor.ingest(
                rawTitle = "x",
                rawUrl = "https://example.com",
            )
        assertEquals(a, b)
    }

    @Test
    fun `assertNull guard — empty inputs both null`() {
        // Sanity: neither helper returns the wrong sentinel.
        assertNull(SafariPagePayloadValidator.validate(null, null))
    }
}
