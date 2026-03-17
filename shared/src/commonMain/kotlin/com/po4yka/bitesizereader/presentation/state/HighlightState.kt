package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Highlight

data class HighlightState(
    val highlights: List<Highlight> = emptyList(),
    val highlightedNodeOffsets: Set<Int> = emptySet(),
    val isHighlightModeActive: Boolean = false,
    val editingAnnotationHighlightId: String? = null,
    val annotationDraft: String = "",
)
