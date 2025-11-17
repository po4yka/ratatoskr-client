package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.SearchFilters
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.model.SyncState
import com.po4yka.bitesizereader.util.error.AppError

/**
 * UI state for summary list screen
 */
data class SummaryListState(
    val summaries: List<Summary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null, // Kept for backward compatibility
    val appError: AppError? = null, // Enhanced error with retry info
    val isRefreshing: Boolean = false,
    val hasMore: Boolean = true,
    val syncState: SyncState = SyncState.Idle,
    val unreadCount: Int = 0,
    val filters: SearchFilters = SearchFilters(),
    val canRetry: Boolean = false, // Whether current error is retryable
    val retryAttempt: Int = 0, // Current retry attempt number
)
