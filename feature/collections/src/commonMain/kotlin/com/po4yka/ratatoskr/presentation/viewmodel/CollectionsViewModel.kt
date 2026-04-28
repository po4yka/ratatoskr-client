package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.feature.collections.domain.repository.CollectionRepository
import com.po4yka.ratatoskr.domain.usecase.CreateCollectionUseCase
import com.po4yka.ratatoskr.presentation.state.CollectionsState
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

class CollectionsViewModel(
    private val collectionRepository: CollectionRepository,
    private val createCollectionUseCase: CreateCollectionUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(CollectionsState())
    val state = _state.asStateFlow()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        collectionRepository.getCollections()
            .onStart { _state.update { it.copy(isLoading = true) } }
            .onEach { collections ->
                _state.update {
                    it.copy(
                        collections = collections,
                        isLoading = false,
                        error = null,
                    )
                }
            }
            .catch { e ->
                logger.warn(e) { "Failed to load collections" }
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load collections",
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun showCreateDialog() {
        _state.update { it.copy(showCreateDialog = true, createError = null) }
    }

    fun dismissCreateDialog() {
        _state.update { it.copy(showCreateDialog = false, createError = null) }
    }

    @Suppress("TooGenericExceptionCaught")
    fun createCollection(
        name: String,
        description: String?,
    ) {
        if (name.isBlank()) {
            _state.update { it.copy(createError = "Name cannot be empty") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isCreating = true, createError = null) }
            try {
                createCollectionUseCase(name, description?.takeIf { it.isNotBlank() })
                _state.update { it.copy(isCreating = false, showCreateDialog = false) }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to create collection" }
                _state.update {
                    it.copy(
                        isCreating = false,
                        createError = e.message ?: "Failed to create collection",
                    )
                }
            }
        }
    }
}
