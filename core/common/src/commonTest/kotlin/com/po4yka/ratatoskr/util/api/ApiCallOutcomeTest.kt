package com.po4yka.ratatoskr.util.api

import kotlin.test.Test
import kotlin.test.assertEquals

class ApiCallOutcomeTest {
    @Test
    fun `2xx body — Success`() {
        val outcome = ApiCallOutcome.fromHttpStatus(httpStatus = 200, body = "ok")
        assertEquals(ApiCallOutcome.Success(value = "ok"), outcome)
    }

    @Test
    fun `204 No Content — Success with null body`() {
        val outcome = ApiCallOutcome.fromHttpStatus<String?>(httpStatus = 204, body = null)
        assertEquals(ApiCallOutcome.Success(value = null), outcome)
    }

    @Test
    fun `401 — Unauthorized`() {
        // Pin the canonical refresh-trigger status so the generated
        // client and the hand-written ApiClient agree on what causes
        // SharedTokenRefresher to fire.
        assertEquals(
            ApiCallOutcome.Unauthorized,
            ApiCallOutcome.fromHttpStatus<String>(httpStatus = 401, body = "auth fail"),
        )
    }

    @Test
    fun `404 — NotFound`() {
        assertEquals(
            ApiCallOutcome.NotFound,
            ApiCallOutcome.fromHttpStatus<String>(httpStatus = 404, body = "missing"),
        )
    }

    @Test
    fun `409 — Conflict`() {
        // Sync graph appliers translate 409 into the conflict-count
        // surface; pin so the generated-client refactor doesn't
        // collapse 409 into a generic ClientError.
        assertEquals(
            ApiCallOutcome.Conflict,
            ApiCallOutcome.fromHttpStatus<String>(httpStatus = 409, body = "conflict"),
        )
    }

    @Test
    fun `429 — RateLimited`() {
        // Pin the canonical PendingOpRetryPolicy.RateLimited trigger.
        assertEquals(
            ApiCallOutcome.RateLimited,
            ApiCallOutcome.fromHttpStatus<String>(httpStatus = 429, body = "slow down"),
        )
    }

    @Test
    fun `other 4xx — ClientError`() {
        assertEquals(
            ApiCallOutcome.ClientError(httpStatus = 400),
            ApiCallOutcome.fromHttpStatus<String>(httpStatus = 400, body = "bad"),
        )
        assertEquals(
            ApiCallOutcome.ClientError(httpStatus = 422),
            ApiCallOutcome.fromHttpStatus<String>(httpStatus = 422, body = "schema fail"),
        )
    }

    @Test
    fun `5xx — ServerError`() {
        assertEquals(
            ApiCallOutcome.ServerError(httpStatus = 500),
            ApiCallOutcome.fromHttpStatus<String>(httpStatus = 500, body = "boom"),
        )
        assertEquals(
            ApiCallOutcome.ServerError(httpStatus = 503),
            ApiCallOutcome.fromHttpStatus<String>(httpStatus = 503, body = "unavailable"),
        )
    }

    @Test
    fun `out-of-range status — TransportError`() {
        // 0 (no response) and negative codes from misbehaving proxies.
        assertEquals(
            ApiCallOutcome.TransportError,
            ApiCallOutcome.fromHttpStatus<String>(httpStatus = 0, body = null),
        )
        assertEquals(
            ApiCallOutcome.TransportError,
            ApiCallOutcome.fromHttpStatus<String>(httpStatus = -1, body = null),
        )
    }

    @Test
    fun `mapping is deterministic`() {
        val a = ApiCallOutcome.fromHttpStatus(httpStatus = 200, body = "ok")
        val b = ApiCallOutcome.fromHttpStatus(httpStatus = 200, body = "ok")
        assertEquals(a, b)
    }
}
