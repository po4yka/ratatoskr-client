package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.presentation.PresentationConstants
import com.po4yka.ratatoskr.domain.usecase.ArchiveSummaryUseCase
import com.po4yka.ratatoskr.domain.usecase.DeleteSummaryUseCase
import com.po4yka.ratatoskr.domain.usecase.GetAvailableTagsUseCase
import com.po4yka.ratatoskr.domain.usecase.GetFilteredSummariesUseCase
import com.po4yka.ratatoskr.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.ratatoskr.domain.usecase.SearchSummariesUseCase
import com.po4yka.ratatoskr.domain.usecase.ToggleFavoriteUseCase
import com.po4yka.ratatoskr.feature.auth.api.AuthSessionPort
import com.po4yka.ratatoskr.feature.sync.domain.usecase.SyncDataUseCase
import com.po4yka.ratatoskr.presentation.state.LayoutMode
import com.po4yka.ratatoskr.domain.model.ReadFilter
import com.po4yka.ratatoskr.domain.model.SortOrder
import com.po4yka.ratatoskr.presentation.state.SummaryListState
import com.po4yka.ratatoskr.presentation.state.ViewDensity
import com.po4yka.ratatoskr.util.error.AppError
import com.po4yka.ratatoskr.util.error.toAppError
import com.po4yka.ratatoskr.util.error.userMessage
import com.po4yka.ratatoskr.util.network.NetworkMonitor
import com.po4yka.ratatoskr.util.network.NetworkStatus
import com.po4yka.ratatoskr.util.network.isConnected
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

private const val LOAD_MORE_THRESHOLD = 5

class SummaryListViewModel(
    private val getFilteredSummariesUseCase: GetFilteredSummariesUseCase,
    private val searchSummariesUseCase: SearchSummariesUseCase,
    markSummaryAsReadUseCase: MarkSummaryAsReadUseCase,
    deleteSummaryUseCase: DeleteSummaryUseCase,
    archiveSummaryUseCase: ArchiveSummaryUseCase,
    private val getAvailableTagsUseCase: GetAvailableTagsUseCase,
    searchHistoryManager: SearchHistoryManager,
    private val syncDataUseCase: SyncDataUseCase,
    toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val authSessionPort: AuthSessionPort,
    private val networkMonitor: NetworkMonitor,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : BaseViewModel(dispatcher) {
    private val _state = MutableStateFlow(SummaryListState())
    val state = _state.asStateFlow()

    private val stateAccessor = MutableStateFlowAccessor(_state)

    val searchDelegate =
        SummarySearchDelegate(
            scope = viewModelScope,
            stateAccessor = stateAccessor,
            searchSummariesUseCase = searchSummariesUseCase,
            searchHistoryManager = searchHistoryManager,
            onResetAndLoad = ::resetAndLoad,
        )

    val actionHandler =
        SummaryActionHandler(
            scope = viewModelScope,
            stateAccessor = stateAccessor,
            markSummaryAsReadUseCase = markSummaryAsReadUseCase,
            deleteSummaryUseCase = deleteSummaryUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            archiveSummaryUseCase = archiveSummaryUseCase,
        )

    val layoutPreferences = LayoutPreferencesManager(stateAccessor)

    private var loadJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        syncAndLoad()
        loadAvailableTags()
        searchDelegate.loadTrendingTopics()
        searchDelegate.loadRecentSearches()
        observeNetworkStatus()
        observeLastSyncTime()
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            var previousStatus: NetworkStatus? = null
            networkMonitor.networkStatus.collect { status ->
                _state.update { it.copy(isOffline = !status.isConnected()) }
                if (previousStatus?.isConnected() == false && status.isConnected()) {
                    logger.info { "Network reconnected, triggering delta sync" }
                    try {
                        syncDataUseCase()
                        _state.update { it.copy(syncError = null) }
                        loadSummariesFromDatabase()
                    } catch (e: Exception) {
                        val appError = e.toAppError()
                        _state.update { it.copy(syncError = appError.userMessage()) }
                        logger.warn(e) { "Auto-sync on reconnect failed (${appError::class.simpleName})" }
                    }
                }
                previousStatus = status
            }
        }
    }

    private fun observeLastSyncTime() {
        viewModelScope.launch {
            syncDataUseCase.syncState.collect { syncState ->
                _state.update { it.copy(lastSyncTime = syncState.lastSyncTime) }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun syncAndLoad() {
        loadMoreJob?.cancel()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                syncDataUseCase()
                _state.update { it.copy(syncError = null) }
                logger.info { "Sync completed successfully" }
            } catch (_: AppError.SessionExpiredError) {
                logger.warn { "Session expired, triggering re-authentication" }
                authSessionPort.logout()
            } catch (e: Exception) {
                val appError = e.toAppError()
                _state.update { it.copy(syncError = appError.userMessage()) }
                logger.warn(e) { "Sync failed (${appError::class.simpleName}), loading from local cache" }
            }
            loadSummariesFromDatabase()
        }
    }

    fun refresh() {
        loadMoreJob?.cancel()
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRefreshing = true,
                    error = null,
                    page = 1,
                    summaries = emptyList(),
                )
            }
            try {
                syncDataUseCase()
                _state.update { it.copy(syncError = null) }
                logger.info { "Refresh sync completed" }
            } catch (_: AppError.SessionExpiredError) {
                logger.warn { "Session expired during refresh, triggering re-authentication" }
                authSessionPort.logout()
            } catch (e: Exception) {
                val appError = e.toAppError()
                _state.update { it.copy(syncError = appError.userMessage()) }
                logger.warn(e) { "Refresh sync failed (${appError::class.simpleName}), loading from local cache" }
            }
            loadSummariesFromDatabase()
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun loadSummaries() {
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                loadSummariesFromDatabase()
            }
    }

    private suspend fun loadSummariesFromDatabase() {
        kotlin.coroutines.coroutineContext.ensureActive()

        val currentState = _state.value

        if (currentState.search.query.isNotBlank()) {
            searchDelegate.performSearch(currentState.search.query)
        } else {
            try {
                val summaries =
                    getFilteredSummariesUseCase(
                        page = currentState.page,
                        pageSize = PresentationConstants.DEFAULT_PAGE_SIZE,
                        readFilter = currentState.filter.readFilter,
                        sortOrder = currentState.filter.sortOrder,
                        selectedTag = currentState.filter.selectedTag,
                    ).first()

                _state.update {
                    it.copy(
                        summaries = summaries,
                        isLoading = false,
                        hasMore = summaries.size >= PresentationConstants.DEFAULT_PAGE_SIZE,
                        error = null,
                    )
                }
            } catch (e: Exception) {
                logger.error(e) { "Failed to load summaries" }
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.toAppError().userMessage(),
                    )
                }
            }
        }
    }

    private fun loadAvailableTags() {
        viewModelScope.launch {
            try {
                val tags = getAvailableTagsUseCase()
                _state.update {
                    it.copy(filter = it.filter.copy(availableTags = tags))
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to load available tags" }
            }
        }
    }

    // Search delegation

    fun toggleSearch() = searchDelegate.toggleSearch()

    fun onSearchQueryChanged(query: String) = searchDelegate.onSearchQueryChanged(query)

    fun selectTrendingTopic(topic: String) = searchDelegate.selectTrendingTopic(topic)

    fun selectRecentSearch(query: String) = searchDelegate.selectRecentSearch(query)

    fun deleteRecentSearch(query: String) = searchDelegate.deleteRecentSearch(query)

    fun clearSearchHistory() = searchDelegate.clearSearchHistory()

    // Action delegation

    fun markAsRead(id: String) = actionHandler.markAsRead(id)

    fun deleteSummary(id: String) = actionHandler.deleteSummary(id)

    fun toggleFavorite(id: String) = actionHandler.toggleFavorite(id)

    fun archiveSummary(id: String) = actionHandler.archiveSummary(id)

    // Layout delegation

    fun setLayoutMode(mode: LayoutMode) = layoutPreferences.setLayoutMode(mode)

    fun setViewDensity(density: ViewDensity) = layoutPreferences.setViewDensity(density)

    // Pagination

    fun loadMoreIfNeeded(lastVisibleIndex: Int) {
        val currentState = _state.value
        val shouldLoadMore =
            !currentState.isLoading &&
                !currentState.isLoadingMore &&
                currentState.hasMore &&
                lastVisibleIndex >= currentState.summaries.size - LOAD_MORE_THRESHOLD

        if (shouldLoadMore) {
            loadNextPage()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadNextPage() {
        loadMoreJob?.cancel()
        loadMoreJob =
            viewModelScope.launch {
                var startState = _state.value
                _state.update {
                    startState = it
                    it.copy(isLoadingMore = true)
                }

                try {
                    val nextPage = startState.page + 1

                    if (startState.search.query.isNotBlank()) {
                        val results =
                            searchSummariesUseCase(
                                query = startState.search.query,
                                page = nextPage,
                                pageSize = PresentationConstants.DEFAULT_PAGE_SIZE,
                            )

                        _state.update { current ->
                            current.copy(
                                summaries = current.summaries + results,
                                page = nextPage,
                                isLoadingMore = false,
                                hasMore = results.size >= PresentationConstants.DEFAULT_PAGE_SIZE,
                            )
                        }
                    } else {
                        val nextPageItems =
                            getFilteredSummariesUseCase(
                                page = nextPage,
                                pageSize = PresentationConstants.DEFAULT_PAGE_SIZE,
                                readFilter = startState.filter.readFilter,
                                sortOrder = startState.filter.sortOrder,
                                selectedTag = startState.filter.selectedTag,
                            ).first()

                        _state.update { current ->
                            val stateChanged =
                                current.filter.readFilter != startState.filter.readFilter ||
                                    current.filter.sortOrder != startState.filter.sortOrder ||
                                    current.search.query != startState.search.query ||
                                    current.filter.selectedTag != startState.filter.selectedTag

                            if (stateChanged) {
                                current.copy(isLoadingMore = false)
                            } else {
                                current.copy(
                                    summaries = current.summaries + nextPageItems,
                                    page = nextPage,
                                    isLoadingMore = false,
                                    hasMore = nextPageItems.size >= PresentationConstants.DEFAULT_PAGE_SIZE,
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Failed to load more summaries" }
                    _state.update { it.copy(isLoadingMore = false) }
                }
            }
    }

    // Filtering

    fun onTagSelected(tag: String?) {
        _state.update { it.copy(filter = it.filter.copy(selectedTag = tag)) }
        resetAndLoad()
    }

    fun setReadFilter(filter: ReadFilter) {
        if (_state.value.filter.readFilter != filter) {
            _state.update { it.copy(filter = it.filter.copy(readFilter = filter)) }
            resetAndLoad()
        }
    }

    fun setSortOrder(order: SortOrder) {
        if (_state.value.filter.sortOrder != order) {
            _state.update { it.copy(filter = it.filter.copy(sortOrder = order)) }
            resetAndLoad()
        }
    }

    // Utility

    private fun resetAndLoad() {
        loadMoreJob?.cancel()
        _state.update {
            it.copy(
                page = 1,
                summaries = emptyList(),
                hasMore = true,
            )
        }
        loadSummaries()
    }
}
