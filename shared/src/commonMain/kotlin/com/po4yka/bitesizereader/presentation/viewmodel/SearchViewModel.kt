package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.GetTrendingTopicsUseCase
import com.po4yka.bitesizereader.domain.usecase.SearchSummariesUseCase
import com.po4yka.bitesizereader.presentation.state.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private const val DEBOUNCE_DELAY_MS = 500L
private const val DEFAULT_PAGE = 1
private const val DEFAULT_PAGE_SIZE = 20

@Factory
class SearchViewModel(
    private val searchSummariesUseCase: SearchSummariesUseCase,
    private val getTrendingTopicsUseCase: GetTrendingTopicsUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadTrendingTopics()
    }

    @Suppress("unused") // Public API for UI layer
    fun onQueryChanged(query: String) {
        _state.value = _state.value.copy(query = query)
        searchJob?.cancel()
        searchJob =
            viewModelScope.launch {
                delay(DEBOUNCE_DELAY_MS)
                if (query.isNotBlank()) {
                    performSearch(query)
                }
            }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadTrendingTopics() {
        viewModelScope.launch {
            try {
                val topics = getTrendingTopicsUseCase()
                _state.value = _state.value.copy(trendingTopics = topics)
            } catch (_: Exception) {
                // Trending topics are non-critical, silently ignore failures
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun performSearch(query: String) {
        _state.value = _state.value.copy(isLoading = true)
        try {
            val results = searchSummariesUseCase(query, DEFAULT_PAGE, DEFAULT_PAGE_SIZE)
            _state.value = _state.value.copy(results = results, isLoading = false)
        } catch (e: Exception) {
            _state.value = _state.value.copy(isLoading = false, error = e.message)
        }
    }
}
