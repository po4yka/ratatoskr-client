package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.usecase.ArchiveSummaryUseCase
import com.po4yka.ratatoskr.domain.usecase.ClearSearchHistoryUseCase
import com.po4yka.ratatoskr.domain.usecase.DeleteSearchQueryUseCase
import com.po4yka.ratatoskr.domain.usecase.DeleteSummaryUseCase
import com.po4yka.ratatoskr.domain.usecase.GetAvailableTagsUseCase
import com.po4yka.ratatoskr.domain.usecase.GetFilteredSummariesUseCase
import com.po4yka.ratatoskr.domain.usecase.GetRecentSearchesUseCase
import com.po4yka.ratatoskr.domain.usecase.GetTrendingTopicsUseCase
import com.po4yka.ratatoskr.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.ratatoskr.domain.usecase.SaveSearchQueryUseCase
import com.po4yka.ratatoskr.domain.usecase.SearchSummariesUseCase
import com.po4yka.ratatoskr.feature.auth.api.AuthSessionPort
import com.po4yka.ratatoskr.feature.sync.domain.usecase.SyncDataUseCase
import com.po4yka.ratatoskr.domain.usecase.ToggleFavoriteUseCase
import com.po4yka.ratatoskr.domain.model.ReadFilter
import com.po4yka.ratatoskr.domain.model.SortOrder
import com.po4yka.ratatoskr.domain.model.SyncState
import com.po4yka.ratatoskr.util.network.NetworkMonitor
import com.po4yka.ratatoskr.util.network.NetworkStatus
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
    private val archiveSummaryUseCase: ArchiveSummaryUseCase = mockk()
    private val getAvailableTagsUseCase: GetAvailableTagsUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
    private val getTrendingTopicsUseCase: GetTrendingTopicsUseCase = mockk()
    private val getRecentSearchesUseCase: GetRecentSearchesUseCase = mockk()
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase = mockk()
    private val deleteSearchQueryUseCase: DeleteSearchQueryUseCase = mockk()
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase = mockk()
    private val syncDataUseCase: SyncDataUseCase = mockk()
    private val authSessionPort: AuthSessionPort = mockk(relaxed = true)
    private val networkMonitor: NetworkMonitor = mockk()
    private lateinit var searchHistoryManager: SearchHistoryManager
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
        every { syncDataUseCase.syncState } returns flowOf(SyncState(lastSyncTime = null, lastSyncHash = null))
        coEvery { getAvailableTagsUseCase() } returns listOf("tech", "news")
        coEvery { getTrendingTopicsUseCase() } returns listOf("Tech", "Science")
        coEvery { getRecentSearchesUseCase() } returns emptyList()
        coEvery { saveSearchQueryUseCase(any()) } returns Unit
        coEvery { deleteSearchQueryUseCase(any()) } returns Unit
        coEvery { clearSearchHistoryUseCase() } returns Unit
        every { networkMonitor.networkStatus } returns flowOf(NetworkStatus.CONNECTED)
        every { authSessionPort.isAuthenticated } returns flowOf(true)
        coEvery { authSessionPort.checkAuthStatus() } returns Unit
        coEvery { authSessionPort.logout() } returns Unit
        every {
            getFilteredSummariesUseCase(
                page = any(),
                pageSize = any(),
                readFilter = any(),
                sortOrder = any(),
                selectedTag = any(),
            )
        } returns flowOf(listOf(testSummary))

        searchHistoryManager =
            SearchHistoryManager(
                getRecentSearchesUseCase = getRecentSearchesUseCase,
                saveSearchQueryUseCase = saveSearchQueryUseCase,
                deleteSearchQueryUseCase = deleteSearchQueryUseCase,
                clearSearchHistoryUseCase = clearSearchHistoryUseCase,
                getTrendingTopicsUseCase = getTrendingTopicsUseCase,
            )

        viewModel =
            SummaryListViewModel(
                getFilteredSummariesUseCase = getFilteredSummariesUseCase,
                searchSummariesUseCase = searchSummariesUseCase,
                markSummaryAsReadUseCase = markSummaryAsReadUseCase,
                deleteSummaryUseCase = deleteSummaryUseCase,
                archiveSummaryUseCase = archiveSummaryUseCase,
                getAvailableTagsUseCase = getAvailableTagsUseCase,
                searchHistoryManager = searchHistoryManager,
                syncDataUseCase = syncDataUseCase,
                toggleFavoriteUseCase = toggleFavoriteUseCase,
                authSessionPort = authSessionPort,
                networkMonitor = networkMonitor,
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
            assertEquals(listOf("tech", "news"), state.filter.availableTags)
        }

    @Test
    fun `setReadFilter updates state and reloads`() =
        runTest {
            advanceUntilIdle()
            viewModel.setReadFilter(ReadFilter.UNREAD)
            advanceUntilIdle()
            val state = viewModel.state.value
            assertEquals(ReadFilter.UNREAD, state.filter.readFilter)
        }

    @Test
    fun `setSortOrder updates state and reloads`() =
        runTest {
            advanceUntilIdle()
            viewModel.setSortOrder(SortOrder.OLDEST)
            advanceUntilIdle()
            val state = viewModel.state.value
            assertEquals(SortOrder.OLDEST, state.filter.sortOrder)
        }
}
