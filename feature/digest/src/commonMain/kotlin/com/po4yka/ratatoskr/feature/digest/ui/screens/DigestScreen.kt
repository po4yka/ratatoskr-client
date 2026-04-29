package com.po4yka.ratatoskr.feature.digest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.progressbar.ProgressBar
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState
import com.po4yka.ratatoskr.domain.model.DigestChannel
import com.po4yka.ratatoskr.domain.model.DigestHistoryItem
import com.po4yka.ratatoskr.presentation.navigation.DigestComponent
import com.po4yka.ratatoskr.presentation.state.DigestChannelsState
import com.po4yka.ratatoskr.presentation.state.DigestHistoryState
import com.po4yka.ratatoskr.presentation.state.DigestPreferencesState
import com.po4yka.ratatoskr.presentation.state.DigestTab
import com.po4yka.ratatoskr.presentation.state.DigestTriggerState
import com.po4yka.ratatoskr.core.ui.components.AppIconButton
import com.po4yka.ratatoskr.core.ui.icons.CarbonIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.digest_screen_add_channel
import ratatoskr.core.ui.generated.resources.digest_screen_channel_placeholder
import ratatoskr.core.ui.generated.resources.digest_screen_channel_username
import ratatoskr.core.ui.generated.resources.digest_screen_delivery_time
import ratatoskr.core.ui.generated.resources.digest_screen_delivery_time_placeholder
import ratatoskr.core.ui.generated.resources.digest_screen_history_channels_posts
import ratatoskr.core.ui.generated.resources.digest_screen_hours_lookback
import ratatoskr.core.ui.generated.resources.digest_screen_hours_lookback_placeholder
import ratatoskr.core.ui.generated.resources.digest_screen_load_more
import ratatoskr.core.ui.generated.resources.digest_screen_loading_more
import ratatoskr.core.ui.generated.resources.digest_screen_max_posts
import ratatoskr.core.ui.generated.resources.digest_screen_max_posts_placeholder
import ratatoskr.core.ui.generated.resources.digest_screen_min_relevance
import ratatoskr.core.ui.generated.resources.digest_screen_min_relevance_placeholder
import ratatoskr.core.ui.generated.resources.digest_screen_no_history
import ratatoskr.core.ui.generated.resources.digest_screen_saving_preferences
import ratatoskr.core.ui.generated.resources.digest_screen_save_preferences
import ratatoskr.core.ui.generated.resources.digest_screen_status_completed
import ratatoskr.core.ui.generated.resources.digest_screen_status_delivered
import ratatoskr.core.ui.generated.resources.digest_screen_status_failed
import ratatoskr.core.ui.generated.resources.digest_screen_status_pending
import ratatoskr.core.ui.generated.resources.digest_screen_subscribe
import ratatoskr.core.ui.generated.resources.digest_screen_subscribed
import ratatoskr.core.ui.generated.resources.digest_screen_subscriptions
import ratatoskr.core.ui.generated.resources.digest_screen_subscribing
import ratatoskr.core.ui.generated.resources.digest_screen_tab_channels
import ratatoskr.core.ui.generated.resources.digest_screen_tab_history
import ratatoskr.core.ui.generated.resources.digest_screen_tab_preferences
import ratatoskr.core.ui.generated.resources.digest_screen_timezone
import ratatoskr.core.ui.generated.resources.digest_screen_timezone_placeholder
import ratatoskr.core.ui.generated.resources.digest_screen_trigger_now
import ratatoskr.core.ui.generated.resources.digest_screen_trigger_success
import ratatoskr.core.ui.generated.resources.digest_screen_triggering
import ratatoskr.core.ui.generated.resources.digest_screen_unsubscribe
import ratatoskr.core.ui.generated.resources.settings_digest_channels
import ratatoskr.core.ui.generated.resources.submit_url_back
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming", "LongMethod")
@Composable
fun DigestScreen(
    component: DigestComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel = component.viewModel
    val state by viewModel.state.collectAsState()
    val actions =
        object : DigestActions {
            override fun selectTab(tab: DigestTab) = viewModel.selectTab(tab)

            override fun unsubscribe(username: String) = viewModel.unsubscribe(username)

            override fun onNewChannelUsernameChanged(username: String) = viewModel.onNewChannelUsernameChanged(username)

            override fun subscribe() = viewModel.subscribe()

            override fun triggerDigest() = viewModel.triggerDigest()

            override fun onDeliveryTimeChanged(value: String) = viewModel.onDeliveryTimeChanged(value)

            override fun onTimezoneChanged(value: String) = viewModel.onTimezoneChanged(value)

            override fun onHoursLookbackChanged(value: String) = viewModel.onHoursLookbackChanged(value)

            override fun onMaxPostsChanged(value: String) = viewModel.onMaxPostsChanged(value)

            override fun onMinRelevanceChanged(value: String) = viewModel.onMinRelevanceChanged(value)

            override fun savePreferences() = viewModel.savePreferences()

            override fun loadHistory(loadMore: Boolean) = viewModel.loadHistory(loadMore)
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        // Header
        DigestHeader(onBackClick = component::onBackClicked)

        // Tab bar
        DigestTabBar(
            selectedTab = state.selectedTab,
            onTabSelected = actions::selectTab,
        )

        // Tab content
        when (state.selectedTab) {
            DigestTab.CHANNELS ->
                ChannelsTab(
                    channels = state.channels,
                    trigger = state.trigger,
                    actions = actions,
                )
            DigestTab.PREFERENCES ->
                PreferencesTab(
                    preferences = state.preferences,
                    actions = actions,
                )
            DigestTab.HISTORY ->
                HistoryTab(
                    history = state.history,
                    actions = actions,
                )
        }
    }
}

private interface DigestActions {
    fun selectTab(tab: DigestTab)

    fun unsubscribe(username: String)

    fun onNewChannelUsernameChanged(username: String)

    fun subscribe()

    fun triggerDigest()

    fun onDeliveryTimeChanged(value: String)

    fun onTimezoneChanged(value: String)

    fun onHoursLookbackChanged(value: String)

    fun onMaxPostsChanged(value: String)

    fun onMinRelevanceChanged(value: String)

    fun savePreferences()

    fun loadHistory(loadMore: Boolean)
}

@Suppress("FunctionNaming")
@Composable
private fun DigestHeader(onBackClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(Dimensions.detailHeaderHeight)
                .background(Carbon.theme.layer01)
                .padding(horizontal = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIconButton(
            imageVector = CarbonIcons.ArrowLeft,
            contentDescription = stringResource(Res.string.submit_url_back),
            onClick = onBackClick,
            iconSize = IconSizes.md,
        )
        Text(
            text = stringResource(Res.string.settings_digest_channels),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun DigestTabBar(
    selectedTab: DigestTab,
    onTabSelected: (DigestTab) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01),
    ) {
        DigestTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val label =
                when (tab) {
                    DigestTab.CHANNELS -> stringResource(Res.string.digest_screen_tab_channels)
                    DigestTab.PREFERENCES -> stringResource(Res.string.digest_screen_tab_preferences)
                    DigestTab.HISTORY -> stringResource(Res.string.digest_screen_tab_history)
                }
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .semantics { role = Role.Tab }
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = Spacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = label,
                    style = Carbon.typography.bodyCompact01,
                    color = if (isSelected) Carbon.theme.textPrimary else Carbon.theme.textSecondary,
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier =
                            Modifier
                                .width(48.dp)
                                .height(2.dp)
                                .background(Carbon.theme.borderInteractive),
                    )
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ChannelsTab(
    channels: DigestChannelsState,
    trigger: DigestTriggerState,
    actions: DigestActions,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            ChannelSlotUsage(
                usedSlots = channels.subscriptionInfo.usedSlots,
                maxSlots = channels.subscriptionInfo.maxSlots,
            )
        }

        if (channels.isLoading && channels.subscriptionInfo.channels.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    SmallLoading()
                }
            }
        } else {
            items(
                items = channels.subscriptionInfo.channels,
                key = { it.username },
            ) { channel ->
                DigestChannelRow(
                    channel = channel,
                    isLoading = channels.isSubscribing,
                    onUnsubscribe = { actions.unsubscribe(channel.username) },
                )
            }
        }

        item {
            AddChannelForm(
                username = channels.newChannelUsername,
                onUsernameChanged = actions::onNewChannelUsernameChanged,
                isSubscribing = channels.isSubscribing,
                subscribeError = channels.subscribeError,
                onSubscribe = actions::subscribe,
            )
        }

        item {
            TriggerDigestSection(
                isTriggering = trigger.isTriggering,
                triggerSuccess = trigger.success,
                triggerError = trigger.error,
                onTrigger = actions::triggerDigest,
            )
        }

        channels.error?.let { error ->
            item {
                Text(
                    text = error,
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ChannelSlotUsage(
    usedSlots: Int,
    maxSlots: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = stringResource(Res.string.digest_screen_subscriptions, usedSlots, maxSlots),
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        if (maxSlots > 0) {
            ProgressBar(
                value = usedSlots.toFloat() / maxSlots.toFloat(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun AddChannelForm(
    username: String,
    onUsernameChanged: (String) -> Unit,
    isSubscribing: Boolean,
    subscribeError: String?,
    onSubscribe: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01)
                .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.digest_screen_add_channel),
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        TextInput(
            label = stringResource(Res.string.digest_screen_channel_username),
            value = username,
            onValueChange = onUsernameChanged,
            placeholderText = stringResource(Res.string.digest_screen_channel_placeholder),
            state = if (isSubscribing) TextInputState.Disabled else TextInputState.Enabled,
            modifier = Modifier.fillMaxWidth(),
        )
        if (subscribeError != null) {
            Text(
                text = subscribeError,
                style = Carbon.typography.label01,
                color = Carbon.theme.supportError,
            )
        }
        Button(
            label =
                if (isSubscribing) {
                    stringResource(Res.string.digest_screen_subscribing)
                } else {
                    stringResource(Res.string.digest_screen_subscribe)
                },
            onClick = onSubscribe,
            isEnabled = !isSubscribing && username.isNotBlank(),
            buttonType = ButtonType.Primary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun TriggerDigestSection(
    isTriggering: Boolean,
    triggerSuccess: Boolean,
    triggerError: String?,
    onTrigger: () -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.height(Spacing.md))
        Button(
            label =
                if (isTriggering) {
                    stringResource(Res.string.digest_screen_triggering)
                } else {
                    stringResource(Res.string.digest_screen_trigger_now)
                },
            onClick = onTrigger,
            isEnabled = !isTriggering,
            buttonType = ButtonType.Secondary,
            modifier = Modifier.fillMaxWidth(),
        )
        if (triggerSuccess) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = stringResource(Res.string.digest_screen_trigger_success),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.supportSuccess,
            )
        }
        if (triggerError != null) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = triggerError,
                style = Carbon.typography.label01,
                color = Carbon.theme.supportError,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun DigestChannelRow(
    channel: DigestChannel,
    isLoading: Boolean,
    onUnsubscribe: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01)
                .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = channel.username,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
            )
            channel.subscribedAt?.let {
                Text(
                    text = stringResource(Res.string.digest_screen_subscribed, it),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }
        }
        AppIconButton(
            imageVector = CarbonIcons.Close,
            contentDescription = stringResource(Res.string.digest_screen_unsubscribe),
            onClick = onUnsubscribe,
            enabled = !isLoading,
            tint = Carbon.theme.supportError,
            iconSize = IconSizes.sm,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun PreferencesTab(
    preferences: DigestPreferencesState,
    actions: DigestActions,
) {
    if (preferences.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            SmallLoading()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            DigestPreferencesForm(
                preferences = preferences,
                actions = actions,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun DigestPreferencesForm(
    preferences: DigestPreferencesState,
    actions: DigestActions,
) {
    val inputState = if (preferences.isSaving) TextInputState.Disabled else TextInputState.Enabled

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        TextInput(
            label = stringResource(Res.string.digest_screen_delivery_time),
            value = preferences.editedDeliveryTime ?: preferences.preferences.deliveryTime,
            onValueChange = actions::onDeliveryTimeChanged,
            placeholderText = stringResource(Res.string.digest_screen_delivery_time_placeholder),
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = stringResource(Res.string.digest_screen_timezone),
            value = preferences.editedTimezone ?: preferences.preferences.timezone,
            onValueChange = actions::onTimezoneChanged,
            placeholderText = stringResource(Res.string.digest_screen_timezone_placeholder),
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = stringResource(Res.string.digest_screen_hours_lookback),
            value = preferences.editedHoursLookback ?: preferences.preferences.hoursLookback.toString(),
            onValueChange = actions::onHoursLookbackChanged,
            placeholderText = stringResource(Res.string.digest_screen_hours_lookback_placeholder),
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = stringResource(Res.string.digest_screen_max_posts),
            value = preferences.editedMaxPosts ?: preferences.preferences.maxPostsPerDigest.toString(),
            onValueChange = actions::onMaxPostsChanged,
            placeholderText = stringResource(Res.string.digest_screen_max_posts_placeholder),
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = stringResource(Res.string.digest_screen_min_relevance),
            value = preferences.editedMinRelevance ?: preferences.preferences.minRelevanceScore.toString(),
            onValueChange = actions::onMinRelevanceChanged,
            placeholderText = stringResource(Res.string.digest_screen_min_relevance_placeholder),
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        preferences.saveError?.let { error ->
            Text(
                text = error,
                style = Carbon.typography.label01,
                color = Carbon.theme.supportError,
            )
        }

        preferences.error?.let { error ->
            Text(
                text = error,
                style = Carbon.typography.label01,
                color = Carbon.theme.supportError,
            )
        }

        Button(
            label =
                if (preferences.isSaving) {
                    stringResource(Res.string.digest_screen_saving_preferences)
                } else {
                    stringResource(Res.string.digest_screen_save_preferences)
                },
            onClick = actions::savePreferences,
            isEnabled = !preferences.isSaving,
            buttonType = ButtonType.Primary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun HistoryTab(
    history: DigestHistoryState,
    actions: DigestActions,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        if (history.isLoading && history.items.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    SmallLoading()
                }
            }
        } else if (history.items.isEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.digest_screen_no_history),
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                    modifier = Modifier.padding(Spacing.md),
                )
            }
        } else {
            items(
                items = history.items,
                key = { it.id },
            ) { item ->
                DigestHistoryRow(item = item)
            }

            if (history.hasMore) {
                item {
                    Button(
                        label =
                            if (history.isLoading) {
                                stringResource(Res.string.digest_screen_loading_more)
                            } else {
                                stringResource(Res.string.digest_screen_load_more)
                            },
                        onClick = { actions.loadHistory(loadMore = true) },
                        isEnabled = !history.isLoading,
                        buttonType = ButtonType.Ghost,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        history.error?.let { error ->
            item {
                Text(
                    text = error,
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun DigestHistoryRow(item: DigestHistoryItem) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01)
                .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val statusIcon =
            when (item.status) {
                "delivered", "completed" -> CarbonIcons.CheckmarkFilled
                "failed" -> CarbonIcons.Close
                else -> CarbonIcons.CircleOutline
            }
        val statusColor =
            when (item.status) {
                "delivered", "completed" -> Carbon.theme.supportSuccess
                "failed" -> Carbon.theme.supportError
                else -> Carbon.theme.iconSecondary
            }

        Icon(
            imageVector = statusIcon,
            contentDescription =
                when (item.status) {
                    "delivered" -> stringResource(Res.string.digest_screen_status_delivered)
                    "completed" -> stringResource(Res.string.digest_screen_status_completed)
                    "failed" -> stringResource(Res.string.digest_screen_status_failed)
                    else -> stringResource(Res.string.digest_screen_status_pending)
                },
            tint = statusColor,
            modifier = Modifier.size(IconSizes.sm),
        )

        Spacer(modifier = Modifier.width(Spacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.deliveredAt,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
            )
            Text(
                text =
                    stringResource(
                        Res.string.digest_screen_history_channels_posts,
                        item.channelCount,
                        item.postCount,
                    ),
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }
    }
}
