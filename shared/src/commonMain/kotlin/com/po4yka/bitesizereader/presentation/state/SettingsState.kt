package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.Session
import com.po4yka.bitesizereader.domain.model.SyncProgress
import com.po4yka.bitesizereader.domain.model.TelegramLinkStatus
import com.po4yka.bitesizereader.domain.model.UserPreferences
import com.po4yka.bitesizereader.domain.model.UserStats

data class TelegramLinkState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val linkStatus: TelegramLinkStatus? = null,
    val linkNonce: String? = null,
)

data class SyncSettingsState(
    val isDownloading: Boolean = false,
    val downloadError: String? = null,
    val syncProgress: SyncProgress? = null,
    val requests: List<Request> = emptyList(),
    val isLoadingRequests: Boolean = false,
    val requestsExpanded: Boolean = false,
    val cacheSize: Long = 0L,
    val isClearingCache: Boolean = false,
)

data class AccountSettingsState(
    val userStats: UserStats? = null,
    val isLoadingStats: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
    val sessions: List<Session> = emptyList(),
    val isLoadingSessions: Boolean = false,
    val sessionsExpanded: Boolean = false,
    val userPreferences: UserPreferences? = null,
    val isLoadingPreferences: Boolean = false,
    val isSavingPreferences: Boolean = false,
)

data class SettingsState(
    val telegram: TelegramLinkState = TelegramLinkState(),
    val sync: SyncSettingsState = SyncSettingsState(),
    val account: AccountSettingsState = AccountSettingsState(),
)
