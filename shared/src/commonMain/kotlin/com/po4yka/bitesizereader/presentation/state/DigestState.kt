package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.DigestHistoryItem
import com.po4yka.bitesizereader.domain.model.DigestPreferences
import com.po4yka.bitesizereader.domain.model.DigestSubscriptionInfo

enum class DigestTab {
    CHANNELS,
    PREFERENCES,
    HISTORY,
}

data class DigestState(
    val selectedTab: DigestTab = DigestTab.CHANNELS,
    // Channels
    val subscriptionInfo: DigestSubscriptionInfo = DigestSubscriptionInfo(),
    val isLoadingChannels: Boolean = false,
    val channelsError: String? = null,
    val newChannelUsername: String = "",
    val isSubscribing: Boolean = false,
    val subscribeError: String? = null,
    // Preferences
    val preferences: DigestPreferences = DigestPreferences(),
    val isLoadingPreferences: Boolean = false,
    val preferencesError: String? = null,
    val isSavingPreferences: Boolean = false,
    val savePreferencesError: String? = null,
    // Edited preference fields (null = not edited)
    val editedDeliveryTime: String? = null,
    val editedTimezone: String? = null,
    val editedHoursLookback: String? = null,
    val editedMaxPosts: String? = null,
    val editedMinRelevance: String? = null,
    // History
    val historyItems: List<DigestHistoryItem> = emptyList(),
    val isLoadingHistory: Boolean = false,
    val historyError: String? = null,
    val historyPage: Int = 1,
    val hasMoreHistory: Boolean = false,
    // Trigger
    val isTriggering: Boolean = false,
    val triggerSuccess: Boolean = false,
    val triggerError: String? = null,
)
