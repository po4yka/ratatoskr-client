package com.po4yka.ratatoskr.util.auth

/**
 * Pre-flight decision atom that composes [JwtExpiryProbe] into a binary
 * verdict the network layer can act on before sending a request:
 *
 *  - [Decision.RefreshNow]      — no token, blank token, or a JWT whose
 *                                  `exp` claim has passed (or is inside
 *                                  the proactive-refresh grace window).
 *                                  Pause the request and ask the shared
 *                                  refresher for a new pair.
 *  - [Decision.RefreshNotNeeded] — the JWT is parseable and its `exp`
 *                                  is comfortably in the future.
 *  - [Decision.TreatAsOpaque]   — the token is non-empty but not a
 *                                  parseable JWT (malformed, or missing
 *                                  `exp`). Send the request as-is and
 *                                  let the server's 401 trigger the
 *                                  reactive refresh. Avoids burning a
 *                                  refresh on every request when the
 *                                  bearer is an opaque session id.
 *
 * Pure, side-effect-free, deterministic. Composes with
 * [JwtExpiryProbe] but does not duplicate its parsing — failure modes
 * map to [Decision.TreatAsOpaque] so the gate stays conservative.
 */
object ProactiveRefreshGate {
    enum class Decision {
        RefreshNow,
        RefreshNotNeeded,
        TreatAsOpaque,
    }

    fun decide(
        accessToken: String?,
        nowEpochSeconds: Long,
        graceSeconds: Long = DEFAULT_GRACE_SECONDS,
    ): Decision {
        if (accessToken.isNullOrBlank()) return Decision.RefreshNow

        return when (
            JwtExpiryProbe.check(
                jwt = accessToken,
                nowEpochSeconds = nowEpochSeconds,
                graceSeconds = graceSeconds,
            )
        ) {
            JwtExpiryProbe.Status.Valid -> Decision.RefreshNotNeeded
            JwtExpiryProbe.Status.Expired -> Decision.RefreshNow
            JwtExpiryProbe.Status.NoExpiry,
            JwtExpiryProbe.Status.Malformed,
            -> Decision.TreatAsOpaque
        }
    }

    const val DEFAULT_GRACE_SECONDS: Long = 60
}
