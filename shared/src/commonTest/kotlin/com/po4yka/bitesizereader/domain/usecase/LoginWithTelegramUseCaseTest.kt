package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.AuthRepository
import com.po4yka.bitesizereader.util.CoroutineTestBase
import com.po4yka.bitesizereader.util.MockDataFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for LoginWithTelegramUseCase
 */
class LoginWithTelegramUseCaseTest : CoroutineTestBase() {
    private val mockRepository = mockk<AuthRepository>()
    private val useCase = LoginWithTelegramUseCase(mockRepository)

    @Test
    fun `invoke logs in successfully with valid credentials`() =
        runTest {
            // Given
            val telegramUserId = 123456789L
            val authHash = "valid-hash"
            val authDate = 1234567890L
            val username = "testuser"
            val firstName = "Test"
            val lastName = "User"
            val photoUrl = "https://example.com/photo.jpg"
            val clientId = "test-client"

            val expectedTokens = MockDataFactory.createAuthTokens()
            val expectedUser =
                MockDataFactory.createUser(
                    id = telegramUserId,
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    photoUrl = photoUrl,
                )

            coEvery {
                mockRepository.loginWithTelegram(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    photoUrl = photoUrl,
                    clientId = clientId,
                )
            } returns Result.success(Pair(expectedTokens, expectedUser))

            // When
            val result =
                useCase(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    photoUrl = photoUrl,
                    clientId = clientId,
                )

            // Then
            assertTrue(result.isSuccess)
            val (tokens, user) = result.getOrThrow()
            assertEquals(expectedTokens, tokens)
            assertEquals(expectedUser, user)
            coVerify(exactly = 1) {
                mockRepository.loginWithTelegram(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    photoUrl = photoUrl,
                    clientId = clientId,
                )
            }
        }

    @Test
    fun `invoke returns failure when authentication fails`() =
        runTest {
            // Given
            val telegramUserId = 123456789L
            val authHash = "invalid-hash"
            val authDate = 1234567890L
            val clientId = "test-client"
            val error = Exception("Invalid authentication hash")

            coEvery {
                mockRepository.loginWithTelegram(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = null,
                    firstName = null,
                    lastName = null,
                    photoUrl = null,
                    clientId = clientId,
                )
            } returns Result.failure(error)

            // When
            val result =
                useCase(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = null,
                    firstName = null,
                    lastName = null,
                    photoUrl = null,
                    clientId = clientId,
                )

            // Then
            assertTrue(result.isFailure)
            assertEquals("Invalid authentication hash", result.exceptionOrNull()?.message)
            coVerify(exactly = 1) {
                mockRepository.loginWithTelegram(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = null,
                    firstName = null,
                    lastName = null,
                    photoUrl = null,
                    clientId = clientId,
                )
            }
        }

    @Test
    fun `invoke handles optional parameters correctly`() =
        runTest {
            // Given
            val telegramUserId = 123456789L
            val authHash = "valid-hash"
            val authDate = 1234567890L
            val clientId = "test-client"

            val expectedTokens = MockDataFactory.createAuthTokens()
            val expectedUser =
                MockDataFactory.createUser(
                    id = telegramUserId,
                    username = null,
                    lastName = null,
                    photoUrl = null,
                )

            coEvery {
                mockRepository.loginWithTelegram(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = null,
                    firstName = null,
                    lastName = null,
                    photoUrl = null,
                    clientId = clientId,
                )
            } returns Result.success(Pair(expectedTokens, expectedUser))

            // When
            val result =
                useCase(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = null,
                    firstName = null,
                    lastName = null,
                    photoUrl = null,
                    clientId = clientId,
                )

            // Then
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) {
                mockRepository.loginWithTelegram(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = null,
                    firstName = null,
                    lastName = null,
                    photoUrl = null,
                    clientId = clientId,
                )
            }
        }
}
