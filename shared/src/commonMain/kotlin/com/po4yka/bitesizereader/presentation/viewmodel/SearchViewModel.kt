package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.GetSearchInsightsUseCase
import com.po4yka.bitesizereader.domain.usecase.SearchSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.SemanticSearchUseCase
import com.po4yka.bitesizereader.presentation.state.SearchFilters
import com.po4yka.bitesizereader.presentation.state.SearchMode
import com.po4yka.bitesizereader.presentation.state.SearchState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private const val DEBOUNCE_DELAY_MS = 500L
private const val DEFAULT_PAGE = 1
private const val DEFAULT_PAGE_SIZE = 20

@Factory
class SearchViewModel(
    private val searchSummariesUseCase: SearchSummariesUseCase,
    private val semanticSearchUseCase: SemanticSearchUseCase,
    private val searchHistoryManager: SearchHistoryManager,
    private val getSearchInsightsUseCase: GetSearchInsightsUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : BaseViewModel(dispatcher) {
    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        loadTrendingTopics()
        loadRecentSearches()
        loadInsights()
    }

    /**
     * Called when the search query changes. Debounces input and triggers search.
     */
    fun onQueryChanged(query: String) {
        _state.update { it.copy(query = query, error = null) }
        searchJob?.cancel()

        if (query.isBlank()) {
            _state.update { it.copy(results = emptyList(), currentPage = 1, hasMoreResults = false) }
            return
        }

        searchJob =
            viewModelScope.launch {
                delay(DEBOUNCE_DELAY_MS)
                performSearch(query, page = DEFAULT_PAGE, isNewSearch = true)
            }
    }

    /**
     * Selects a trending topic and performs search.
     */
    fun selectTrendingTopic(topic: String) {
        _state.update { it.copy(query = topic) }
        searchJob?.cancel()
        searchJob =
            viewModelScope.launch {
                searchHistoryManager.saveSearch(topic)
                performSearch(topic, page = DEFAULT_PAGE, isNewSearch = true)
                loadRecentSearches()
            }
    }

    /**
     * Selects a recent search and performs search.
     */
    fun selectRecentSearch(query: String) {
        _state.update { it.copy(query = query) }
        searchJob?.cancel()
        searchJob =
            viewModelScope.launch {
                searchHistoryManager.saveSearch(query)
                performSearch(query, page = DEFAULT_PAGE, isNewSearch = true)
                loadRecentSearches()
            }
    }

    /**
     * Loads more results for pagination.
     */
    fun loadMoreResults() {
        val currentState = _state.value
        if (currentState.isLoadingMore || !currentState.hasMoreResults || currentState.query.isBlank()) {
            return
        }

        viewModelScope.launch {
            performSearch(
                query = currentState.query,
                page = currentState.currentPage + 1,
                isNewSearch = false,
            )
        }
    }

    /**
     * Toggles between fulltext and semantic search modes.
     */
    fun toggleSearchMode() {
        val newMode =
            when (_state.value.searchMode) {
                SearchMode.FULLTEXT -> SearchMode.SEMANTIC
                SearchMode.SEMANTIC -> SearchMode.FULLTEXT
            }
        _state.update { it.copy(searchMode = newMode) }

        // Re-search with new mode if there's a query
        if (_state.value.query.isNotBlank()) {
            searchJob?.cancel()
            searchJob =
                viewModelScope.launch {
                    performSearch(_state.value.query, page = DEFAULT_PAGE, isNewSearch = true)
                }
        }
    }

    /**
     * Toggles the filters panel visibility.
     */
    fun toggleFiltersPanel() {
        _state.update { it.copy(showFilters = !it.showFilters) }
    }

    /**
     * Updates the search filters and re-executes search.
     */
    fun updateFilters(filters: SearchFilters) {
        _state.update { it.copy(filters = filters) }

        // Re-search with new filters if there's a query
        if (_state.value.query.isNotBlank()) {
            searchJob?.cancel()
            searchJob =
                viewModelScope.launch {
                    performSearch(_state.value.query, page = DEFAULT_PAGE, isNewSearch = true)
                }
        }
    }

    /**
     * Deletes a single recent search query.
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
            _state.update { it.copy(recentSearches = emptyList()) }
        }
    }

    /**
     * Clears the current error.
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    /**
     * Clears search results and resets to initial state.
     */
    fun clearResults() {
        searchJob?.cancel()
        _state.update {
            it.copy(
                query = "",
                results = emptyList(),
                currentPage = 1,
                hasMoreResults = false,
                isLoading = false,
                isLoadingMore = false,
                error = null,
            )
        }
    }

    private fun loadRecentSearches() {
        searchHistoryManager.loadRecentSearches(viewModelScope) { searches ->
            _state.update { it.copy(recentSearches = searches) }
        }
    }

    private fun loadTrendingTopics() {
        searchHistoryManager.loadTrendingTopics(viewModelScope) { topics ->
            _state.update { it.copy(trendingTopics = topics) }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadInsights() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingInsights = true) }
            try {
                val insights = getSearchInsightsUseCase()
                _state.update { it.copy(insights = insights, isLoadingInsights = false) }
            } catch (_: Exception) {
                _state.update { it.copy(isLoadingInsights = false) }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun performSearch(
        query: String,
        page: Int,
        isNewSearch: Boolean,
    ) {
        if (isNewSearch) {
            _state.update { it.copy(isLoading = true, error = null) }
        } else {
            _state.update { it.copy(isLoadingMore = true) }
        }

        try {
            val currentState = _state.value
            val results =
                when (currentState.searchMode) {
                    SearchMode.FULLTEXT -> searchSummariesUseCase(query, page, DEFAULT_PAGE_SIZE)
                    SearchMode.SEMANTIC ->
                        semanticSearchUseCase(
                            query = query,
                            page = page,
                            pageSize = DEFAULT_PAGE_SIZE,
                            language = currentState.filters.language,
                            tags = currentState.filters.tags.ifEmpty { null },
                        )
                }
            val hasMore = results.size >= DEFAULT_PAGE_SIZE

            _state.update { currentState ->
                val newResults =
                    if (isNewSearch) {
                        results
                    } else {
                        currentState.results + results
                    }

                currentState.copy(
                    results = newResults,
                    currentPage = page,
                    hasMoreResults = hasMore,
                    isLoading = false,
                    isLoadingMore = false,
                )
            }

            // Save successful search to history
            if (isNewSearch && results.isNotEmpty()) {
                searchHistoryManager.saveSearch(query)
                loadRecentSearches()
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = e.message ?: "Search failed",
                )
            }
        }
    }
}
