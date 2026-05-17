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
 *
 * Subclasses that share their backing store across `SecureStorage` instances
 * (Android Tink + DataStore, iOS Keychain) can additionally override
 * [recreateAgainstSameStore] to opt into the cross-instance AEAD key-reuse
 * tests at the bottom of this contract. Platforms whose `SecureStorage`
 * naturally builds a fresh in-memory store per instance (current desktop
 * MapSettings) return `null` and skip those assertions.
 */
abstract class SecureStorageContract {
    protected abstract suspend fun createStorage(): SecureStorage

    /**
     * Return a second [SecureStorage] pointing at the same backing store as
     * [existing] — used by the cross-instance round-trip tests below. Default
     * returns `null` (test is skipped on this platform). Override on Android +
     * iOS to validate that Tink AEAD keys / Keychain entries persist across
     * `SecureStorage` lifecycles.
     */
    protected open suspend fun recreateAgainstSameStore(existing: SecureStorage): SecureStorage? = null

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

    @Test
    fun `token survives storage recreation against the same backing store`() =
        runTest {
            // Pins AEAD key-reuse on Android (Tink keyset must be persisted, not
            // regenerated per `AndroidSecureStorage` instance) and Keychain-entry
            // reuse on iOS. Platforms whose `recreateAgainstSameStore` returns
            // null (desktop MapSettings, no real cross-instance persistence) skip.
            val first = createStorage()
            first.saveAccessToken("survives-aead-reuse")
            first.saveRefreshToken("refresh-survives")

            val second = recreateAgainstSameStore(first) ?: return@runTest
            assertEquals("survives-aead-reuse", second.getAccessToken())
            assertEquals("refresh-survives", second.getRefreshToken())
        }

    @Test
    fun `developer credentials survive storage recreation against the same backing store`() =
        runTest {
            val first = createStorage()
            first.saveDeveloperCredentials(userId = 314, clientId = "cid-survives", secret = "sec-survives")

            val second = recreateAgainstSameStore(first) ?: return@runTest
            assertEquals(
                DeveloperCredentials(userId = 314, clientId = "cid-survives", secret = "sec-survives"),
                second.getDeveloperCredentials(),
            )
        }
}
