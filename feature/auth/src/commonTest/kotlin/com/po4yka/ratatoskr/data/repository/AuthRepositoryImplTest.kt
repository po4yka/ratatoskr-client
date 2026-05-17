package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.local.DeveloperCredentials
import com.po4yka.ratatoskr.data.local.SecureStorage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

/**
 * Pure-unit regression coverage for the SecureStorage-driven branches of
 * AuthRepositoryImpl. The API-mock-dependent cases the audit lists for
 * `getCurrentUser` and `logoutWithRevoke` require intercepting the
 * generated AuthenticationApi singleton; until that mocking infrastructure
 * lands (tracked alongside add-syncrepositoryimpl-integration-tests…),
 * those branches are exercised manually.
 */
class AuthRepositoryImplTest {
    private class FakeSecureStorage : SecureStorage {
        private var accessToken: String? = null
        private var refreshToken: String? = null
        private var credentials: DeveloperCredentials? = null

        override suspend fun saveAccessToken(token: String) {
            accessToken = token
        }

        override suspend fun getAccessToken(): String? = accessToken

        override suspend fun saveRefreshToken(token: String) {
            refreshToken = token
        }

        override suspend fun getRefreshToken(): String? = refreshToken

        override suspend fun clearTokens() {
            accessToken = null
            refreshToken = null
        }

        override suspend fun saveDeveloperCredentials(
            userId: Int,
            clientId: String,
            secret: String,
        ) {
            credentials = DeveloperCredentials(userId, clientId, secret)
        }

        override suspend fun getDeveloperCredentials(): DeveloperCredentials? = credentials

        override suspend fun clearDeveloperCredentials() {
            credentials = null
        }
    }

    @Test
    fun checkAuthStatusEmitsFalseWhenNoAccessTokenStored() =
        runTest {
            val repo = AuthRepositoryImpl(FakeSecureStorage())
            repo.checkAuthStatus()
            assertFalse(repo.isAuthenticated.first(), "Fresh storage must report unauthenticated")
        }

    @Test
    fun checkAuthStatusEmitsTrueAfterTokenSaved() =
        runTest {
            val storage = FakeSecureStorage()
            storage.saveAccessToken("jwt.test.token")
            val repo = AuthRepositoryImpl(storage)
            repo.checkAuthStatus()
            assertTrue(repo.isAuthenticated.first(), "Stored access token must flip isAuthenticated to true")
        }

    @Test
    fun logoutClearsTokensAndFlipsIsAuthenticatedFalse() =
        runTest {
            val storage = FakeSecureStorage()
            storage.saveAccessToken("jwt.test.token")
            storage.saveRefreshToken("refresh.test.token")
            val repo = AuthRepositoryImpl(storage)
            repo.checkAuthStatus()
            assertTrue(repo.isAuthenticated.first())

            repo.logout()

            assertNull(storage.getAccessToken(), "logout must clear access token")
            assertNull(storage.getRefreshToken(), "logout must clear refresh token")
            assertFalse(repo.isAuthenticated.first(), "logout must flip isAuthenticated to false")
        }

    @Test
    fun secureStorageDeveloperCredentialsRoundtripsThroughTheFake() =
        runTest {
            // Guards the fake itself — without this, the other tests can quietly
            // share state when run in parallel inside the same class.
            val storage = FakeSecureStorage()
            storage.saveDeveloperCredentials(userId = 42, clientId = "test", secret = "shh")
            val loaded = storage.getDeveloperCredentials()
            assertEquals(42, loaded?.userId)
            storage.clearDeveloperCredentials()
            assertNull(storage.getDeveloperCredentials())
        }
}
