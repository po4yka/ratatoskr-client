package com.po4yka.bitesizereader.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import com.po4yka.bitesizereader.domain.usecase.LoginWithTelegramUseCase
import com.po4yka.bitesizereader.presentation.state.LoginState
import com.po4yka.bitesizereader.presentation.viewmodel.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI tests for AuthScreen
 */
class AuthScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun authScreen_displaysLoginButton() {
        // Given
        val mockAuthRepository = mockk<AuthRepository>()
        val mockLoginUseCase = mockk<LoginWithTelegramUseCase>()
        coEvery { mockAuthRepository.isAuthenticated() } returns false

        val viewModel = LoginViewModel(
            loginWithTelegramUseCase = mockLoginUseCase,
            authRepository = mockAuthRepository,
            viewModelScope = CoroutineScope(Dispatchers.Unconfined)
        )

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = viewModel,
                onLoginSuccess = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Login with Telegram")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun authScreen_displaysAppTitle() {
        // Given
        val mockAuthRepository = mockk<AuthRepository>()
        val mockLoginUseCase = mockk<LoginWithTelegramUseCase>()
        coEvery { mockAuthRepository.isAuthenticated() } returns false

        val viewModel = LoginViewModel(
            loginWithTelegramUseCase = mockLoginUseCase,
            authRepository = mockAuthRepository,
            viewModelScope = CoroutineScope(Dispatchers.Unconfined)
        )

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = viewModel,
                onLoginSuccess = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Bite-Size Reader")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun authScreen_displaysFeatures() {
        // Given
        val mockAuthRepository = mockk<AuthRepository>()
        val mockLoginUseCase = mockk<LoginWithTelegramUseCase>()
        coEvery { mockAuthRepository.isAuthenticated() } returns false

        val viewModel = LoginViewModel(
            loginWithTelegramUseCase = mockLoginUseCase,
            authRepository = mockAuthRepository,
            viewModelScope = CoroutineScope(Dispatchers.Unconfined)
        )

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = viewModel,
                onLoginSuccess = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Get concise summaries of any web article")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Search and organize your reading history")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Sync across all your devices")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun authScreen_showsLoadingWhenAuthenticating() {
        // Given
        val mockAuthRepository = mockk<AuthRepository>()
        val mockLoginUseCase = mockk<LoginWithTelegramUseCase>()
        coEvery { mockAuthRepository.isAuthenticated() } returns false

        val viewModel = LoginViewModel(
            loginWithTelegramUseCase = mockLoginUseCase,
            authRepository = mockAuthRepository,
            viewModelScope = CoroutineScope(Dispatchers.Unconfined)
        )

        // Simulate loading state
        viewModel.state.value = LoginState(isLoading = true)

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = viewModel,
                onLoginSuccess = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Logging in...")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun authScreen_showsErrorMessage() {
        // Given
        val mockAuthRepository = mockk<AuthRepository>()
        val mockLoginUseCase = mockk<LoginWithTelegramUseCase>()
        coEvery { mockAuthRepository.isAuthenticated() } returns false

        val viewModel = LoginViewModel(
            loginWithTelegramUseCase = mockLoginUseCase,
            authRepository = mockAuthRepository,
            viewModelScope = CoroutineScope(Dispatchers.Unconfined)
        )

        // Simulate error state
        viewModel.state.value = LoginState(error = "Authentication failed")

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = viewModel,
                onLoginSuccess = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Authentication failed")
            .assertExists()
            .assertIsDisplayed()
    }
}
