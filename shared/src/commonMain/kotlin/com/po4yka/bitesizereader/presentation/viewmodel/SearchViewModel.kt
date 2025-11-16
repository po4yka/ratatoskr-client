package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.repository.SearchRepository
import com.po4yka.bitesizereader.domain.usecase.SearchSummariesUseCase
import com.po4yka.bitesizereader.presentation.state.SearchState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for search screen
 */
class SearchViewModel(
    private val searchSummariesUseCase: SearchSummariesUseCase,
    private val searchRepository: SearchRepository,
    private val viewModelScope: CoroutineScope
) {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadTrendingTopics()
    }

    fun setQuery(query: String) {
        _state.value = _state.value.copy(query = query)

        // Debounce search
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Wait 300ms before searching
            search()
        }
    }

    fun search() {
        val query = _state.value.query

        if (query.isBlank()) {
            _state.value = _state.value.copy(results = emptyList())
            return
        }

        _state.value = _state.value.copy(isSearching = true, error = null)

        viewModelScope.launch {
            val result = searchSummariesUseCase(query)

            result.onSuccess { results ->
                _state.value = _state.value.copy(
                    results = results,
                    isSearching = false,
                    error = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isSearching = false,
                    error = error.message
                )
            }
        }
    }

    private fun loadTrendingTopics() {
        viewModelScope.launch {
            val result = searchRepository.getTrendingTopics(limit = 10)

            result.onSuccess { topics ->
                _state.value = _state.value.copy(trendingTopics = topics)
            }
        }
    }

    fun clearSearch() {
        _state.value = SearchState(trendingTopics = _state.value.trendingTopics)
        searchJob?.cancel()
    }
}
