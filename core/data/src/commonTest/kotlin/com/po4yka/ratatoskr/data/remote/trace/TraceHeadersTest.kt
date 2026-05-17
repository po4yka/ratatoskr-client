package com.po4yka.ratatoskr.data.remote.trace

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Regression coverage for [TraceHeaders.generate].
 *
 * A trace id ties a single client-initiated request to its backend
 * log lines. The headers must match the formats both endpoints agree
 * on: `X-Request-Id` is any opaque string (we use a v4 UUID) and
 * `traceparent` is the W3C Trace Context v0 format:
 *
 *   `00-<32 lower-hex trace-id>-<16 lower-hex span-id>-01`
 *
 * The `01` flag marks the request as sampled — every mobile request
 * should be sampled because traffic volume is human-scale.
 */
class TraceHeadersTest {
    @Test
    fun `requestId is a UUID-shaped string`() {
        val headers = TraceHeaders.generate()

        // 8-4-4-4-12 hex groups
        val pattern = Regex("""^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$""")
        assertTrue(
            pattern.matches(headers.requestId),
            "X-Request-Id must be lower-hex UUID-shaped; was '${headers.requestId}'",
        )
    }

    @Test
    fun `traceparent matches the W3C v0 sampled format`() {
        val headers = TraceHeaders.generate()

        val pattern = Regex("""^00-[0-9a-f]{32}-[0-9a-f]{16}-01$""")
        assertTrue(
            pattern.matches(headers.traceparent),
            "traceparent must be 00-<32hex>-<16hex>-01; was '${headers.traceparent}'",
        )
    }

    @Test
    fun `generate returns distinct values across calls`() {
        val first = TraceHeaders.generate()
        val second = TraceHeaders.generate()

        assertNotEquals(first.requestId, second.requestId, "request ids must be unique per call")
        assertNotEquals(first.traceparent, second.traceparent, "traceparents must be unique per call")
    }

    @Test
    fun `requestId is reused as the traceparent trace-id segment`() {
        // The traceparent's trace-id is derived from the request id with
        // dashes removed — that lets backend logs that only saw one of
        // the two headers still cross-reference the other.
        val headers = TraceHeaders.generate()

        val expectedTraceId = headers.requestId.replace("-", "")
        val actualTraceId = headers.traceparent.split("-")[1]
        assertEquals(expectedTraceId, actualTraceId)
    }
}
