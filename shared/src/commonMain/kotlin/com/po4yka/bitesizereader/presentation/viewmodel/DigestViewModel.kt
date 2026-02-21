package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.GetDigestChannelsUseCase
import com.po4yka.bitesizereader.domain.usecase.GetDigestHistoryUseCase
import com.po4yka.bitesizereader.domain.usecase.GetDigestPreferencesUseCase
import com.po4yka.bitesizereader.domain.usecase.ManageDigestSubscriptionUseCase
import com.po4yka.bitesizereader.domain.usecase.TriggerDigestUseCase
import com.po4yka.bitesizereader.domain.usecase.UpdateDigestPreferencesUseCase
import com.po4yka.bitesizereader.presentation.state.DigestState
import com.po4yka.bitesizereader.presentation.state.DigestTab
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}
private const val DEFAULT_PAGE_SIZE = 20

@Factory
class DigestViewModel(
    private val getDigestChannelsUseCase: GetDigestChannelsUseCase,
    private val manageDigestSubscriptionUseCase: ManageDigestSubscriptionUseCase,
    private val getDigestPreferencesUseCase: GetDigestPreferencesUseCase,
    private val updateDigestPreferencesUseCase: UpdateDigestPreferencesUseCase,
    private val getDigestHistoryUseCase: GetDigestHistoryUseCase,
    private val triggerDigestUseCase: TriggerDigestUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(DigestState())
    val state = _state.asStateFlow()

    init {
        loadChannels()
    }

    fun selectTab(tab: DigestTab) {
        _state.update { it.copy(selectedTab = tab) }
        when (tab) {
            DigestTab.CHANNELS -> if (_state.value.subscriptionInfo.channels.isEmpty()) loadChannels()
            DigestTab.PREFERENCES -> loadPreferences()
            DigestTab.HISTORY -> if (_state.value.historyItems.isEmpty()) loadHistory()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun loadChannels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingChannels = true, channelsError = null) }
            try {
                val info = getDigestChannelsUseCase()
                _state.update { it.copy(subscriptionInfo = info, isLoadingChannels = false) }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to load digest channels" }
                _state.update {
                    it.copy(isLoadingChannels = false, channelsError = e.message ?: "Failed to load channels")
                }
            }
        }
    }

    fun onNewChannelUsernameChanged(username: String) {
        _state.update { it.copy(newChannelUsername = username, subscribeError = null) }
    }

    @Suppress("TooGenericExceptionCaught")
    fun subscribe() {
        val username = _state.value.newChannelUsername.trim()
        if (username.isBlank()) {
            _state.update { it.copy(subscribeError = "Channel username cannot be empty") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSubscribing = true, subscribeError = null) }
            try {
                val info = manageDigestSubscriptionUseCase.subscribe(username)
                _state.update {
                    it.copy(
                        subscriptionInfo = info,
                        isSubscribing = false,
                        newChannelUsername = "",
                    )
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to subscribe to channel: $username" }
                _state.update {
                    it.copy(isSubscribing = false, subscribeError = e.message ?: "Failed to subscribe")
                }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun unsubscribe(channelUsername: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSubscribing = true) }
            try {
                val info = manageDigestSubscriptionUseCase.unsubscribe(channelUsername)
                _state.update { it.copy(subscriptionInfo = info, isSubscribing = false) }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to unsubscribe from channel: $channelUsername" }
                _state.update {
                    it.copy(isSubscribing = false, channelsError = e.message ?: "Failed to unsubscribe")
                }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun loadPreferences() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingPreferences = true, preferencesError = null) }
            try {
                val prefs = getDigestPreferencesUseCase()
                _state.update {
                    it.copy(
                        preferences = prefs,
                        isLoadingPreferences = false,
                        editedDeliveryTime = null,
                        editedTimezone = null,
                        editedHoursLookback = null,
                        editedMaxPosts = null,
                        editedMinRelevance = null,
                    )
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to load digest preferences" }
                _state.update {
                    it.copy(isLoadingPreferences = false, preferencesError = e.message ?: "Failed to load preferences")
                }
            }
        }
    }

    fun onDeliveryTimeChanged(value: String) {
        _state.update { it.copy(editedDeliveryTime = value) }
    }

    fun onTimezoneChanged(value: String) {
        _state.update { it.copy(editedTimezone = value) }
    }

    fun onHoursLookbackChanged(value: String) {
        _state.update { it.copy(editedHoursLookback = value) }
    }

    fun onMaxPostsChanged(value: String) {
        _state.update { it.copy(editedMaxPosts = value) }
    }

    fun onMinRelevanceChanged(value: String) {
        _state.update { it.copy(editedMinRelevance = value) }
    }

    @Suppress("TooGenericExceptionCaught")
    fun savePreferences() {
        val current = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isSavingPreferences = true, savePreferencesError = null) }
            try {
                val prefs = updateDigestPreferencesUseCase(
                    deliveryTime = current.editedDeliveryTime,
                    timezone = current.editedTimezone,
                    hoursLookback = current.editedHoursLookback?.toIntOrNull(),
                    maxPostsPerDigest = current.editedMaxPosts?.toIntOrNull(),
                    minRelevanceScore = current.editedMinRelevance?.toDoubleOrNull(),
                )
                _state.update {
                    it.copy(
                        preferences = prefs,
                        isSavingPreferences = false,
                        editedDeliveryTime = null,
                        editedTimezone = null,
                        editedHoursLookback = null,
                        editedMaxPosts = null,
                        editedMinRelevance = null,
                    )
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to save digest preferences" }
                _state.update {
                    it.copy(
                        isSavingPreferences = false,
                        savePreferencesError = e.message ?: "Failed to save preferences",
                    )
                }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun loadHistory(loadMore: Boolean = false) {
        val page = if (loadMore) _state.value.historyPage + 1 else 1
        viewModelScope.launch {
            _state.update { it.copy(isLoadingHistory = true, historyError = null) }
            try {
                val items = getDigestHistoryUseCase(page, DEFAULT_PAGE_SIZE)
                _state.update { current ->
                    val allItems = if (loadMore) current.historyItems + items else items
                    current.copy(
                        historyItems = allItems,
                        isLoadingHistory = false,
                        historyPage = page,
                        hasMoreHistory = items.size >= DEFAULT_PAGE_SIZE,
                    )
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to load digest history" }
                _state.update {
                    it.copy(isLoadingHistory = false, historyError = e.message ?: "Failed to load history")
                }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun triggerDigest() {
        viewModelScope.launch {
            _state.update { it.copy(isTriggering = true, triggerSuccess = false, triggerError = null) }
            try {
                triggerDigestUseCase()
                _state.update { it.copy(isTriggering = false, triggerSuccess = true) }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to trigger digest" }
                _state.update {
                    it.copy(isTriggering = false, triggerError = e.message ?: "Failed to trigger digest")
                }
            }
        }
    }

    fun dismissTriggerSuccess() {
        _state.update { it.copy(triggerSuccess = false) }
    }
}
