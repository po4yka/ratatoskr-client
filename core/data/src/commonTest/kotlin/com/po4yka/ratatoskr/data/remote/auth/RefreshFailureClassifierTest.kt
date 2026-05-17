package com.po4yka.ratatoskr.data.remote.auth

import kotlin.test.Test
import kotlin.test.assertEquals

class RefreshFailureClassifierTest {
    @Test
    fun `401 is the canonical refresh-token-rejected case — HardFailure`() {
        // The server returns 401 when the refresh token has been revoked or
        // reused. Stored tokens are dead — caller must clear them.
        assertEquals(
            RefreshOutcome.HardFailure,
            RefreshFailureClassifier.classify(httpStatus = 401),
        )
    }

    @Test
    fun `400 Bad Request — HardFailure, caller cannot recover by retrying`() {
        assertEquals(
            RefreshOutcome.HardFailure,
            RefreshFailureClassifier.classify(httpStatus = 400),
        )
    }

    @Test
    fun `403 Forbidden — HardFailure`() {
        assertEquals(
            RefreshOutcome.HardFailure,
            RefreshFailureClassifier.classify(httpStatus = 403),
        )
    }

    @Test
    fun `422 Unprocessable Entity — HardFailure`() {
        // Server says the refresh-token payload itself is malformed; retry
        // with the same token will keep failing.
        assertEquals(
            RefreshOutcome.HardFailure,
            RefreshFailureClassifier.classify(httpStatus = 422),
        )
    }

    @Test
    fun `499 (client-side closed) — HardFailure boundary`() {
        // Upper bound of 4xx; pin the inclusive edge so a future
        // refactor doesn't quietly demote 499 to Soft.
        assertEquals(
            RefreshOutcome.HardFailure,
            RefreshFailureClassifier.classify(httpStatus = 499),
        )
    }

    @Test
    fun `500 Internal Server Error — SoftFailure, preserve tokens for retry`() {
        // 5xx is transient — the refresh token is still valid; the server
        // is just sick. Don't punish the user by logging them out.
        assertEquals(
            RefreshOutcome.SoftFailure,
            RefreshFailureClassifier.classify(httpStatus = 500),
        )
    }

    @Test
    fun `502 Bad Gateway — SoftFailure`() {
        assertEquals(
            RefreshOutcome.SoftFailure,
            RefreshFailureClassifier.classify(httpStatus = 502),
        )
    }

    @Test
    fun `503 Service Unavailable — SoftFailure`() {
        assertEquals(
            RefreshOutcome.SoftFailure,
            RefreshFailureClassifier.classify(httpStatus = 503),
        )
    }

    @Test
    fun `504 Gateway Timeout — SoftFailure`() {
        assertEquals(
            RefreshOutcome.SoftFailure,
            RefreshFailureClassifier.classify(httpStatus = 504),
        )
    }

    @Test
    fun `599 — SoftFailure boundary`() {
        // Upper bound of 5xx; some CDNs use 599 for network-connect timeouts.
        assertEquals(
            RefreshOutcome.SoftFailure,
            RefreshFailureClassifier.classify(httpStatus = 599),
        )
    }

    @Test
    fun `200 OK passed in defensively — SoftFailure, do not nuke tokens`() {
        // Callers should not invoke this for 2xx responses, but if they
        // mistakenly do, preserve the stored tokens — a buggy caller
        // shouldn't silently log the user out.
        assertEquals(
            RefreshOutcome.SoftFailure,
            RefreshFailureClassifier.classify(httpStatus = 200),
        )
    }

    @Test
    fun `301 redirect — SoftFailure`() {
        // 3xx on a refresh endpoint is bizarre but transient-looking;
        // preserve tokens.
        assertEquals(
            RefreshOutcome.SoftFailure,
            RefreshFailureClassifier.classify(httpStatus = 301),
        )
    }

    @Test
    fun `0 — no response — SoftFailure`() {
        // Ktor surfaces 0 for transport-level failures with no HTTP
        // response. Preserve tokens; the network will come back.
        assertEquals(
            RefreshOutcome.SoftFailure,
            RefreshFailureClassifier.classify(httpStatus = 0),
        )
    }

    @Test
    fun `negative status — SoftFailure, defensive`() {
        // Garbage input must not destroy tokens.
        assertEquals(
            RefreshOutcome.SoftFailure,
            RefreshFailureClassifier.classify(httpStatus = -1),
        )
    }

    @Test
    fun `out-of-range status above 599 — SoftFailure`() {
        // Non-standard codes from misbehaving proxies; never punish
        // the user.
        assertEquals(
            RefreshOutcome.SoftFailure,
            RefreshFailureClassifier.classify(httpStatus = 999),
        )
    }
}
