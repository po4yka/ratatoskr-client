package com.po4yka.ratatoskr.util.observability

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SentryEventScrubberTest {
    @Test
    fun `plain prose passes through unchanged`() {
        val text = "Failed to load summaries because the server returned 500."
        assertEquals(text, SentryEventScrubber.scrub(text))
    }

    @Test
    fun `email addresses are redacted to a stable token`() {
        // Spec: "No PII in crash payloads." Email is the canonical PII shape
        // server-validation errors echo back in their messages.
        val result = SentryEventScrubber.scrub("Reset email sent to user@example.com please check")

        assertFalse(result.contains("user@example.com"))
        assertTrue(result.contains(SentryEventScrubber.EMAIL_REDACTION))
        assertTrue(result.contains("please check"))
    }

    @Test
    fun `multiple emails in the same payload are all redacted`() {
        val result =
            SentryEventScrubber.scrub("From: alice@example.com, To: bob@example.com")
        val occurrences = result.split(SentryEventScrubber.EMAIL_REDACTION).size - 1
        assertEquals(2, occurrences)
        assertFalse(result.contains("alice@"))
        assertFalse(result.contains("bob@"))
    }

    @Test
    fun `Bearer tokens in Authorization header text are redacted but the scheme survives`() {
        // The token is what we strip; "Bearer" stays so the engineer reading
        // Sentry can still recognize the auth-header context.
        val raw = "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.fake.signature_abcdef1234"
        val result = SentryEventScrubber.scrub(raw)

        assertFalse(result.contains("eyJhbGciOiJIUzI1NiJ9"))
        assertTrue(result.contains("Bearer"))
        assertTrue(result.contains(SentryEventScrubber.TOKEN_REDACTION))
    }

    @Test
    fun `case variations of Bearer match — case-insensitive header parsing`() {
        // HTTP allows "bearer", "BEARER", etc. Real-world libraries normalize
        // differently; the redactor catches all variants.
        val result = SentryEventScrubber.scrub("authorization: bearer abcdef1234567890XYZ")
        assertFalse(result.contains("abcdef1234567890"))
    }

    @Test
    fun `URL credentials in the userinfo segment are redacted`() {
        // https://user:pass@host accidentally leaks via Ktor exception
        // messages quoting the full request URL. Strip the user:pass.
        val raw = "Request failed: https://alice:s3cret@api.example.com/v1/summary timed out"
        val result = SentryEventScrubber.scrub(raw)

        assertFalse(result.contains("alice:s3cret"))
        assertFalse(result.contains("alice"))
        assertFalse(result.contains("s3cret"))
        assertTrue(result.contains("api.example.com/v1/summary"))
    }

    @Test
    fun `URLs without credentials are not over-redacted`() {
        // A plain https URL is fine to surface — the engineer needs the
        // endpoint to triage. Only credentials and tokens go.
        val raw = "Request failed: https://api.example.com/v1/summary timed out"
        assertEquals(raw, SentryEventScrubber.scrub(raw))
    }

    @Test
    fun `query-parameter tokens are redacted but the parameter name survives`() {
        // ?token=abc&access_token=xyz patterns leak credentials through
        // request-URL logs. Keep the key (helps debugging) but strip the value.
        val raw = "GET https://api.example.com/v1?token=secret123&utm_source=email"
        val result = SentryEventScrubber.scrub(raw)

        assertFalse(result.contains("secret123"))
        assertTrue(result.contains("token=${SentryEventScrubber.TOKEN_REDACTION}"))
        // utm_source is not sensitive — must pass through intact for triage.
        assertTrue(result.contains("utm_source=email"))
    }

    @Test
    fun `multiple sensitive query parameters are all redacted`() {
        // The redactor handles every known sensitive key in one pass —
        // partial coverage would leak a credential by accident.
        val raw =
            "GET ?token=t1&api_key=k1&password=p1&utm_source=foo&access_token=a1"
        val result = SentryEventScrubber.scrub(raw)

        assertFalse(result.contains("t1"))
        assertFalse(result.contains("k1"))
        assertFalse(result.contains("p1"))
        assertFalse(result.contains("a1"))
        assertTrue(result.contains("utm_source=foo"))
    }

    @Test
    fun `empty and blank input are passed through unchanged — no allocation overhead`() {
        assertEquals("", SentryEventScrubber.scrub(""))
        assertEquals("   ", SentryEventScrubber.scrub("   "))
    }

    @Test
    fun `mixed payload — email plus Bearer plus URL creds — all classes redacted in one pass`() {
        // Real Sentry events carry stacks with nested context. The scrubber
        // must handle a payload with several PII classes in one call.
        val raw =
            "User alice@example.com hit Bearer xyz123abc456def789 via " +
                "https://admin:pwd@api.example.com/notify"
        val result = SentryEventScrubber.scrub(raw)

        assertFalse(result.contains("alice@example.com"))
        assertFalse(result.contains("xyz123abc456def789"))
        assertFalse(result.contains("admin:pwd"))
        assertTrue(result.contains("api.example.com/notify"))
    }

    @Test
    fun `false-positive defense — strings that look like emails but lack a TLD are not redacted`() {
        // "@channel" Slack mentions, "@2x" image qualifiers, "user@local"
        // dev addresses all lack a TLD. The redactor must not eat them.
        val raw = "Mention @channel saw asset@2x size"
        assertEquals(raw, SentryEventScrubber.scrub(raw))
    }

    @Test
    fun `false-positive defense — Bearer followed by a short word is not a token`() {
        // "Bearer of bad news" should not redact "of bad news". The token
        // threshold (>= 16 chars) defends against the false positive.
        val raw = "Bearer of bad news arrived"
        assertEquals(raw, SentryEventScrubber.scrub(raw))
    }
}
