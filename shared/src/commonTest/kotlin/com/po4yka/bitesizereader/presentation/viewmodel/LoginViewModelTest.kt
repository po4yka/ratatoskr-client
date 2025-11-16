package com.po4yka.bitesizereader.presentation.viewmodel

import app.cash.turbine.test
import com.po4yka.bitesizereader.domain.model.AuthTokens
import com.po4yka.bitesizereader.domain.model.User
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import com.po4yka.bitesizereader.domain.usecase.LoginWithTelegramUseCase
import com.po4yka.bitesizereader.presentation.state.LoginState
import com.po4yka.bitesizereader.util.CoroutineTestBase
import com.po4yka.bitesizereader.util.MockDataFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for LoginViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : CoroutineTestBase() {

    private val mockAuthRepository = mockk<AuthRepository>()
    private val mockLoginUseCase = mockk<LoginWithTelegramUseCase>()
    private lateinit var testScope: TestScope
    private lateinit var viewModel: LoginViewModel

    private fun setupViewModel() {
        testScope = TestScope()
        viewModel = LoginViewModel(
            loginWithTelegramUseCase = mockLoginUseCase,
            authRepository = mockAuthRepository,
            viewModelScope = testScope
        )
    }

    @Test
    fun `initial state is not authenticated`() = runTest {
        // Given
        coEvery { mockAuthRepository.isAuthenticated() } returns false

        // When
        setupViewModel()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isAuthenticated)
            assertFalse(state.isLoading)
            assertNull(state.error)
            assertNull(state.user)
        }
    }

    @Test
    fun `initialization checks authentication status`() = runTest {
        // Given
        val mockUser = MockDataFactory.createUser()
        coEvery { mockAuthRepository.isAuthenticated() } returns true
        coEvery { mockAuthRepository.getCurrentUser() } returns Result.success(mockUser)

        // When
        setupViewModel()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isAuthenticated)
            assertEquals(mockUser, state.user)
        }
        coVerify { mockAuthRepository.isAuthenticated() }
        coVerify { mockAuthRepository.getCurrentUser() }
    }

    @Test
    fun `login with Telegram succeeds`() = runTest {
        // Given
        coEvery { mockAuthRepository.isAuthenticated() } returns false
        setupViewModel()

        val telegramUserId = 123456789L
        val authHash = "valid-hash"
        val authDate = 1234567890L
        val username = "testuser"
        val firstName = "Test"
        val clientId = "test-client"

        val mockTokens = MockDataFactory.createAuthTokens()
        val mockUser = MockDataFactory.createUser(
            telegramUserId = telegramUserId,
            username = username,
            firstName = firstName
        )

        coEvery {
            mockLoginUseCase(
                telegramUserId = telegramUserId,
                authHash = authHash,
                authDate = authDate,
                username = username,
                firstName = firstName,
                lastName = null,
                photoUrl = null,
                clientId = clientId
            )
        } returns Result.success(Pair(mockTokens, mockUser))

        // When
        viewModel.loginWithTelegram(
            telegramUserId = telegramUserId,
            authHash = authHash,
            authDate = authDate,
            username = username,
            firstName = firstName,
            lastName = null,
            photoUrl = null,
            clientId = clientId
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isAuthenticated)
            assertFalse(state.isLoading)
            assertEquals(mockUser, state.user)
            assertNull(state.error)
        }
    }

    @Test
    fun `login with Telegram shows loading state`() = runTest {
        // Given
        coEvery { mockAuthRepository.isAuthenticated() } returns false
        setupViewModel()
        advanceUntilIdle()

        val telegramUserId = 123456789L
        val authHash = "valid-hash"
        val authDate = 1234567890L
        val clientId = "test-client"

        val mockTokens = MockDataFactory.createAuthTokens()
        val mockUser = MockDataFactory.createUser()

        coEvery {
            mockLoginUseCase(
                telegramUserId = telegramUserId,
                authHash = authHash,
                authDate = authDate,
                username = null,
                firstName = null,
                lastName = null,
                photoUrl = null,
                clientId = clientId
            )
        } returns Result.success(Pair(mockTokens, mockUser))

        // When
        viewModel.state.test {
            // Skip initial state
            awaitItem()

            viewModel.loginWithTelegram(
                telegramUserId = telegramUserId,
                authHash = authHash,
                authDate = authDate,
                username = null,
                firstName = null,
                lastName = null,
                photoUrl = null,
                clientId = clientId
            )

            // Then - loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)
        }
    }

    @Test
    fun `login with Telegram handles failure`() = runTest {
        // Given
        coEvery { mockAuthRepository.isAuthenticated() } returns false
        setupViewModel()

        val telegramUserId = 123456789L
        val authHash = "invalid-hash"
        val authDate = 1234567890L
        val clientId = "test-client"
        val errorMessage = "Invalid authentication hash"

        coEvery {
            mockLoginUseCase(
                telegramUserId = telegramUserId,
                authHash = authHash,
                authDate = authDate,
                username = null,
                firstName = null,
                lastName = null,
                photoUrl = null,
                clientId = clientId
            )
        } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.loginWithTelegram(
            telegramUserId = telegramUserId,
            authHash = authHash,
            authDate = authDate,
            username = null,
            firstName = null,
            lastName = null,
            photoUrl = null,
            clientId = clientId
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isAuthenticated)
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
            assertNull(state.user)
        }
    }

    @Test
    fun `logout clears user state`() = runTest {
        // Given
        val mockUser = MockDataFactory.createUser()
        coEvery { mockAuthRepository.isAuthenticated() } returns true
        coEvery { mockAuthRepository.getCurrentUser() } returns Result.success(mockUser)
        setupViewModel()
        advanceUntilIdle()

        coEvery { mockAuthRepository.logout() } returns Unit

        // When
        viewModel.logout()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isAuthenticated)
            assertFalse(state.isLoading)
            assertNull(state.user)
            assertNull(state.error)
        }
        coVerify { mockAuthRepository.logout() }
    }
}
