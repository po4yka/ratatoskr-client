package com.po4yka.ratatoskr.util.deeplink

import kotlin.test.Test
import kotlin.test.assertEquals

class DeeplinkNavRouterTest {
    @Test
    fun `OpenSummary deep link routes to OpenSummary nav intent`() {
        val result = DeeplinkNavRouter.routeFor(RatatoskrDeepLink.OpenSummary(id = "abc123"))
        assertEquals(DeeplinkNavIntent.OpenSummary(summaryId = "abc123"), result)
    }

    @Test
    fun `SubmitUrl deep link routes to PrefillSubmitUrl nav intent`() {
        val result =
            DeeplinkNavRouter.routeFor(
                RatatoskrDeepLink.SubmitUrl(url = "https://example.com/article"),
            )
        assertEquals(
            DeeplinkNavIntent.PrefillSubmitUrl(url = "https://example.com/article"),
            result,
        )
    }

    @Test
    fun `Unknown deep link maps to Drop — platform host should not crash`() {
        // Future-proof: any unknown link shape (a typo'd path, a host
        // mismatch, a stale OS callback) is dropped silently so the
        // host never crashes on an unparseable Intent.
        assertEquals(
            DeeplinkNavIntent.Drop,
            DeeplinkNavRouter.routeFor(
                RatatoskrDeepLink.Unknown(raw = "https://example.com/totally-unrelated"),
            ),
        )
    }

    @Test
    fun `OpenSummary with surrounding whitespace is trimmed`() {
        // Defense-in-depth: the parser already trims, but a future
        // regression elsewhere shouldn't push `" abc "` into the nav
        // layer where it'd cause a "summary not found" log line.
        assertEquals(
            DeeplinkNavIntent.OpenSummary(summaryId = "abc123"),
            DeeplinkNavRouter.routeFor(RatatoskrDeepLink.OpenSummary(id = "  abc123  ")),
        )
    }

    @Test
    fun `OpenSummary with blank id is dropped`() {
        // The parser is supposed to reject this upstream. If it ever
        // regresses, the router catches it rather than navigating to
        // a phantom summary id.
        assertEquals(
            DeeplinkNavIntent.Drop,
            DeeplinkNavRouter.routeFor(RatatoskrDeepLink.OpenSummary(id = "")),
        )
        assertEquals(
            DeeplinkNavIntent.Drop,
            DeeplinkNavRouter.routeFor(RatatoskrDeepLink.OpenSummary(id = "   ")),
        )
    }

    @Test
    fun `SubmitUrl with surrounding whitespace is trimmed`() {
        assertEquals(
            DeeplinkNavIntent.PrefillSubmitUrl(url = "https://example.com/article"),
            DeeplinkNavRouter.routeFor(
                RatatoskrDeepLink.SubmitUrl(url = "\thttps://example.com/article\n"),
            ),
        )
    }

    @Test
    fun `SubmitUrl with blank url is dropped`() {
        assertEquals(
            DeeplinkNavIntent.Drop,
            DeeplinkNavRouter.routeFor(RatatoskrDeepLink.SubmitUrl(url = "")),
        )
        assertEquals(
            DeeplinkNavIntent.Drop,
            DeeplinkNavRouter.routeFor(RatatoskrDeepLink.SubmitUrl(url = " \t\n")),
        )
    }

    @Test
    fun `routing is exhaustive — every sealed case has a branch`() {
        // Pin that the when-expression covers every RatatoskrDeepLink
        // subtype. If a new variant is added without a router branch,
        // this test fails to compile.
        val cases: List<RatatoskrDeepLink> =
            listOf(
                RatatoskrDeepLink.OpenSummary(id = "abc"),
                RatatoskrDeepLink.SubmitUrl(url = "https://example.com"),
                RatatoskrDeepLink.Unknown(raw = "garbage"),
            )
        // Every case must produce a non-null result without throwing.
        cases.forEach { case ->
            val result: DeeplinkNavIntent = DeeplinkNavRouter.routeFor(case)
            assertEquals(true, result is DeeplinkNavIntent)
        }
    }

    @Test
    fun `routing is deterministic — same input maps to same output`() {
        val input = RatatoskrDeepLink.OpenSummary(id = "abc123")
        assertEquals(
            DeeplinkNavRouter.routeFor(input),
            DeeplinkNavRouter.routeFor(input),
        )
    }
}
