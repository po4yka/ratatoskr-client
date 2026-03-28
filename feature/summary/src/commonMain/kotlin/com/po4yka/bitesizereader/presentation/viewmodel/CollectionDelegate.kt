package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import com.po4yka.bitesizereader.domain.usecase.AddToCollectionUseCase
import com.po4yka.bitesizereader.presentation.state.CollectionDialogState
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

class CollectionDelegate(
    private val collectionRepository: CollectionRepository,
    private val addToCollectionUseCase: AddToCollectionUseCase,
) {
    @Suppress("TooGenericExceptionCaught")
    fun showAddToCollection(
        scope: CoroutineScope,
        currentState: () -> CollectionDialogState,
        onState: (CollectionDialogState) -> Unit,
    ) {
        onState(currentState().copy(showDialog = true, isLoading = true, error = null))
        scope.launch {
            try {
                val collections = collectionRepository.getCollections().first()
                onState(currentState().copy(collections = collections, isLoading = false))
            } catch (e: Exception) {
                logger.warn(e) { "Failed to load collections" }
                onState(
                    currentState().copy(
                        isLoading = false,
                        error = e.toAppError().userMessage(),
                    ),
                )
            }
        }
    }

    fun dismissAddToCollection(
        currentState: () -> CollectionDialogState,
        onState: (CollectionDialogState) -> Unit,
    ) {
        onState(currentState().copy(showDialog = false, error = null))
    }

    @Suppress("TooGenericExceptionCaught")
    fun addToCollection(
        collectionId: String,
        summaryId: String,
        scope: CoroutineScope,
        currentState: () -> CollectionDialogState,
        onState: (CollectionDialogState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isAdding = true, error = null))
            try {
                addToCollectionUseCase(collectionId, summaryId)
                onState(currentState().copy(isAdding = false, showDialog = false))
            } catch (e: Exception) {
                logger.warn(e) { "Failed to add to collection" }
                onState(
                    currentState().copy(
                        isAdding = false,
                        error = e.toAppError().userMessage(),
                    ),
                )
            }
        }
    }
}
