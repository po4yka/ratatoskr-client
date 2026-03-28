package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.GetHighlightsUseCase
import com.po4yka.bitesizereader.domain.usecase.ToggleHighlightUseCase
import com.po4yka.bitesizereader.domain.usecase.UpdateAnnotationUseCase
import com.po4yka.bitesizereader.presentation.state.HighlightState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class HighlightDelegate(
    private val getHighlightsUseCase: GetHighlightsUseCase,
    private val toggleHighlightUseCase: ToggleHighlightUseCase,
    private val updateAnnotationUseCase: UpdateAnnotationUseCase,
) {
    fun observeHighlights(
        summaryId: String,
        scope: CoroutineScope,
        currentState: () -> HighlightState,
        onState: (HighlightState) -> Unit,
    ) {
        scope.launch {
            getHighlightsUseCase(summaryId).collect { highlights ->
                onState(
                    currentState().copy(
                        highlights = highlights,
                        highlightedNodeOffsets = highlights.map { it.nodeOffset }.toSet(),
                    ),
                )
            }
        }
    }

    fun toggleHighlightMode(
        currentState: () -> HighlightState,
        onState: (HighlightState) -> Unit,
    ) {
        val state = currentState()
        onState(state.copy(isHighlightModeActive = !state.isHighlightModeActive))
    }

    fun toggleHighlight(
        summaryId: String,
        nodeOffset: Int,
        text: String,
        currentState: () -> HighlightState,
        scope: CoroutineScope,
    ) {
        scope.launch {
            toggleHighlightUseCase(
                summaryId = summaryId,
                nodeOffset = nodeOffset,
                text = text,
                existingHighlights = currentState().highlights,
            )
        }
    }

    fun openAnnotationEditor(
        highlightId: String,
        currentState: () -> HighlightState,
        onState: (HighlightState) -> Unit,
    ) {
        val note = currentState().highlights.find { it.id == highlightId }?.note ?: ""
        onState(currentState().copy(editingAnnotationHighlightId = highlightId, annotationDraft = note))
    }

    fun updateAnnotationDraft(
        text: String,
        currentState: () -> HighlightState,
        onState: (HighlightState) -> Unit,
    ) {
        onState(currentState().copy(annotationDraft = text))
    }

    fun saveAnnotation(
        scope: CoroutineScope,
        currentState: () -> HighlightState,
        onState: (HighlightState) -> Unit,
    ) {
        val highlightId = currentState().editingAnnotationHighlightId ?: return
        val note = currentState().annotationDraft.trim().ifEmpty { null }
        scope.launch {
            updateAnnotationUseCase(highlightId, note)
            onState(currentState().copy(editingAnnotationHighlightId = null, annotationDraft = ""))
        }
    }

    fun closeAnnotationEditor(
        currentState: () -> HighlightState,
        onState: (HighlightState) -> Unit,
    ) {
        onState(currentState().copy(editingAnnotationHighlightId = null, annotationDraft = ""))
    }
}
