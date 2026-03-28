package com.po4yka.bitesizereader.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.presentation.navigation.SummaryListComponent
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import com.po4yka.bitesizereader.ui.screens.SummaryListScreen
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import org.junit.Rule
import org.junit.Test

class SummaryListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysSummaries() {
        val mockViewModel = mockk<SummaryListViewModel>(relaxed = true)
        val mockComponent = mockk<SummaryListComponent>(relaxed = true)

        val summary =
            Summary(
                id = "1",
                title = "Test Summary UI",
                content = "Content",
                sourceUrl = "url",
                imageUrl = null,
                createdAt = Clock.System.now(),
                isRead = false,
                tags = emptyList(),
            )

        every { mockComponent.viewModel } returns mockViewModel
        every { mockViewModel.state } returns
            MutableStateFlow(
                SummaryListState(summaries = listOf(summary)),
            )

        composeTestRule.setContent {
            SummaryListScreen(component = mockComponent)
        }

        composeTestRule.onNodeWithText("Test Summary UI").assertIsDisplayed()
    }
}
