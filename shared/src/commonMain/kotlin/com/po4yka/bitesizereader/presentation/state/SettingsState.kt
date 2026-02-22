package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.Session
import com.po4yka.bitesizereader.domain.model.SyncProgress
import com.po4yka.bitesizereader.domain.model.TelegramLinkStatus
import com.po4yka.bitesizereader.domain.model.UserPreferences
import com.po4yka.bitesizereader.domain.model.UserStats

data class SettingsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val linkStatus: TelegramLinkStatus? = null,
    val linkNonce: String? = null, // Nonce for the linking process
    val isDownloading: Boolean = false, // Reused for Import/Sync
    val downloadError: String? = null,
    val syncProgress: SyncProgress? = null, // Progress of current sync operation
    // User stats
    val userStats: UserStats? = null,
    val isLoadingStats: Boolean = false,
    // Account deletion
    val showDeleteConfirmation: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
    // Sessions
    val sessions: List<Session> = emptyList(),
    val isLoadingSessions: Boolean = false,
    val sessionsExpanded: Boolean = false,
    // Request history
    val requests: List<Request> = emptyList(),
    val isLoadingRequests: Boolean = false,
    val requestsExpanded: Boolean = false,
    // User preferences
    val userPreferences: UserPreferences? = null,
    val isLoadingPreferences: Boolean = false,
    val isSavingPreferences: Boolean = false,
    // Cache management
    val cacheSize: Long = 0L,
    val isClearingCache: Boolean = false,
)
