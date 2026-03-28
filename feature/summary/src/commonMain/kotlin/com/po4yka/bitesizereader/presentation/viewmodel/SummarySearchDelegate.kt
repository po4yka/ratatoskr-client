package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.SearchSummariesUseCase
import com.po4yka.bitesizereader.presentation.PresentationConstants
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

class SummarySearchDelegate(
    private val scope: CoroutineScope,
    private val stateAccessor: StateAccessor<SummaryListState>,
    private val searchSummariesUseCase: SearchSummariesUseCase,
    private val searchHistoryManager: SearchHistoryManager,
    private val onResetAndLoad: () -> Unit,
) {
    private var searchJob: Job? = null

    fun toggleSearch() {
        var wasSearchActive = false
        stateAccessor.update {
            wasSearchActive = it.search.isActive
            it.copy(
                search =
                    it.search.copy(
                        isActive = !it.search.isActive,
                        query = if (it.search.isActive) "" else it.search.query,
                    ),
            )
        }
        if (wasSearchActive) {
            onResetAndLoad()
        }
    }

    fun onSearchQueryChanged(query: String) {
        stateAccessor.update { it.copy(search = it.search.copy(query = query)) }
        searchJob?.cancel()

        if (query.isBlank()) {
            onResetAndLoad()
            return
        }

        searchJob =
            scope.launch {
                delay(PresentationConstants.SEARCH_DEBOUNCE_MS)
                performSearch(query)
            }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun performSearch(query: String) {
        stateAccessor.update { it.copy(isLoading = true, error = null) }
        try {
            val page = stateAccessor.value.page
            val results =
                searchSummariesUseCase(
                    query = query,
                    page = page,
                    pageSize = PresentationConstants.DEFAULT_PAGE_SIZE,
                )
            stateAccessor.update {
                it.copy(
                    summaries = results,
                    isLoading = false,
                    hasMore = results.size >= PresentationConstants.DEFAULT_PAGE_SIZE,
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Search failed" }
            stateAccessor.update {
                it.copy(
                    isLoading = false,
                    error = e.toAppError().userMessage(),
                )
            }
        }
    }

    fun selectTrendingTopic(topic: String) {
        stateAccessor.update { it.copy(search = it.search.copy(query = topic)) }
        searchJob?.cancel()
        searchJob =
            scope.launch {
                searchHistoryManager.saveSearch(topic)
                performSearch(topic)
                loadRecentSearches()
            }
    }

    fun selectRecentSearch(query: String) {
        stateAccessor.update { it.copy(search = it.search.copy(query = query)) }
        searchJob?.cancel()
        searchJob =
            scope.launch {
                searchHistoryManager.saveSearch(query)
                performSearch(query)
                loadRecentSearches()
            }
    }

    fun deleteRecentSearch(query: String) {
        searchHistoryManager.deleteSearch(scope, query) {
            loadRecentSearches()
        }
    }

    fun clearSearchHistory() {
        searchHistoryManager.clearHistory(scope) {
            stateAccessor.update {
                it.copy(search = it.search.copy(recentSearches = emptyList()))
            }
        }
    }

    fun loadRecentSearches() {
        searchHistoryManager.loadRecentSearches(scope) { searches ->
            stateAccessor.update {
                it.copy(search = it.search.copy(recentSearches = searches))
            }
        }
    }

    fun loadTrendingTopics() {
        searchHistoryManager.loadTrendingTopics(scope) { topics ->
            stateAccessor.update {
                it.copy(search = it.search.copy(trendingTopics = topics))
            }
        }
    }
}
