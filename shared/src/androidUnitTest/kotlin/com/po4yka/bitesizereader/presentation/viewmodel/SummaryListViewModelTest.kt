package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.usecase.ClearSearchHistoryUseCase
import com.po4yka.bitesizereader.domain.usecase.DeleteSearchQueryUseCase
import com.po4yka.bitesizereader.domain.usecase.DeleteSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.GetAvailableTagsUseCase
import com.po4yka.bitesizereader.domain.usecase.GetFilteredSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.GetRecentSearchesUseCase
import com.po4yka.bitesizereader.domain.usecase.GetTrendingTopicsUseCase
import com.po4yka.bitesizereader.domain.usecase.LogoutUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.SaveSearchQueryUseCase
import com.po4yka.bitesizereader.domain.usecase.SearchSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import com.po4yka.bitesizereader.domain.model.ReadFilter
import com.po4yka.bitesizereader.domain.model.SortOrder
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.time.Clock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SummaryListViewModelTest {
    private val getFilteredSummariesUseCase: GetFilteredSummariesUseCase = mockk()
    private val searchSummariesUseCase: SearchSummariesUseCase = mockk()
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase = mockk()
    private val deleteSummaryUseCase: DeleteSummaryUseCase = mockk()
    private val getAvailableTagsUseCase: GetAvailableTagsUseCase = mockk()
    private val getTrendingTopicsUseCase: GetTrendingTopicsUseCase = mockk()
    private val getRecentSearchesUseCase: GetRecentSearchesUseCase = mockk()
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase = mockk()
    private val deleteSearchQueryUseCase: DeleteSearchQueryUseCase = mockk()
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase = mockk()
    private val syncDataUseCase: SyncDataUseCase = mockk()
    private val logoutUseCase: LogoutUseCase = mockk()
    private lateinit var viewModel: SummaryListViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testSummary =
        Summary(
            id = "1",
            title = "Test Summary",
            content = "Content",
            sourceUrl = "https://example.com/article",
            imageUrl = null,
            createdAt = Clock.System.now(),
            isRead = false,
            tags = emptyList(),
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { syncDataUseCase() } returns Unit
        coEvery { getAvailableTagsUseCase() } returns listOf("tech", "news")
        coEvery { getTrendingTopicsUseCase() } returns listOf("Tech", "Science")
        coEvery { getRecentSearchesUseCase() } returns emptyList()
        coEvery { saveSearchQueryUseCase(any()) } returns Unit
        coEvery { deleteSearchQueryUseCase(any()) } returns Unit
        coEvery { clearSearchHistoryUseCase() } returns Unit
        every {
            getFilteredSummariesUseCase(
                page = any(),
                pageSize = any(),
                readFilter = any(),
                sortOrder = any(),
            )
        } returns flowOf(listOf(testSummary))

        viewModel =
            SummaryListViewModel(
                getFilteredSummariesUseCase = getFilteredSummariesUseCase,
                searchSummariesUseCase = searchSummariesUseCase,
                markSummaryAsReadUseCase = markSummaryAsReadUseCase,
                deleteSummaryUseCase = deleteSummaryUseCase,
                getAvailableTagsUseCase = getAvailableTagsUseCase,
                getTrendingTopicsUseCase = getTrendingTopicsUseCase,
                getRecentSearchesUseCase = getRecentSearchesUseCase,
                saveSearchQueryUseCase = saveSearchQueryUseCase,
                deleteSearchQueryUseCase = deleteSearchQueryUseCase,
                clearSearchHistoryUseCase = clearSearchHistoryUseCase,
                syncDataUseCase = syncDataUseCase,
                logoutUseCase = logoutUseCase,
                dispatcher = testDispatcher,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads summaries`() =
        runTest {
            advanceUntilIdle()
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals(1, state.summaries.size)
            assertEquals("Test Summary", state.summaries[0].title)
        }

    @Test
    fun `initial state loads available tags`() =
        runTest {
            advanceUntilIdle()
            val state = viewModel.state.value
            assertEquals(listOf("tech", "news"), state.availableTags)
        }

    @Test
    fun `setReadFilter updates state and reloads`() =
        runTest {
            advanceUntilIdle()
            viewModel.setReadFilter(ReadFilter.UNREAD)
            advanceUntilIdle()
            val state = viewModel.state.value
            assertEquals(ReadFilter.UNREAD, state.readFilter)
        }

    @Test
    fun `setSortOrder updates state and reloads`() =
        runTest {
            advanceUntilIdle()
            viewModel.setSortOrder(SortOrder.OLDEST)
            advanceUntilIdle()
            val state = viewModel.state.value
            assertEquals(SortOrder.OLDEST, state.sortOrder)
        }
}
