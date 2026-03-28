package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.DigestHistoryItem
import com.po4yka.bitesizereader.domain.model.DigestPreferences
import com.po4yka.bitesizereader.domain.model.DigestSubscriptionInfo

enum class DigestTab {
    CHANNELS,
    PREFERENCES,
    HISTORY,
}

data class DigestChannelsState(
    val subscriptionInfo: DigestSubscriptionInfo = DigestSubscriptionInfo(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val newChannelUsername: String = "",
    val isSubscribing: Boolean = false,
    val subscribeError: String? = null,
)

data class DigestPreferencesState(
    val preferences: DigestPreferences = DigestPreferences(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val editedDeliveryTime: String? = null,
    val editedTimezone: String? = null,
    val editedHoursLookback: String? = null,
    val editedMaxPosts: String? = null,
    val editedMinRelevance: String? = null,
)

data class DigestHistoryState(
    val items: List<DigestHistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = false,
)

data class DigestTriggerState(
    val isTriggering: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
)

data class DigestState(
    val selectedTab: DigestTab = DigestTab.CHANNELS,
    val channels: DigestChannelsState = DigestChannelsState(),
    val preferences: DigestPreferencesState = DigestPreferencesState(),
    val history: DigestHistoryState = DigestHistoryState(),
    val trigger: DigestTriggerState = DigestTriggerState(),
)
