package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.model.SyncState

/**
 * UI state for summary list screen
 */
data class SummaryListState(
    val summaries: List<Summary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val hasMore: Boolean = true,
    val syncState: SyncState = SyncState.Idle,
    val unreadCount: Int = 0
)
