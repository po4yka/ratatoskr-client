package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.ClearSearchHistoryUseCase
import com.po4yka.bitesizereader.domain.usecase.DeleteSearchQueryUseCase
import com.po4yka.bitesizereader.domain.usecase.GetRecentSearchesUseCase
import com.po4yka.bitesizereader.domain.usecase.GetTrendingTopicsUseCase
import com.po4yka.bitesizereader.domain.usecase.SaveSearchQueryUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
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
                logger.warn(e) { "Failed to load recent searches" }
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
                logger.warn(e) { "Failed to load trending topics" }
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
                logger.warn(e) { "Failed to delete recent search" }
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
                logger.warn(e) { "Failed to clear search history" }
            }
        }
    }
}
