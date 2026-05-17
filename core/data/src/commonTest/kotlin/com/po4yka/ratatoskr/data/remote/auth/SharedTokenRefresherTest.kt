package com.po4yka.ratatoskr.data.remote.auth

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SharedTokenRefresherTest {
    private class InMemoryTokenStorage(
        initialAccess: String? = "old-access",
        initialRefresh: String? = "old-refresh",
    ) : TokenStorage {
        private var access: String? = initialAccess
        private var refresh: String? = initialRefresh
        var saveCount = 0
            private set
        var clearCount = 0
            private set

        override suspend fun getAccessToken(): String? = access

        override suspend fun getRefreshToken(): String? = refresh

        override suspend fun saveTokens(tokens: BearerPair) {
            access = tokens.accessToken
            refresh = tokens.refreshToken
            saveCount++
        }

        override suspend fun clearTokens() {
            access = null
            refresh = null
            clearCount++
        }
    }

    @Test
    fun `successful refresh writes the new pair atomically and returns it`() = runTest {
        val storage = InMemoryTokenStorage()
        val refresher = SharedTokenRefresher(
            storage = storage,
            refreshOperation = { _ -> RefreshOutcome.Success(BearerPair("new-access", "new-refresh")) },
        )

        val result = refresher.refresh(previouslySeenAccessToken = "old-access")

        assertEquals(BearerPair("new-access", "new-refresh"), result)
        assertEquals("new-access", storage.getAccessToken())
        assertEquals("new-refresh", storage.getRefreshToken())
        assertEquals(1, storage.saveCount, "saveTokens must be called exactly once — atomic write")
        assertEquals(0, storage.clearCount)
    }

    @Test
    fun `hard failure clears stored tokens and returns null`() = runTest {
        val storage = InMemoryTokenStorage()
        val refresher = SharedTokenRefresher(storage, refreshOperation = { _ -> RefreshOutcome.HardFailure })

        val result = refresher.refresh(previouslySeenAccessToken = "old-access")

        assertNull(result)
        assertEquals(1, storage.clearCount, "4xx must clear stored tokens")
        assertNull(storage.getAccessToken())
    }

    @Test
    fun `soft failure preserves stored tokens and returns null for caller retry`() = runTest {
        val storage = InMemoryTokenStorage()
        val refresher = SharedTokenRefresher(storage, refreshOperation = { _ -> RefreshOutcome.SoftFailure })

        val result = refresher.refresh(previouslySeenAccessToken = "old-access")

        assertNull(result)
        assertEquals(0, storage.clearCount, "5xx must NOT clear — caller retries later")
        assertEquals("old-access", storage.getAccessToken())
        assertEquals("old-refresh", storage.getRefreshToken())
    }

    @Test
    fun `caller carrying a stale access token sees the already-refreshed pair without re-issuing`() = runTest {
        // Simulate: caller B is woken up holding access-token-A, but storage already
        // shows access-token-B from a refresh that completed while B was waiting.
        val storage = InMemoryTokenStorage(initialAccess = "freshly-refreshed", initialRefresh = "freshly-refreshed-refresh")
        var refreshCalled = false
        val refresher = SharedTokenRefresher(
            storage = storage,
            refreshOperation = { _ ->
                refreshCalled = true
                RefreshOutcome.Success(BearerPair("must-not-be-used", "must-not-be-used"))
            },
        )

        val result = refresher.refresh(previouslySeenAccessToken = "stale-access")

        assertEquals(BearerPair("freshly-refreshed", "freshly-refreshed-refresh"), result)
        assertEquals(false, refreshCalled, "must reuse the already-stored pair instead of re-calling /auth/refresh")
        assertEquals(0, storage.saveCount, "no save when reusing stored pair")
    }

    @Test
    fun `concurrent callers all see exactly one refresh operation fire`() = runTest {
        // The regression this test guards: today's dual-mutex code can issue
        // two POST /v1/auth/refresh with the same refresh token; the server
        // rejects the second as reused → silent logout.
        val storage = InMemoryTokenStorage()
        // runTest uses a single-threaded dispatcher, so concurrent coroutines do not race
        // on a plain Int — but the mutex still gets exercised because each launched
        // coroutine suspends inside refresh() before any has completed.
        var callCount = 0
        val refresher = SharedTokenRefresher(
            storage = storage,
            refreshOperation = { _ ->
                callCount++
                RefreshOutcome.Success(BearerPair("new-access", "new-refresh"))
            },
        )

        val concurrentCallers = 5
        val results = (1..concurrentCallers).map {
            async { refresher.refresh(previouslySeenAccessToken = "old-access") }
        }.awaitAll()

        assertEquals(1, callCount, "exactly one refresh HTTP call across $concurrentCallers concurrent callers")
        assertEquals(1, storage.saveCount, "atomic save must run exactly once even under contention")
        results.forEach { pair ->
            assertEquals(BearerPair("new-access", "new-refresh"), pair, "all callers must see the same refreshed pair")
        }
    }
}
