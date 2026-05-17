package com.po4yka.ratatoskr.data.remote.auth

import com.po4yka.ratatoskr.util.error.runCatchingDomain
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Process-wide refresh-token gate shared by both Ktor clients (the hand-written
 * `ApiClient` and the openapi-kmp-gen-driven generated client). Eliminates the
 * dual-mutex race where two concurrent 401s could both fire `POST v1/auth/refresh`
 * with the same refresh token; the server rejects the second as reused and the
 * loser path clears tokens → silent logout.
 *
 * Design contract:
 *  - One [Mutex] for the entire process. Both clients route through this gate.
 *  - Inside the lock, re-check whether another caller already refreshed during
 *    the wait — if so, return the stored tokens instead of issuing a second
 *    refresh request.
 *  - The actual HTTP call is supplied by the caller via [RefreshOperation] so
 *    this class stays Ktor-agnostic and unit-testable.
 *  - Token persistence is an atomic two-token write (`TokenStorage.saveTokens`)
 *    so a crash between access-save and refresh-save is impossible.
 *
 * The `shouldClearTokensAfterRefreshFailure` predicate that lives in `ApiClient`
 * is preserved verbatim by mapping [RefreshOutcome.HardFailure] → clear tokens
 * and [RefreshOutcome.SoftFailure] → preserve tokens for retry.
 */
class SharedTokenRefresher(
    private val storage: TokenStorage,
    private val refreshOperation: RefreshOperation,
) {
    private val mutex = Mutex()

    /**
     * Refresh the bearer pair. [previouslySeenAccessToken] is the access token the
     * caller was using when it received a 401, so this method can detect
     * "another caller already refreshed during my wait" and return the freshly
     * stored tokens without firing a second HTTP request.
     *
     * Returns the new pair on success, `null` on failure (the caller should treat
     * `null` as "let the original 401 propagate to the user").
     */
    suspend fun refresh(previouslySeenAccessToken: String?): BearerPair? =
        mutex.withLock {
            val currentAccess = storage.getAccessToken()
            if (currentAccess != null && currentAccess != previouslySeenAccessToken) {
                // Another caller refreshed during the wait. Return the stored pair.
                val currentRefresh = storage.getRefreshToken() ?: return@withLock null
                return@withLock BearerPair(currentAccess, currentRefresh)
            }

            val refreshToken = storage.getRefreshToken() ?: return@withLock null

            runCatchingDomain { refreshOperation(refreshToken) }
                .fold(
                    onSuccess = { outcome ->
                        when (outcome) {
                            is RefreshOutcome.Success -> {
                                storage.saveTokens(outcome.tokens)
                                outcome.tokens
                            }
                            is RefreshOutcome.HardFailure -> {
                                // 4xx — token rejected by server. Stored tokens are now invalid.
                                storage.clearTokens()
                                null
                            }
                            is RefreshOutcome.SoftFailure -> {
                                // 5xx / transient — preserve stored tokens for retry on next 401.
                                null
                            }
                        }
                    },
                    onFailure = {
                        // Network exception or other — preserve tokens, let caller retry later.
                        null
                    },
                )
        }
}

/**
 * Two-token pair the refresher passes around. Mirrors `io.ktor.client.plugins.auth.providers.BearerTokens`
 * but lives in commonMain so it's usable by both clients without a direct Ktor-plugin import in shared code.
 */
data class BearerPair(val accessToken: String, val refreshToken: String)

/**
 * Atomic two-token persistence contract. Implementations must write both tokens or neither —
 * Android via a single `DataStore.edit { }`, iOS via a single `settings.transaction { }`.
 */
interface TokenStorage {
    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun saveTokens(tokens: BearerPair)

    suspend fun clearTokens()
}

/**
 * The HTTP-level refresh call, separated so the refresher stays Ktor-agnostic.
 * Implementations call `POST v1/auth/refresh` and translate the server response
 * into one of [RefreshOutcome.Success], [RefreshOutcome.HardFailure] (4xx —
 * tokens should be cleared), or [RefreshOutcome.SoftFailure] (5xx / transient —
 * tokens should be preserved).
 */
fun interface RefreshOperation {
    suspend operator fun invoke(refreshToken: String): RefreshOutcome
}

sealed interface RefreshOutcome {
    data class Success(val tokens: BearerPair) : RefreshOutcome

    /** Server rejected the refresh token (4xx). The stored pair is invalid; clear it. */
    data object HardFailure : RefreshOutcome

    /** Transient failure (5xx, network). Preserve the stored pair for a future retry. */
    data object SoftFailure : RefreshOutcome
}
