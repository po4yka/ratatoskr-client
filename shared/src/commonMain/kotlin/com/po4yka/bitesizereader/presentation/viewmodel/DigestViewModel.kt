package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.presentation.PresentationConstants
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
            DigestTab.CHANNELS -> if (_state.value.channels.subscriptionInfo.channels.isEmpty()) loadChannels()
            DigestTab.PREFERENCES -> loadPreferences()
            DigestTab.HISTORY -> if (_state.value.history.items.isEmpty()) loadHistory()
        }
    }

    // Channels

    @Suppress("TooGenericExceptionCaught")
    fun loadChannels() {
        viewModelScope.launch {
            _state.update { it.copy(channels = it.channels.copy(isLoading = true, error = null)) }
            try {
                val info = getDigestChannelsUseCase()
                _state.update {
                    it.copy(channels = it.channels.copy(subscriptionInfo = info, isLoading = false))
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to load digest channels" }
                _state.update {
                    it.copy(
                        channels = it.channels.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load channels",
                        ),
                    )
                }
            }
        }
    }

    fun onNewChannelUsernameChanged(username: String) {
        _state.update {
            it.copy(channels = it.channels.copy(newChannelUsername = username, subscribeError = null))
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun subscribe() {
        val username = _state.value.channels.newChannelUsername.trim()
        if (username.isBlank()) {
            _state.update {
                it.copy(channels = it.channels.copy(subscribeError = "Channel username cannot be empty"))
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(channels = it.channels.copy(isSubscribing = true, subscribeError = null))
            }
            try {
                val info = manageDigestSubscriptionUseCase.subscribe(username)
                _state.update {
                    it.copy(
                        channels = it.channels.copy(
                            subscriptionInfo = info,
                            isSubscribing = false,
                            newChannelUsername = "",
                        ),
                    )
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to subscribe to channel: $username" }
                _state.update {
                    it.copy(
                        channels = it.channels.copy(
                            isSubscribing = false,
                            subscribeError = e.message ?: "Failed to subscribe",
                        ),
                    )
                }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun unsubscribe(channelUsername: String) {
        viewModelScope.launch {
            _state.update { it.copy(channels = it.channels.copy(isSubscribing = true)) }
            try {
                val info = manageDigestSubscriptionUseCase.unsubscribe(channelUsername)
                _state.update {
                    it.copy(channels = it.channels.copy(subscriptionInfo = info, isSubscribing = false))
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to unsubscribe from channel: $channelUsername" }
                _state.update {
                    it.copy(
                        channels = it.channels.copy(
                            isSubscribing = false,
                            error = e.message ?: "Failed to unsubscribe",
                        ),
                    )
                }
            }
        }
    }

    // Preferences

    @Suppress("TooGenericExceptionCaught")
    fun loadPreferences() {
        viewModelScope.launch {
            _state.update {
                it.copy(preferences = it.preferences.copy(isLoading = true, error = null))
            }
            try {
                val prefs = getDigestPreferencesUseCase()
                _state.update {
                    it.copy(
                        preferences = it.preferences.copy(
                            preferences = prefs,
                            isLoading = false,
                            editedDeliveryTime = null,
                            editedTimezone = null,
                            editedHoursLookback = null,
                            editedMaxPosts = null,
                            editedMinRelevance = null,
                        ),
                    )
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to load digest preferences" }
                _state.update {
                    it.copy(
                        preferences = it.preferences.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load preferences",
                        ),
                    )
                }
            }
        }
    }

    fun onDeliveryTimeChanged(value: String) {
        _state.update { it.copy(preferences = it.preferences.copy(editedDeliveryTime = value)) }
    }

    fun onTimezoneChanged(value: String) {
        _state.update { it.copy(preferences = it.preferences.copy(editedTimezone = value)) }
    }

    fun onHoursLookbackChanged(value: String) {
        _state.update { it.copy(preferences = it.preferences.copy(editedHoursLookback = value)) }
    }

    fun onMaxPostsChanged(value: String) {
        _state.update { it.copy(preferences = it.preferences.copy(editedMaxPosts = value)) }
    }

    fun onMinRelevanceChanged(value: String) {
        _state.update { it.copy(preferences = it.preferences.copy(editedMinRelevance = value)) }
    }

    @Suppress("TooGenericExceptionCaught")
    fun savePreferences() {
        val currentPrefs = _state.value.preferences
        viewModelScope.launch {
            _state.update {
                it.copy(preferences = it.preferences.copy(isSaving = true, saveError = null))
            }
            try {
                val prefs = updateDigestPreferencesUseCase(
                    deliveryTime = currentPrefs.editedDeliveryTime,
                    timezone = currentPrefs.editedTimezone,
                    hoursLookback = currentPrefs.editedHoursLookback?.toIntOrNull(),
                    maxPostsPerDigest = currentPrefs.editedMaxPosts?.toIntOrNull(),
                    minRelevanceScore = currentPrefs.editedMinRelevance?.toDoubleOrNull(),
                )
                _state.update {
                    it.copy(
                        preferences = it.preferences.copy(
                            preferences = prefs,
                            isSaving = false,
                            editedDeliveryTime = null,
                            editedTimezone = null,
                            editedHoursLookback = null,
                            editedMaxPosts = null,
                            editedMinRelevance = null,
                        ),
                    )
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to save digest preferences" }
                _state.update {
                    it.copy(
                        preferences = it.preferences.copy(
                            isSaving = false,
                            saveError = e.message ?: "Failed to save preferences",
                        ),
                    )
                }
            }
        }
    }

    // History

    @Suppress("TooGenericExceptionCaught")
    fun loadHistory(loadMore: Boolean = false) {
        val page = if (loadMore) _state.value.history.page + 1 else 1
        viewModelScope.launch {
            _state.update {
                it.copy(history = it.history.copy(isLoading = true, error = null))
            }
            try {
                val items = getDigestHistoryUseCase(page, PresentationConstants.DEFAULT_PAGE_SIZE)
                _state.update { current ->
                    val allItems = if (loadMore) current.history.items + items else items
                    current.copy(
                        history = current.history.copy(
                            items = allItems,
                            isLoading = false,
                            page = page,
                            hasMore = items.size >= PresentationConstants.DEFAULT_PAGE_SIZE,
                        ),
                    )
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to load digest history" }
                _state.update {
                    it.copy(
                        history = it.history.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load history",
                        ),
                    )
                }
            }
        }
    }

    // Trigger

    @Suppress("TooGenericExceptionCaught")
    fun triggerDigest() {
        viewModelScope.launch {
            _state.update {
                it.copy(trigger = it.trigger.copy(isTriggering = true, success = false, error = null))
            }
            try {
                triggerDigestUseCase()
                _state.update {
                    it.copy(trigger = it.trigger.copy(isTriggering = false, success = true))
                }
            } catch (e: Exception) {
                logger.warn(e) { "Failed to trigger digest" }
                _state.update {
                    it.copy(
                        trigger = it.trigger.copy(
                            isTriggering = false,
                            error = e.message ?: "Failed to trigger digest",
                        ),
                    )
                }
            }
        }
    }

    fun dismissTriggerSuccess() {
        _state.update { it.copy(trigger = it.trigger.copy(success = false)) }
    }
}
