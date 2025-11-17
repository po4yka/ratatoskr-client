package com.po4yka.bitesizereader.presentation.viewmodel

import app.cash.turbine.test
import com.po4yka.bitesizereader.domain.model.SyncState
import com.po4yka.bitesizereader.domain.usecase.GetSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
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
    private val mockSyncDataUseCase = mockk<SyncDataUseCase>()
    private lateinit var testScope: TestScope
    private lateinit var viewModel: SummaryListViewModel

    private fun setupViewModel() {
        testScope = TestScope()
        coEvery { mockSyncDataUseCase(any()) } returns flowOf(SyncState.Idle)
        viewModel =
            SummaryListViewModel(
                getSummariesUseCase = mockGetSummariesUseCase,
                markSummaryAsReadUseCase = mockMarkAsReadUseCase,
                syncDataUseCase = mockSyncDataUseCase,
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
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.summaries.isEmpty())
                assertFalse(state.isLoading)
                assertTrue(state.filters.topicTags.isEmpty())
                assertTrue(state.filters.readStatus == null)
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
            advanceUntilIdle() // Let init block complete

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertEquals(5, state.summaries.size)
                assertFalse(state.isLoading)
                assertNull(state.error)
            }
            coVerify(atLeast = 1) { mockGetSummariesUseCase(any(), any(), any()) }
        }

    @Test
    fun `loadSummaries shows loading state`() =
        runTest {
            // Given
            val mockSummaries = MockDataFactory.createSummaryList(count = 3)
            coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flowOf(mockSummaries)

            setupViewModel()
            advanceUntilIdle() // Let init block complete

            // When
            viewModel.state.test {
                // Skip current state
                awaitItem()

                viewModel.loadSummaries()

                // Then - loading state
                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)
            }
        }

    // TODO: Fix this test - error state timing issue
    // @Test
    // fun `loadSummaries handles failure`() =
    //     runTest {
    //         // Given
    //         coEvery { mockGetSummariesUseCase(any(), any(), any()) } throws Exception("Network error")
    //
    //         // When
    //         setupViewModel()
    //         testScope.advanceUntilIdle() // Let init block complete (which will fail)
    //
    //         // Then
    //         viewModel.state.test {
    //             val state = awaitItem()
    //             assertTrue(state.summaries.isEmpty())
    //             assertEquals("Network error", state.error)
    //         }
    //     }

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
    fun `toggleTagFilter updates filter`() =
        runTest {
            // Given
            val allSummaries = MockDataFactory.createSummaryList(count = 5)

            coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flowOf(allSummaries)

            setupViewModel()
            advanceUntilIdle()

            // When
            viewModel.toggleTagFilter("technology")
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.filters.topicTags.contains("technology"))
            }
        }

    @Test
    fun `setReadFilter updates filter`() =
        runTest {
            // Given
            val allSummaries = MockDataFactory.createSummaryList(count = 5)

            coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flowOf(allSummaries)

            setupViewModel()
            advanceUntilIdle()

            // When
            viewModel.setReadFilter("unread")
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertEquals("unread", state.filters.readStatus)
            }
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
            viewModel.toggleTagFilter("technology")
            viewModel.setReadFilter("unread")
            advanceUntilIdle()

            // When
            viewModel.clearFilters()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.filters.topicTags.isEmpty())
                assertTrue(state.filters.readStatus == null)
            }
        }
}
