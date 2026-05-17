package com.po4yka.ratatoskr.data.remote

import io.ktor.client.plugins.logging.Logger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Regression coverage for [SanitizingLogger]. The `Logging` Ktor plugin
 * funnels every emitted line through whatever [Logger] is installed. The
 * default `Logger.DEFAULT` prints raw request and response bodies, so a
 * future endpoint that returns `access_token` / `refresh_token` shaped
 * payloads would leak verbatim to logcat / stdout. The wrapper here keeps
 * `redactSensitiveBodyForLog` on the hot path for every log line.
 */
class SanitizingLoggerTest {
    private class CapturingLogger : Logger {
        val lines = mutableListOf<String>()
        override fun log(message: String) {
            lines += message
        }
    }

    @Test
    fun `wrapper redacts access and refresh tokens before delegating`() {
        val capturing = CapturingLogger()
        val sanitizing = SanitizingLogger(capturing)

        sanitizing.log("""{"access_token":"abc.123","refresh_token":"def.456"}""")

        assertEquals(1, capturing.lines.size)
        val emitted = capturing.lines.single()
        assertFalse(emitted.contains("abc.123"), "raw access_token leaked: $emitted")
        assertFalse(emitted.contains("def.456"), "raw refresh_token leaked: $emitted")
        assertTrue(emitted.contains("<redacted>"), "redaction marker missing: $emitted")
    }

    @Test
    fun `wrapper redacts bearer tokens echoed inside response bodies`() {
        val capturing = CapturingLogger()
        val sanitizing = SanitizingLogger(capturing)

        // Ktor's sanitizeHeader strips Authorization headers before they
        // reach the logger, but a careless backend can echo the inbound
        // Authorization value back inside an error body. Verify the
        // wrapper catches that case so the raw token never reaches the
        // platform logger.
        sanitizing.log("""BODY Content: {"detail":"got Bearer eyJhbGciOi.payload.sig"}""")

        val emitted = capturing.lines.single()
        assertFalse(emitted.contains("eyJhbGciOi.payload.sig"), "raw bearer token leaked: $emitted")
        assertTrue(emitted.contains("<redacted>"), "redaction marker missing: $emitted")
    }

    @Test
    fun `wrapper passes through lines without sensitive material untouched`() {
        val capturing = CapturingLogger()
        val sanitizing = SanitizingLogger(capturing)

        val plain = """-> GET /v1/summaries"""
        sanitizing.log(plain)

        assertEquals(plain, capturing.lines.single())
    }
}
