package com.po4yka.ratatoskr.data.remote

import io.ktor.http.Url
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApiClientLogSanitizerTest {
    @Test
    fun `request target keeps path and redacts query values`() {
        val target =
            sanitizedRequestTargetForLog(
                Url("https://example.com/v1/summaries?token=secret&cursor=next"),
            )

        assertEquals("/v1/summaries?<query-redacted>", target)
        assertFalse(target.contains("secret"))
        assertFalse(target.contains("next"))
    }

    @Test
    fun `request target omits query marker when query is absent`() {
        val target = sanitizedRequestTargetForLog(Url("https://example.com/v1/summaries"))

        assertEquals("/v1/summaries", target)
    }

    @Test
    fun `body sanitizer redacts common secret fields`() {
        val body =
            """
            {
              "accessToken": "abc123",
              "refresh_token": "def456",
              "password": "hunter2",
              "apiKey": unquoted-key,
              "message": "validation failed"
            }
            """.trimIndent()

        val sanitized = redactSensitiveBodyForLog(body)

        assertFalse(sanitized.contains("abc123"))
        assertFalse(sanitized.contains("def456"))
        assertFalse(sanitized.contains("hunter2"))
        assertFalse(sanitized.contains("unquoted-key"))
        assertTrue(sanitized.contains("\"message\": \"validation failed\""))
        assertTrue(sanitized.contains("\"accessToken\": \"<redacted>\""))
    }

    @Test
    fun `body sanitizer redacts bearer tokens`() {
        val sanitized = redactSensitiveBodyForLog("""{"error":"Bearer eyJ.secret.token"}""")

        assertEquals("""{"error":"Bearer <redacted>"}""", sanitized)
    }

    @Test
    fun `sensitive endpoint body suppression covers auth secret and dump paths`() {
        assertTrue(shouldSuppressErrorBodyForLog("/v1/auth/refresh"))
        assertTrue(shouldSuppressErrorBodyForLog("/v1/secret-login"))
        assertTrue(shouldSuppressErrorBodyForLog("/v1/db-dump/export"))
        assertFalse(shouldSuppressErrorBodyForLog("/v1/authors"))
        assertFalse(shouldSuppressErrorBodyForLog("/v1/summaries"))
    }

    @Test
    fun `refresh failure token clearing is limited to invalid credential statuses`() {
        assertTrue(shouldClearTokensAfterRefreshFailure(HttpStatusCode.BadRequest))
        assertTrue(shouldClearTokensAfterRefreshFailure(HttpStatusCode.Unauthorized))
        assertTrue(shouldClearTokensAfterRefreshFailure(HttpStatusCode.Forbidden))
        assertFalse(shouldClearTokensAfterRefreshFailure(HttpStatusCode.InternalServerError))
        assertFalse(shouldClearTokensAfterRefreshFailure(HttpStatusCode.ServiceUnavailable))
    }
}
