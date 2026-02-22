package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Collection
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
)
