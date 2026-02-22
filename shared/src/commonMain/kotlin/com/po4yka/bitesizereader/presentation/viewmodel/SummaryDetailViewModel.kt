package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import com.po4yka.bitesizereader.domain.usecase.AddToCollectionUseCase
import com.po4yka.bitesizereader.domain.usecase.DeleteSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryByIdUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryContentUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.ToggleFavoriteUseCase
import com.po4yka.bitesizereader.presentation.state.SummaryDetailState
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class SummaryDetailViewModel(
    private val getSummaryByIdUseCase: GetSummaryByIdUseCase,
    private val getSummaryContentUseCase: GetSummaryContentUseCase,
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase,
    private val deleteSummaryUseCase: DeleteSummaryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val collectionRepository: CollectionRepository,
    private val addToCollectionUseCase: AddToCollectionUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SummaryDetailState())
    val state = _state.asStateFlow()

    @Suppress("TooGenericExceptionCaught")
    fun loadSummary(id: String) {
        viewModelScope.launch {
            _state.value = SummaryDetailState(isLoading = true)
            try {
                val summary = getSummaryByIdUseCase(id)
                _state.value = _state.value.copy(summary = summary, isLoading = false)
                if (summary != null && !summary.isRead) {
                    markSummaryAsReadUseCase(id)
                }
                if (summary != null) {
                    fetchFullContent(id)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.toAppError().userMessage())
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun fetchFullContent(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingContent = true)
            try {
                val fullContent = getSummaryContentUseCase(id)
                if (fullContent != null) {
                    _state.value =
                        _state.value.copy(
                            summary = _state.value.summary?.copy(content = fullContent),
                            isLoadingContent = false,
                        )
                } else {
                    _state.value = _state.value.copy(isLoadingContent = false)
                }
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(
                        isLoadingContent = false,
                        error = e.toAppError().userMessage(),
                    )
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun toggleFavorite() {
        val summary = _state.value.summary ?: return
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(summary.id)
                _state.value =
                    _state.value.copy(
                        summary = summary.copy(isFavorited = !summary.isFavorited),
                    )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.toAppError().userMessage())
            }
        }
    }

    @Suppress("unused", "TooGenericExceptionCaught")
    fun deleteSummary(id: String) {
        viewModelScope.launch {
            try {
                deleteSummaryUseCase(id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.toAppError().userMessage())
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun showAddToCollection() {
        _state.value = _state.value.copy(
            showAddToCollectionDialog = true,
            isLoadingCollections = true,
            addToCollectionError = null,
        )
        viewModelScope.launch {
            try {
                val collections = collectionRepository.getCollections().first()
                _state.value = _state.value.copy(
                    collections = collections,
                    isLoadingCollections = false,
                )
            } catch (e: Exception) {
                logger.warn(e) { "Failed to load collections" }
                _state.value = _state.value.copy(
                    isLoadingCollections = false,
                    addToCollectionError = e.toAppError().userMessage(),
                )
            }
        }
    }

    fun dismissAddToCollection() {
        _state.value = _state.value.copy(
            showAddToCollectionDialog = false,
            addToCollectionError = null,
        )
    }

    @Suppress("TooGenericExceptionCaught")
    fun addToCollection(collectionId: String) {
        val summaryId = _state.value.summary?.id ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isAddingToCollection = true, addToCollectionError = null)
            try {
                addToCollectionUseCase(collectionId, summaryId)
                _state.value = _state.value.copy(
                    isAddingToCollection = false,
                    showAddToCollectionDialog = false,
                )
            } catch (e: Exception) {
                logger.warn(e) { "Failed to add to collection" }
                _state.value = _state.value.copy(
                    isAddingToCollection = false,
                    addToCollectionError = e.toAppError().userMessage(),
                )
            }
        }
    }
}
