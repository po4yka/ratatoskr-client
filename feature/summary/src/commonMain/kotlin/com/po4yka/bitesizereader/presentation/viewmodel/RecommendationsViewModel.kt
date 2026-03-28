package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.DismissRecommendationUseCase
import com.po4yka.bitesizereader.domain.usecase.GetRecommendationsUseCase
import com.po4yka.bitesizereader.domain.usecase.RefreshRecommendationsUseCase
import com.po4yka.bitesizereader.presentation.state.RecommendationsState
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

class RecommendationsViewModel(
    private val getRecommendationsUseCase: GetRecommendationsUseCase,
    private val refreshRecommendationsUseCase: RefreshRecommendationsUseCase,
    private val dismissRecommendationUseCase: DismissRecommendationUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(RecommendationsState())
    val state: StateFlow<RecommendationsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getRecommendationsUseCase().collect { recommendations ->
                _state.update { it.copy(recommendations = recommendations, isLoading = false) }
            }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                refreshRecommendationsUseCase()
                _state.update { it.copy(isLoading = false) }
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.warn(e) { "Failed to refresh recommendations" }
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun dismiss(id: String) {
        viewModelScope.launch {
            try {
                dismissRecommendationUseCase(id)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.warn(e) { "Failed to dismiss recommendation $id" }
            }
        }
    }
}
