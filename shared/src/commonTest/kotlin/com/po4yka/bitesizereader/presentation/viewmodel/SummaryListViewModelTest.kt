package com.po4yka.bitesizereader.presentation.viewmodel

import app.cash.turbine.test
import com.po4yka.bitesizereader.domain.usecase.GetSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.util.CoroutineTestBase
import com.po4yka.bitesizereader.util.MockDataFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for SummaryListViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SummaryListViewModelTest : CoroutineTestBase() {
    private val mockGetSummariesUseCase = mockk<GetSummariesUseCase>()
    private val mockMarkAsReadUseCase = mockk<MarkSummaryAsReadUseCase>()
    private lateinit var testScope: TestScope
    private lateinit var viewModel: SummaryListViewModel

    private fun setupViewModel() {
        testScope = TestScope()
        viewModel =
            SummaryListViewModel(
                getSummariesUseCase = mockGetSummariesUseCase,
                markSummaryAsReadUseCase = mockMarkAsReadUseCase,
                viewModelScope = testScope,
            )
    }

    @Test
    fun `initial state is empty`() =
        runTest {
            // Given
            coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flowOf(emptyList())

            // When
            setupViewModel()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.summaries.isEmpty())
                assertFalse(state.isLoading)
                assertNull(state.error)
                assertNull(state.selectedTopic)
                assertNull(state.readFilter)
            }
        }

    @Test
    fun `loadSummaries fetches summaries successfully`() =
        runTest {
            // Given
            val mockSummaries = MockDataFactory.createSummaryList(count = 5)
            coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flowOf(mockSummaries)

            // When
            setupViewModel()
            viewModel.loadSummaries()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertEquals(5, state.summaries.size)
                assertFalse(state.isLoading)
                assertNull(state.error)
            }
            coVerify { mockGetSummariesUseCase(any(), any(), any()) }
        }

    @Test
    fun `loadSummaries shows loading state`() =
        runTest {
            // Given
            val mockSummaries = MockDataFactory.createSummaryList(count = 3)
            coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flowOf(mockSummaries)

            setupViewModel()

            // When
            viewModel.state.test {
                // Skip initial state
                awaitItem()

                viewModel.loadSummaries()

                // Then - loading state
                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)
            }
        }

    @Test
    fun `loadSummaries handles failure`() =
        runTest {
            // Given
            coEvery { mockGetSummariesUseCase(any(), any(), any()) } throws Exception("Network error")

            // When
            setupViewModel()
            viewModel.loadSummaries()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.summaries.isEmpty())
                assertFalse(state.isLoading)
                assertEquals("Network error", state.error)
            }
        }

    @Test
    fun `markAsRead updates summary successfully`() =
        runTest {
            // Given
            val mockSummaries = MockDataFactory.createSummaryList(count = 3)
            coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flowOf(mockSummaries)
            coEvery { mockMarkAsReadUseCase(1, true) } returns Result.success(Unit)

            setupViewModel()
            viewModel.loadSummaries()
            advanceUntilIdle()

            // When
            viewModel.markAsRead(1, true)
            advanceUntilIdle()

            // Then
            coVerify { mockMarkAsReadUseCase(1, true) }
        }

    @Test
    fun `filterByTopic updates filter`() =
        runTest {
            // Given
            val allSummaries = MockDataFactory.createSummaryList(count = 5)
            val techSummaries = allSummaries.filter { it.topicTags.contains("technology") }

            coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flowOf(allSummaries)
            coEvery { mockGetSummariesUseCase(any(), "technology", any()) } returns flowOf(techSummaries)

            setupViewModel()
            viewModel.loadSummaries()
            advanceUntilIdle()

            // When
            viewModel.filterByTopic("technology")
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertEquals("technology", state.selectedTopic)
            }
            coVerify { mockGetSummariesUseCase(any(), "technology", any()) }
        }

    @Test
    fun `filterByReadStatus updates filter`() =
        runTest {
            // Given
            val allSummaries = MockDataFactory.createSummaryList(count = 5)
            val unreadSummaries = allSummaries.filter { !it.isRead }

            coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flowOf(allSummaries)
            coEvery { mockGetSummariesUseCase(any(), any(), false) } returns flowOf(unreadSummaries)

            setupViewModel()
            viewModel.loadSummaries()
            advanceUntilIdle()

            // When
            viewModel.filterByReadStatus(false)
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertEquals(false, state.readFilter)
            }
            coVerify { mockGetSummariesUseCase(any(), any(), false) }
        }

    @Test
    fun `refresh reloads summaries`() =
        runTest {
            // Given
            val mockSummaries = MockDataFactory.createSummaryList(count = 3)
            coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flowOf(mockSummaries)

            setupViewModel()

            // When
            viewModel.refresh()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertEquals(3, state.summaries.size)
                assertFalse(state.isRefreshing)
            }
        }

    @Test
    fun `clearFilters resets all filters`() =
        runTest {
            // Given
            val allSummaries = MockDataFactory.createSummaryList(count = 5)
            coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flowOf(allSummaries)

            setupViewModel()
            viewModel.filterByTopic("technology")
            viewModel.filterByReadStatus(false)
            advanceUntilIdle()

            // When
            viewModel.clearFilters()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertNull(state.selectedTopic)
                assertNull(state.readFilter)
            }
        }
}
