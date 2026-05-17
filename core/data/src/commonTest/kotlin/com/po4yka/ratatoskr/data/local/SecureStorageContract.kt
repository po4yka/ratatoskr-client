package com.po4yka.ratatoskr.data.local

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Shared contract that every [SecureStorage] implementation must satisfy.
 *
 * Subclass in a platform-specific test source set, override [createStorage]
 * to produce a fresh implementation per test, and the inherited `@Test`
 * methods will run on that lane. The contract covers the behavior the rest
 * of the app depends on: round-trip preservation, independent clear scopes
 * for tokens vs. developer credentials, and idempotent clears.
 *
 * Tests do not assert on the underlying storage format — assertions stay at
 * the `SecureStorage` interface so the same suite can run against the
 * Tink-AEAD-backed Android implementation, the Keychain-backed iOS
 * implementation, and the development desktop implementation alike.
 */
abstract class SecureStorageContract {
    protected abstract suspend fun createStorage(): SecureStorage

    @Test
    fun `access token round-trips`() =
        runTest {
            val storage = createStorage()
            storage.saveAccessToken("access-abc")
            assertEquals("access-abc", storage.getAccessToken())
        }

    @Test
    fun `refresh token round-trips`() =
        runTest {
            val storage = createStorage()
            storage.saveRefreshToken("refresh-xyz")
            assertEquals("refresh-xyz", storage.getRefreshToken())
        }

    @Test
    fun `clearTokens removes access and refresh but preserves dev credentials`() =
        runTest {
            val storage = createStorage()
            storage.saveAccessToken("a")
            storage.saveRefreshToken("r")
            storage.saveDeveloperCredentials(userId = 42, clientId = "cid", secret = "sec")

            storage.clearTokens()

            assertNull(storage.getAccessToken())
            assertNull(storage.getRefreshToken())
            assertEquals(
                DeveloperCredentials(userId = 42, clientId = "cid", secret = "sec"),
                storage.getDeveloperCredentials(),
            )
        }

    @Test
    fun `developer credentials round-trip`() =
        runTest {
            val storage = createStorage()
            storage.saveDeveloperCredentials(userId = 7, clientId = "client-1", secret = "shh")
            assertEquals(
                DeveloperCredentials(userId = 7, clientId = "client-1", secret = "shh"),
                storage.getDeveloperCredentials(),
            )
        }

    @Test
    fun `clearDeveloperCredentials removes dev creds but preserves tokens`() =
        runTest {
            val storage = createStorage()
            storage.saveAccessToken("a")
            storage.saveDeveloperCredentials(userId = 7, clientId = "c", secret = "s")

            storage.clearDeveloperCredentials()

            assertNull(storage.getDeveloperCredentials())
            assertEquals("a", storage.getAccessToken())
        }

    @Test
    fun `clearing twice is idempotent`() =
        runTest {
            val storage = createStorage()
            storage.saveAccessToken("a")
            storage.clearTokens()
            storage.clearTokens()
            assertNull(storage.getAccessToken())
        }

    @Test
    fun `overwriting a token replaces the previous value`() =
        runTest {
            val storage = createStorage()
            storage.saveAccessToken("first")
            storage.saveAccessToken("second")
            assertEquals("second", storage.getAccessToken())
        }
}
