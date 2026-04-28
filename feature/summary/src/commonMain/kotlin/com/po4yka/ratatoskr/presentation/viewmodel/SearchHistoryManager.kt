package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.domain.usecase.ClearSearchHistoryUseCase
import com.po4yka.ratatoskr.domain.usecase.DeleteSearchQueryUseCase
import com.po4yka.ratatoskr.domain.usecase.GetRecentSearchesUseCase
import com.po4yka.ratatoskr.domain.usecase.GetTrendingTopicsUseCase
import com.po4yka.ratatoskr.domain.usecase.SaveSearchQueryUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class SearchHistoryManager(
    private val getRecentSearchesUseCase: GetRecentSearchesUseCase,
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase,
    private val deleteSearchQueryUseCase: DeleteSearchQueryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
    private val getTrendingTopicsUseCase: GetTrendingTopicsUseCase,
) {
    fun loadRecentSearches(
        scope: CoroutineScope,
        onResult: (List<String>) -> Unit,
    ) {
        scope.launch {
            try {
                val searches = getRecentSearchesUseCase()
                onResult(searches)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                logger.warn(e) { "Failed to load recent searches" }
                onResult(emptyList())
            }
        }
    }

    fun loadTrendingTopics(
        scope: CoroutineScope,
        onResult: (List<String>) -> Unit,
    ) {
        scope.launch {
            try {
                val topics = getTrendingTopicsUseCase()
                onResult(topics)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                logger.warn(e) { "Failed to load trending topics" }
                onResult(emptyList())
            }
        }
    }

    suspend fun saveSearch(query: String) {
        saveSearchQueryUseCase(query)
    }

    fun deleteSearch(
        scope: CoroutineScope,
        query: String,
        onComplete: () -> Unit,
    ) {
        scope.launch {
            try {
                deleteSearchQueryUseCase(query)
                onComplete()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                logger.warn(e) { "Failed to delete recent search" }
                onComplete()
            }
        }
    }

    fun clearHistory(
        scope: CoroutineScope,
        onComplete: () -> Unit,
    ) {
        scope.launch {
            try {
                clearSearchHistoryUseCase()
                onComplete()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                logger.warn(e) { "Failed to clear search history" }
                onComplete()
            }
        }
    }
}
