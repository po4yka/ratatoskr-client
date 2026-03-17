package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.AudioPlaybackState
import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.Highlight
import com.po4yka.bitesizereader.domain.model.ReadingPreferences
import com.po4yka.bitesizereader.domain.model.Summary

data class SummaryDetailState(
    val summary: Summary? = null,
    val isLoading: Boolean = false,
    val isLoadingContent: Boolean = false,
    val error: String? = null,
    // Reading position
    val lastReadPosition: Int = 0,
    val lastReadOffset: Int = 0,
    // Reading preferences
    val readingPreferences: ReadingPreferences = ReadingPreferences(),
    val showReadingSettings: Boolean = false,
    // Add to collection
    val showAddToCollectionDialog: Boolean = false,
    val collections: List<Collection> = emptyList(),
    val isLoadingCollections: Boolean = false,
    val isAddingToCollection: Boolean = false,
    val addToCollectionError: String? = null,
    // Audio playback
    val audioState: AudioPlaybackState? = null,
    // Highlights
    val highlights: List<Highlight> = emptyList(),
    val highlightedNodeOffsets: Set<Int> = emptySet(),
    val isHighlightModeActive: Boolean = false,
    val editingAnnotationHighlightId: String? = null,
    val annotationDraft: String = "",
)
