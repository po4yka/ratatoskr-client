package com.po4yka.ratatoskr.data.remote.trace

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Per-request trace identifiers attached to every outgoing Ktor request.
 *
 * - `requestId` is a v4 UUID copied into the `X-Request-Id` header.
 * - `traceparent` is the W3C Trace Context v0 sampled header
 *   (`00-<32hex>-<16hex>-01`). The trace-id segment is the [requestId]
 *   with dashes removed so a backend log that only captured one of the
 *   two headers can still join against the other.
 *
 * The span-id is a fresh 8-byte random value per request.
 */
@OptIn(ExperimentalUuidApi::class)
data class TraceHeaders(
    val requestId: String,
    val traceparent: String,
) {
    companion object {
        const val REQUEST_ID_HEADER: String = "X-Request-Id"
        const val TRACEPARENT_HEADER: String = "traceparent"

        fun generate(): TraceHeaders {
            val requestUuid = Uuid.random()
            val requestId = requestUuid.toString()
            val traceId = requestUuid.toHexString()
            val spanId = Uuid.random().toHexString().take(SPAN_ID_HEX_LENGTH)
            return TraceHeaders(
                requestId = requestId,
                traceparent = "00-$traceId-$spanId-01",
            )
        }

        private const val SPAN_ID_HEX_LENGTH = 16
    }
}

/**
 * Ktor client plugin that stamps every request with a fresh
 * [TraceHeaders] pair. Install in both the hand-written `ApiClient` and
 * the bootstrap of the generated client.
 *
 * Callers that need to surface the most recent trace ids (e.g. a
 * Settings → Debug screen) can wrap the generator via [recorder] to
 * capture each value into a ring buffer.
 */
val TraceHeadersPlugin =
    createClientPlugin("TraceHeadersPlugin", ::TraceHeadersPluginConfig) {
        val generator = pluginConfig.generator
        onRequest { request, _ ->
            val headers = generator()
            request.header(TraceHeaders.REQUEST_ID_HEADER, headers.requestId)
            request.header(TraceHeaders.TRACEPARENT_HEADER, headers.traceparent)
        }
    }

class TraceHeadersPluginConfig {
    /** Override for tests / debug recording. Defaults to [TraceHeaders.generate]. */
    var generator: () -> TraceHeaders = { TraceHeaders.generate() }
}
