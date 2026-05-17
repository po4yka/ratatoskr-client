package com.po4yka.ratatoskr.data.remote.auth

/**
 * Maps an HTTP status code from a failed `POST v1/auth/refresh` response
 * into one of [RefreshOutcome.HardFailure] (4xx — token rejected, caller
 * must clear stored tokens) or [RefreshOutcome.SoftFailure] (everything
 * else — transient, caller preserves stored tokens for a future retry).
 *
 * Extracted from the predicate that previously lived inline in `ApiClient`
 * (`shouldClearTokensAfterRefreshFailure`) so both refresh paths — the
 * hand-written `ApiClient` and the upcoming generated-client `Auth` plugin
 * — can share one source of truth wired through [SharedTokenRefresher].
 *
 * Defensive choice: any status outside `400..499` (including `2xx`, `3xx`,
 * `1xx`, `0`, negative, or out-of-range codes from misbehaving proxies)
 * maps to [RefreshOutcome.SoftFailure]. A buggy caller passing 2xx must
 * not silently log the user out.
 *
 * Pure, side-effect-free, deterministic.
 */
object RefreshFailureClassifier {
    fun classify(httpStatus: Int): RefreshOutcome =
        when (httpStatus) {
            in 400..499 -> RefreshOutcome.HardFailure
            else -> RefreshOutcome.SoftFailure
        }
}
