package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.presentation.PresentationConstants
import com.po4yka.bitesizereader.domain.usecase.DeleteSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.GetAvailableTagsUseCase
import com.po4yka.bitesizereader.domain.usecase.GetFilteredSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.LogoutUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.SearchSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import com.po4yka.bitesizereader.domain.usecase.ToggleFavoriteUseCase
import com.po4yka.bitesizereader.presentation.state.LayoutMode
import com.po4yka.bitesizereader.domain.model.ReadFilter
import com.po4yka.bitesizereader.domain.model.SortOrder
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import com.po4yka.bitesizereader.presentation.state.ViewDensity
import com.po4yka.bitesizereader.util.error.AppError
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

private const val LOAD_MORE_THRESHOLD = 5

@Factory
class SummaryListViewModel(
    private val getFilteredSummariesUseCase: GetFilteredSummariesUseCase,
    private val searchSummariesUseCase: SearchSummariesUseCase,
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase,
    private val deleteSummaryUseCase: DeleteSummaryUseCase,
    private val getAvailableTagsUseCase: GetAvailableTagsUseCase,
    private val searchHistoryManager: SearchHistoryManager,
    private val syncDataUseCase: SyncDataUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val logoutUseCase: LogoutUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : BaseViewModel(dispatcher) {
    private val _state = MutableStateFlow(SummaryListState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null
    private var loadJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        syncAndLoad()
        loadAvailableTags()
        loadTrendingTopics()
        loadRecentSearches()
    }

    /**
     * Syncs data from server and then loads summaries from local database.
     * Called on init and when user triggers refresh.
     */
    @Suppress("TooGenericExceptionCaught")
    fun syncAndLoad() {
        loadMoreJob?.cancel()
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                syncDataUseCase()
                logger.info { "Sync completed successfully" }
            } catch (_: AppError.SessionExpiredError) {
                logger.warn { "Session expired, triggering re-authentication" }
                logoutUseCase()
                // Continue to load local data despite auth error
            } catch (e: Exception) {
                logger.warn(e) { "Sync failed, loading from local cache" }
            }
            loadSummariesFromDatabase()
        }
    }

    /**
     * Pull-to-refresh action. Syncs with server and reloads from page 1.
     */
    fun refresh() {
        loadMoreJob?.cancel()
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    isRefreshing = true,
                    error = null,
                    page = 1,
                    summaries = emptyList(),
                )
            try {
                syncDataUseCase()
                logger.info { "Refresh sync completed" }
            } catch (e: Exception) {
                logger.warn(e) { "Refresh sync failed, loading from local cache" }
            }
            loadSummariesFromDatabase()
            _state.value = _state.value.copy(isRefreshing = false)
        }
    }

    /**
     * Loads summaries from local database without syncing.
     */
    fun loadSummaries() {
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true, error = null)
                loadSummariesFromDatabase()
            }
    }

    private suspend fun loadSummariesFromDatabase() {
        // Early cancellation check before starting work
        kotlin.coroutines.coroutineContext.ensureActive()

        val currentState = _state.value

        if (currentState.searchQuery.isNotBlank()) {
            // Use search when there's a query
            performSearch(currentState.searchQuery)
        } else {
            // Use filtered query.
            //
            // IMPORTANT: We only take a single snapshot (first()).
            // Collecting a DB-backed Flow indefinitely breaks paging because later DB emissions for
            // page=1 would overwrite/replace the currently appended pages.
            try {
                val summaries =
                    getFilteredSummariesUseCase(
                        page = currentState.page,
                        pageSize = PresentationConstants.DEFAULT_PAGE_SIZE,
                        readFilter = currentState.readFilter,
                        sortOrder = currentState.sortOrder,
                        selectedTag = currentState.selectedTag,
                    ).first()

                _state.value =
                    _state.value.copy(
                        summaries = summaries,
                        isLoading = false,
                        hasMore = summaries.size >= PresentationConstants.DEFAULT_PAGE_SIZE,
                        error = null,
                    )
            } catch (e: Exception) {
                logger.error(e) { "Failed to load summaries" }
                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        error = e.toAppError().userMessage(),
                    )
            }
        }
    }

    private fun loadAvailableTags() {
        viewModelScope.launch {
            try {
                val tags = getAvailableTagsUseCase()
                _state.value = _state.value.copy(availableTags = tags)
            } catch (e: Exception) {
                logger.warn(e) { "Failed to load available tags" }
            }
        }
    }

    private fun loadTrendingTopics() {
        searchHistoryManager.loadTrendingTopics(viewModelScope) { topics ->
            _state.value = _state.value.copy(trendingTopics = topics)
        }
    }

    /**
     * Called when user taps on a trending topic to search for it.
     */
    fun selectTrendingTopic(topic: String) {
        _state.value = _state.value.copy(searchQuery = topic)
        searchJob?.cancel()
        searchJob =
            viewModelScope.launch {
                searchHistoryManager.saveSearch(topic)
                performSearch(topic)
                loadRecentSearches()
            }
    }

    // Recent searches

    private fun loadRecentSearches() {
        searchHistoryManager.loadRecentSearches(viewModelScope) { searches ->
            _state.value = _state.value.copy(recentSearches = searches)
        }
    }

    /**
     * Called when user selects a recent search to perform that search.
     */
    fun selectRecentSearch(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        searchJob?.cancel()
        searchJob =
            viewModelScope.launch {
                searchHistoryManager.saveSearch(query)
                performSearch(query)
                loadRecentSearches()
            }
    }

    /**
     * Deletes a single search query from history.
     */
    fun deleteRecentSearch(query: String) {
        searchHistoryManager.deleteSearch(viewModelScope, query) {
            loadRecentSearches()
        }
    }

    /**
     * Clears all search history.
     */
    fun clearSearchHistory() {
        searchHistoryManager.clearHistory(viewModelScope) {
            _state.value = _state.value.copy(recentSearches = emptyList())
        }
    }

    // Search functionality

    fun toggleSearch() {
        val newSearchActive = !_state.value.isSearchActive
        _state.value =
            _state.value.copy(
                isSearchActive = newSearchActive,
                searchQuery = if (!newSearchActive) "" else _state.value.searchQuery,
            )
        if (!newSearchActive) {
            // Reload summaries when search is closed
            resetAndLoad()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        searchJob?.cancel()

        if (query.isBlank()) {
            resetAndLoad()
            return
        }

        searchJob =
            viewModelScope.launch {
                delay(PresentationConstants.SEARCH_DEBOUNCE_MS)
                performSearch(query)
            }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun performSearch(query: String) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        try {
            val results =
                searchSummariesUseCase(
                    query = query,
                    page = _state.value.page,
                    pageSize = PresentationConstants.DEFAULT_PAGE_SIZE,
                )
            _state.value =
                _state.value.copy(
                    summaries = results,
                    isLoading = false,
                    hasMore = results.size >= PresentationConstants.DEFAULT_PAGE_SIZE,
                )
        } catch (e: Exception) {
            logger.error(e) { "Search failed" }
            _state.value =
                _state.value.copy(
                    isLoading = false,
                    error = e.toAppError().userMessage(),
                )
        }
    }

    // Pagination / Infinite scroll

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
                val startState = _state.value
                _state.value = startState.copy(isLoadingMore = true)

                try {
                    val nextPage = startState.page + 1

                    if (startState.searchQuery.isNotBlank()) {
                        val results =
                            searchSummariesUseCase(
                                query = startState.searchQuery,
                                page = nextPage,
                                pageSize = PresentationConstants.DEFAULT_PAGE_SIZE,
                            )

                        val current = _state.value
                        _state.value =
                            current.copy(
                                summaries = current.summaries + results,
                                page = nextPage,
                                isLoadingMore = false,
                                hasMore = results.size >= PresentationConstants.DEFAULT_PAGE_SIZE,
                            )
                    } else {
                        val nextPageItems =
                            getFilteredSummariesUseCase(
                                page = nextPage,
                                pageSize = PresentationConstants.DEFAULT_PAGE_SIZE,
                                readFilter = startState.readFilter,
                                sortOrder = startState.sortOrder,
                                selectedTag = startState.selectedTag,
                            ).first()

                        // If filters/search changed while loading, don't merge incompatible pages.
                        val current = _state.value
                        val stateChanged =
                            current.readFilter != startState.readFilter ||
                                current.sortOrder != startState.sortOrder ||
                                current.searchQuery != startState.searchQuery ||
                                current.selectedTag != startState.selectedTag

                        if (stateChanged) {
                            _state.value = current.copy(isLoadingMore = false)
                            return@launch
                        }

                        _state.value =
                            current.copy(
                                summaries = current.summaries + nextPageItems,
                                page = nextPage,
                                isLoadingMore = false,
                                hasMore = nextPageItems.size >= PresentationConstants.DEFAULT_PAGE_SIZE,
                            )
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Failed to load more summaries" }
                    _state.value = _state.value.copy(isLoadingMore = false)
                }
            }
    }

    // Filtering

    fun onTagSelected(tag: String?) {
        _state.value = _state.value.copy(selectedTag = tag)
        resetAndLoad()
    }

    fun setReadFilter(filter: ReadFilter) {
        if (_state.value.readFilter != filter) {
            _state.value = _state.value.copy(readFilter = filter)
            resetAndLoad()
        }
    }

    fun setSortOrder(order: SortOrder) {
        if (_state.value.sortOrder != order) {
            _state.value = _state.value.copy(sortOrder = order)
            resetAndLoad()
        }
    }

    // Layout

    fun setLayoutMode(mode: LayoutMode) {
        _state.value = _state.value.copy(layoutMode = mode)
    }

    fun setViewDensity(density: ViewDensity) {
        _state.value = _state.value.copy(viewDensity = density)
    }

    // Actions

    fun markAsRead(id: String) {
        viewModelScope.launch {
            try {
                markSummaryAsReadUseCase(id)
                logger.debug { "Marked summary $id as read" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to mark summary as read: $id" }
            }
        }
    }

    fun deleteSummary(id: String) {
        viewModelScope.launch {
            try {
                deleteSummaryUseCase(id)
                // Remove from local list immediately for responsiveness
                _state.value =
                    _state.value.copy(
                        summaries = _state.value.summaries.filter { it.id != id },
                    )
                logger.info { "Deleted summary $id" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to delete summary: $id" }
                _state.value =
                    _state.value.copy(
                        error = e.toAppError().userMessage(),
                    )
            }
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(id)
                // Update local list immediately for responsiveness
                _state.value =
                    _state.value.copy(
                        summaries =
                            _state.value.summaries.map {
                                if (it.id == id) it.copy(isFavorited = !it.isFavorited) else it
                            },
                    )
                logger.debug { "Toggled favorite for summary $id" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to toggle favorite: $id" }
                _state.value =
                    _state.value.copy(
                        error = e.toAppError().userMessage(),
                    )
            }
        }
    }

    // Utility

    private fun resetAndLoad() {
        loadMoreJob?.cancel()
        _state.value =
            _state.value.copy(
                page = 1,
                summaries = emptyList(),
                hasMore = true,
            )
        loadSummaries()
    }
}
