package com.po4yka.ratatoskr.util.auth

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalEncodingApi::class)
class JwtExpiryProbeTest {
    @Test
    fun `valid unexpired JWT returns Valid`() {
        val token = jwt("""{"sub":"u123","exp":2000000000}""")
        assertEquals(
            JwtExpiryProbe.Status.Valid,
            JwtExpiryProbe.check(token, nowEpochSeconds = 1_500_000_000),
        )
    }

    @Test
    fun `expired JWT returns Expired`() {
        val token = jwt("""{"exp":1000000000}""")
        assertEquals(
            JwtExpiryProbe.Status.Expired,
            JwtExpiryProbe.check(token, nowEpochSeconds = 1_500_000_000),
        )
    }

    @Test
    fun `exp equal to now is Expired — JWT spec says exp MUST be after now`() {
        // Per RFC 7519 §4.1.4: the token is acceptable only when the
        // current time is strictly before `exp`. At equality, the token
        // is no longer acceptable — treat as expired.
        val token = jwt("""{"exp":1700000000}""")
        assertEquals(
            JwtExpiryProbe.Status.Expired,
            JwtExpiryProbe.check(token, nowEpochSeconds = 1_700_000_000),
        )
    }

    @Test
    fun `JWT within grace window is Expired — proactive refresh`() {
        // Grace = 60s means "refresh 60s before actual expiry" so the
        // user never sees a 401. exp=now+30 falls inside the grace
        // window, so report Expired to trigger refresh.
        val token = jwt("""{"exp":1700000030}""")
        assertEquals(
            JwtExpiryProbe.Status.Expired,
            JwtExpiryProbe.check(token, nowEpochSeconds = 1_700_000_000, graceSeconds = 60),
        )
    }

    @Test
    fun `JWT just outside grace window is Valid`() {
        val token = jwt("""{"exp":1700000061}""")
        assertEquals(
            JwtExpiryProbe.Status.Valid,
            JwtExpiryProbe.check(token, nowEpochSeconds = 1_700_000_000, graceSeconds = 60),
        )
    }

    @Test
    fun `missing dots — Malformed`() {
        assertEquals(
            JwtExpiryProbe.Status.Malformed,
            JwtExpiryProbe.check("not.a.valid.jwt.too.many.parts", nowEpochSeconds = 0),
        )
    }

    @Test
    fun `single dot — Malformed`() {
        assertEquals(
            JwtExpiryProbe.Status.Malformed,
            JwtExpiryProbe.check("header.payload", nowEpochSeconds = 0),
        )
    }

    @Test
    fun `empty string — Malformed`() {
        assertEquals(
            JwtExpiryProbe.Status.Malformed,
            JwtExpiryProbe.check("", nowEpochSeconds = 0),
        )
    }

    @Test
    fun `payload that is not base64url — Malformed`() {
        // Use clearly-invalid base64 (contains '#').
        assertEquals(
            JwtExpiryProbe.Status.Malformed,
            JwtExpiryProbe.check("header.###.signature", nowEpochSeconds = 0),
        )
    }

    @Test
    fun `payload missing exp claim returns NoExpiry`() {
        // A token with no `exp` is unusual but valid per spec; caller
        // decides how to treat it. Don't pretend it's expired.
        val token = jwt("""{"sub":"u123"}""")
        assertEquals(
            JwtExpiryProbe.Status.NoExpiry,
            JwtExpiryProbe.check(token, nowEpochSeconds = 1_500_000_000),
        )
    }

    @Test
    fun `empty payload object returns NoExpiry`() {
        val token = jwt("""{}""")
        assertEquals(
            JwtExpiryProbe.Status.NoExpiry,
            JwtExpiryProbe.check(token, nowEpochSeconds = 1_500_000_000),
        )
    }

    @Test
    fun `non-numeric exp value — Malformed`() {
        // Server bug: exp is a string, not an integer. Don't crash;
        // surface as Malformed so the caller refreshes defensively.
        val token = jwt("""{"exp":"never"}""")
        assertEquals(
            JwtExpiryProbe.Status.Malformed,
            JwtExpiryProbe.check(token, nowEpochSeconds = 1_500_000_000),
        )
    }

    @Test
    fun `negative exp — Expired`() {
        // Garbage exp from a misbehaving issuer; pre-1970 is in the
        // past relative to any sane `now`.
        val token = jwt("""{"exp":-1}""")
        assertEquals(
            JwtExpiryProbe.Status.Expired,
            JwtExpiryProbe.check(token, nowEpochSeconds = 1_500_000_000),
        )
    }

    @Test
    fun `exp far in the future is Valid`() {
        val token = jwt("""{"exp":9999999999}""")
        assertEquals(
            JwtExpiryProbe.Status.Valid,
            JwtExpiryProbe.check(token, nowEpochSeconds = 1_700_000_000),
        )
    }

    @Test
    fun `realistic payload with multiple claims around exp`() {
        // Pin that the regex isolates the top-level exp key even when
        // surrounded by other claims (sub, iat, iss).
        val token =
            jwt(
                """{"iss":"https://auth.ratatoskr.po4yka.com","sub":"u123","iat":1700000000,"exp":1700003600,"jti":"abc"}""",
            )
        assertEquals(
            JwtExpiryProbe.Status.Valid,
            JwtExpiryProbe.check(token, nowEpochSeconds = 1_700_000_000),
        )
    }

    private fun jwt(payloadJson: String): String {
        val encoded = Base64.UrlSafe.encode(payloadJson.encodeToByteArray()).trimEnd('=')
        return "header.$encoded.signature"
    }
}
