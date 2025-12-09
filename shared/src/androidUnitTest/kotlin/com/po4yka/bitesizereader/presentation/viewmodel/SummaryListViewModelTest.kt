package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.usecase.GetSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.time.Clock
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class SummaryListViewModelTest {
    private val getSummariesUseCase: GetSummariesUseCase = mockk()
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase = mockk()
    private lateinit var viewModel: SummaryListViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { getSummariesUseCase(any(), any(), any()) } returns
            flowOf(
                listOf(
                    Summary(
                        id = "1",
                        title = "Test Summary",
                        content = "Content",
                        sourceUrl = "url",
                        imageUrl = null,
                        createdAt = Clock.System.now(),
                        isRead = false,
                        tags = emptyList(),
                    ),
                ),
            )

        viewModel = SummaryListViewModel(getSummariesUseCase, markSummaryAsReadUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads summaries`() =
        runTest {
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals(1, state.summaries.size)
            assertEquals("Test Summary", state.summaries[0].title)
        }
}
