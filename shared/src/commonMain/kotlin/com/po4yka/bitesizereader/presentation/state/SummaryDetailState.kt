package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.AudioPlaybackState
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
    // Audio playback
    val audioState: AudioPlaybackState? = null,
    // Offline mode
    val isOffline: Boolean = false,
    // Export/share
    val isExporting: Boolean = false,
    val exportError: String? = null,
    // Nested sub-states
    val session: ReadingSessionState = ReadingSessionState(),
    val highlights: HighlightState = HighlightState(),
    val feedback: FeedbackState = FeedbackState(),
    val collection: CollectionDialogState = CollectionDialogState(),
)
