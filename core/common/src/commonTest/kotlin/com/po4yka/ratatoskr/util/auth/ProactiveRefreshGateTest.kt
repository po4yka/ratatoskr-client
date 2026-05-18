package com.po4yka.ratatoskr.util.auth

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalEncodingApi::class)
class ProactiveRefreshGateTest {
    @Test
    fun `valid unexpired token within grace — RefreshNotNeeded`() {
        val token = jwt("""{"exp":2000000000}""")
        assertEquals(
            ProactiveRefreshGate.Decision.RefreshNotNeeded,
            ProactiveRefreshGate.decide(
                accessToken = token,
                nowEpochSeconds = 1_500_000_000,
                graceSeconds = 60,
            ),
        )
    }

    @Test
    fun `expired access token — RefreshNow`() {
        val token = jwt("""{"exp":1000000000}""")
        assertEquals(
            ProactiveRefreshGate.Decision.RefreshNow,
            ProactiveRefreshGate.decide(
                accessToken = token,
                nowEpochSeconds = 1_500_000_000,
            ),
        )
    }

    @Test
    fun `token expiring inside the grace window — RefreshNow`() {
        // exp = now + 30, grace = 60 → within window → proactive refresh
        val token = jwt("""{"exp":1700000030}""")
        assertEquals(
            ProactiveRefreshGate.Decision.RefreshNow,
            ProactiveRefreshGate.decide(
                accessToken = token,
                nowEpochSeconds = 1_700_000_000,
                graceSeconds = 60,
            ),
        )
    }

    @Test
    fun `null access token — RefreshNow, no token to send`() {
        // A missing access token can't satisfy any request; ask the
        // refresher to mint a new pair rather than letting the request
        // proceed with no Authorization header.
        assertEquals(
            ProactiveRefreshGate.Decision.RefreshNow,
            ProactiveRefreshGate.decide(
                accessToken = null,
                nowEpochSeconds = 1_700_000_000,
            ),
        )
    }

    @Test
    fun `blank access token — RefreshNow`() {
        assertEquals(
            ProactiveRefreshGate.Decision.RefreshNow,
            ProactiveRefreshGate.decide(
                accessToken = "",
                nowEpochSeconds = 1_700_000_000,
            ),
        )
        assertEquals(
            ProactiveRefreshGate.Decision.RefreshNow,
            ProactiveRefreshGate.decide(
                accessToken = "   ",
                nowEpochSeconds = 1_700_000_000,
            ),
        )
    }

    @Test
    fun `malformed JWT — TreatAsOpaque (server is the source of truth)`() {
        // We can't read the exp from a malformed JWT. Don't preemptively
        // refresh — let the request go and let the server's 401 trigger
        // a reactive refresh through the existing Auth-plugin path.
        // Reason: a custom non-JWT bearer (e.g. an opaque session id)
        // would otherwise burn a refresh on every request.
        assertEquals(
            ProactiveRefreshGate.Decision.TreatAsOpaque,
            ProactiveRefreshGate.decide(
                accessToken = "definitely.not.a.jwt.too.many.parts",
                nowEpochSeconds = 1_700_000_000,
            ),
        )
        assertEquals(
            ProactiveRefreshGate.Decision.TreatAsOpaque,
            ProactiveRefreshGate.decide(
                accessToken = "single-token-string",
                nowEpochSeconds = 1_700_000_000,
            ),
        )
    }

    @Test
    fun `JWT with no exp claim — TreatAsOpaque`() {
        // A JWT without exp is unusual; defer to the server.
        val token = jwt("""{"sub":"u1"}""")
        assertEquals(
            ProactiveRefreshGate.Decision.TreatAsOpaque,
            ProactiveRefreshGate.decide(
                accessToken = token,
                nowEpochSeconds = 1_700_000_000,
            ),
        )
    }

    @Test
    fun `decision is deterministic — same inputs map to same output`() {
        val token = jwt("""{"exp":1700003600}""")
        val a =
            ProactiveRefreshGate.decide(
                accessToken = token,
                nowEpochSeconds = 1_700_000_000,
            )
        val b =
            ProactiveRefreshGate.decide(
                accessToken = token,
                nowEpochSeconds = 1_700_000_000,
            )
        assertEquals(a, b)
    }

    private fun jwt(payloadJson: String): String {
        val encoded = Base64.UrlSafe.encode(payloadJson.encodeToByteArray()).trimEnd('=')
        return "header.$encoded.signature"
    }
}
