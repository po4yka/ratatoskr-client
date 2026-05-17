package com.po4yka.ratatoskr.data.remote.trace

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.ByteReadChannel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

/**
 * Verifies [TraceHeadersPlugin] actually stamps headers on every Ktor
 * request and that the values change per call.
 */
class TraceHeadersPluginTest {
    @Test
    fun `each request carries X-Request-Id and traceparent headers`() =
        runTest {
            val seen = mutableListOf<HttpRequestData>()
            val client =
                HttpClient(
                    MockEngine { request ->
                        seen += request
                        respond(content = ByteReadChannel(""), status = HttpStatusCode.OK)
                    },
                ) {
                    install(TraceHeadersPlugin)
                }

            client.get("https://example.test/v1/a")
            client.get("https://example.test/v1/b")

            assertEquals(2, seen.size)
            seen.forEach { req ->
                val requestId = req.headers[TraceHeaders.REQUEST_ID_HEADER]
                val traceparent = req.headers[TraceHeaders.TRACEPARENT_HEADER]
                assertNotNull(requestId, "X-Request-Id missing on ${req.url.encodedPath}")
                assertNotNull(traceparent, "traceparent missing on ${req.url.encodedPath}")
                assertTrue(
                    Regex("""^00-[0-9a-f]{32}-[0-9a-f]{16}-01$""").matches(traceparent),
                    "traceparent on ${req.url.encodedPath} did not match W3C v0 format: $traceparent",
                )
            }
            // Per-call uniqueness.
            assertNotEquals(
                seen[0].headers[TraceHeaders.REQUEST_ID_HEADER],
                seen[1].headers[TraceHeaders.REQUEST_ID_HEADER],
            )
            assertNotEquals(
                seen[0].headers[TraceHeaders.TRACEPARENT_HEADER],
                seen[1].headers[TraceHeaders.TRACEPARENT_HEADER],
            )
        }

    @Test
    fun `plugin honours a custom generator`() =
        runTest {
            val fixed =
                TraceHeaders(
                    requestId = "00000000-0000-4000-8000-000000000001",
                    traceparent = "00-000000000000000000000000000000aa-00000000000000bb-01",
                )
            val seen = mutableListOf<HttpRequestData>()
            val client =
                HttpClient(
                    MockEngine { request ->
                        seen += request
                        respond(content = ByteReadChannel(""), status = HttpStatusCode.OK)
                    },
                ) {
                    install(TraceHeadersPlugin) {
                        generator = { fixed }
                    }
                }

            client.get("https://example.test/v1/a")

            assertEquals(fixed.requestId, seen.single().headers[TraceHeaders.REQUEST_ID_HEADER])
            assertEquals(fixed.traceparent, seen.single().headers[TraceHeaders.TRACEPARENT_HEADER])
        }
}
