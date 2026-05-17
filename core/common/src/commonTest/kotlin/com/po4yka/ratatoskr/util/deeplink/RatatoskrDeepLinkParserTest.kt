package com.po4yka.ratatoskr.util.deeplink

import com.po4yka.ratatoskr.util.deeplink.RatatoskrDeepLink.OpenSummary
import com.po4yka.ratatoskr.util.deeplink.RatatoskrDeepLink.SubmitUrl
import com.po4yka.ratatoskr.util.deeplink.RatatoskrDeepLink.Unknown
import kotlin.test.Test
import kotlin.test.assertEquals

class RatatoskrDeepLinkParserTest {
    @Test
    fun `verified universal link parses to OpenSummary with the path id`() {
        // Spec: "https://ratatoskr.po4yka.com/s/{id}" is the new Universal /
        // Asset Link surface the OS opens deterministically.
        val link = RatatoskrDeepLinkParser.parse("https://ratatoskr.po4yka.com/s/abc123")

        assertEquals(OpenSummary("abc123"), link)
    }

    @Test
    fun `custom-scheme summary URI parses to OpenSummary — widget fallback path`() {
        // Spec: "Keep ratatoskr:// scheme as fallback for the widget." The
        // RecentSummariesWidget builds intents with the custom scheme so its
        // PendingIntents survive App Link verification flakes.
        val link = RatatoskrDeepLinkParser.parse("ratatoskr://summary/abc123")

        assertEquals(OpenSummary("abc123"), link)
    }

    @Test
    fun `plain-http variant of the verified host is rejected — must be HTTPS`() {
        // Defends against accidental http:// links in old emails/notifications.
        // Asset Link verification only matches https, so http URLs cannot be
        // App-Link-verified and would always open the chooser.
        val link = RatatoskrDeepLinkParser.parse("http://ratatoskr.po4yka.com/s/abc123")

        assertEquals(Unknown("http://ratatoskr.po4yka.com/s/abc123"), link)
    }

    @Test
    fun `wrong-host HTTPS link is rejected — only the canonical host wins`() {
        // Phishing defense: we only honor our own host. An attacker who
        // controls "evil.com" cannot have their /s/abc123 path deep-link us.
        val link = RatatoskrDeepLinkParser.parse("https://evil.com/s/abc123")

        assertEquals(Unknown("https://evil.com/s/abc123"), link)
    }

    @Test
    fun `empty id segment is rejected as Unknown`() {
        // Defends against a misclick on a truncated share link. The OS would
        // route /s/ to us if the intent filter is path-prefix; we must reject
        // gracefully rather than open a summary with an empty id.
        assertEquals(
            Unknown("https://ratatoskr.po4yka.com/s/"),
            RatatoskrDeepLinkParser.parse("https://ratatoskr.po4yka.com/s/"),
        )
        assertEquals(
            Unknown("ratatoskr://summary/"),
            RatatoskrDeepLinkParser.parse("ratatoskr://summary/"),
        )
    }

    @Test
    fun `extra path segments after the id are rejected — strict path shape`() {
        // We only accept /s/{id}, not /s/{id}/highlight or /s/{id}/whatever.
        // Allowing extra segments would silently strip them and might surprise
        // a user who clicked /s/abc/highlight expecting to land on a highlight.
        val link =
            RatatoskrDeepLinkParser.parse("https://ratatoskr.po4yka.com/s/abc123/highlight")

        assertEquals(Unknown("https://ratatoskr.po4yka.com/s/abc123/highlight"), link)
    }

    @Test
    fun `query string after the id is ignored, not appended to it`() {
        // utm and trk parameters can hitch a ride from email clients. The id
        // is the path segment, and the rest is metadata we discard.
        val link =
            RatatoskrDeepLinkParser.parse(
                "https://ratatoskr.po4yka.com/s/abc123?utm_source=email&utm_medium=newsletter",
            )

        assertEquals(OpenSummary("abc123"), link)
    }

    @Test
    fun `custom-scheme submit-url decodes percent-encoded url query parameter`() {
        // Share extensions hand us percent-encoded URLs; the parser must decode
        // so the SubmitURLScreen pre-fills the actual URL the user wants to
        // summarize, not the encoded blob.
        val link =
            RatatoskrDeepLinkParser.parse(
                "ratatoskr://submit-url?url=https%3A%2F%2Fexample.com%2Farticle",
            )

        assertEquals(SubmitUrl("https://example.com/article"), link)
    }

    @Test
    fun `submit-url with no url parameter falls through to Unknown`() {
        // A submit-url path that didn't carry the url query parameter is
        // malformed — we don't open a blank submission screen.
        val raw = "ratatoskr://submit-url"
        assertEquals(Unknown(raw), RatatoskrDeepLinkParser.parse(raw))
    }

    @Test
    fun `blank input is rejected as Unknown without throwing`() {
        // Cold-start with no intent extras can hand us null/blank — we must
        // not throw.
        assertEquals(Unknown(""), RatatoskrDeepLinkParser.parse(""))
        assertEquals(Unknown("   "), RatatoskrDeepLinkParser.parse("   "))
    }

    @Test
    fun `case-insensitive scheme and host — defensive against OS normalization`() {
        // Some Android intent extras arrive with uppercased schemes; some
        // Universal Link payloads arrive with the host in mixed case. We
        // canonicalize so the comparison cannot reject a valid link.
        assertEquals(
            OpenSummary("xyz"),
            RatatoskrDeepLinkParser.parse("HTTPS://Ratatoskr.Po4yka.Com/s/xyz"),
        )
        assertEquals(
            OpenSummary("xyz"),
            RatatoskrDeepLinkParser.parse("RATATOSKR://summary/xyz"),
        )
    }

    @Test
    fun `unknown app-scheme host is rejected — strict host allowlist`() {
        // The custom scheme has a small fixed set of hosts (summary, submit-url).
        // Anything else is Unknown so a future host addition can't silently
        // be routed by an older client.
        val raw = "ratatoskr://unknown-future-route/abc"
        assertEquals(Unknown(raw), RatatoskrDeepLinkParser.parse(raw))
    }
}
