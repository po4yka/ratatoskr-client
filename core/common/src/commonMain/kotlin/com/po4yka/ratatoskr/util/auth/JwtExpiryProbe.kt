package com.po4yka.ratatoskr.util.auth

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Pure JWT exp-claim probe. Decodes the payload segment of a JWT, finds
 * the top-level `"exp"` integer claim, and reports whether the token has
 * expired relative to a caller-supplied `now`, with an optional grace
 * window for proactive refresh.
 *
 * Boundary follows RFC 7519 §4.1.4: a token is acceptable only when
 * `now < exp`. At equality (`now == exp`), the token is reported as
 * [Status.Expired].
 *
 * The probe **does not verify the signature** — that is the network
 * layer's job. This atom only answers the question "has the clock
 * passed `exp`?" so the client can refresh before issuing a doomed
 * request and avoid a needless 401 round-trip.
 *
 * Failure modes return distinct status values rather than throwing so
 * the caller can make a conservative decision (e.g. "treat Malformed
 * the same as Expired, since both mean the locally-stored token can't
 * be trusted").
 *
 * Pure, side-effect-free, deterministic. The regex isolates the
 * top-level `exp` key — standard JWT payloads never nest the claim.
 */
object JwtExpiryProbe {
    enum class Status {
        Valid,
        Expired,
        NoExpiry,
        Malformed,
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun check(
        jwt: String,
        nowEpochSeconds: Long,
        graceSeconds: Long = 0,
    ): Status {
        val parts = jwt.split('.')
        if (parts.size != 3) return Status.Malformed

        val payloadJson =
            decodeBase64Url(parts[1])?.decodeToString()
                ?: return Status.Malformed

        val match = EXP_NUMERIC_REGEX.find(payloadJson)
        if (match != null) {
            val expSeconds = match.groupValues[1].toLongOrNull() ?: return Status.Malformed
            return if (expSeconds <= nowEpochSeconds + graceSeconds) {
                Status.Expired
            } else {
                Status.Valid
            }
        }
        // No numeric match. Distinguish "exp key absent" from "exp key
        // present with a non-integer value" (e.g. a server bug that
        // serialized exp as a string) — the latter is Malformed.
        return if (EXP_KEY_REGEX.containsMatchIn(payloadJson)) {
            Status.Malformed
        } else {
            Status.NoExpiry
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun decodeBase64Url(input: String): ByteArray? {
        val paddingNeeded = (4 - input.length % 4) % 4
        val padded = input + "=".repeat(paddingNeeded)
        return try {
            Base64.UrlSafe.decode(padded)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private val EXP_NUMERIC_REGEX = Regex("\"exp\"\\s*:\\s*(-?\\d+)")
    private val EXP_KEY_REGEX = Regex("\"exp\"\\s*:")
}
