package com.po4yka.bitesizereader.ui.screens

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
import com.po4yka.bitesizereader.domain.model.DigestChannel
import com.po4yka.bitesizereader.domain.model.DigestHistoryItem
import com.po4yka.bitesizereader.presentation.navigation.DigestComponent
import com.po4yka.bitesizereader.presentation.state.DigestChannelsState
import com.po4yka.bitesizereader.presentation.state.DigestHistoryState
import com.po4yka.bitesizereader.presentation.state.DigestPreferencesState
import com.po4yka.bitesizereader.presentation.state.DigestTab
import com.po4yka.bitesizereader.presentation.state.DigestTriggerState
import com.po4yka.bitesizereader.presentation.viewmodel.DigestViewModel
import com.po4yka.bitesizereader.ui.components.CarbonIconButton
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.digest_screen_add_channel
import bitesizereader.composeapp.generated.resources.digest_screen_channel_placeholder
import bitesizereader.composeapp.generated.resources.digest_screen_channel_username
import bitesizereader.composeapp.generated.resources.digest_screen_delivery_time
import bitesizereader.composeapp.generated.resources.digest_screen_delivery_time_placeholder
import bitesizereader.composeapp.generated.resources.digest_screen_history_channels_posts
import bitesizereader.composeapp.generated.resources.digest_screen_hours_lookback
import bitesizereader.composeapp.generated.resources.digest_screen_hours_lookback_placeholder
import bitesizereader.composeapp.generated.resources.digest_screen_load_more
import bitesizereader.composeapp.generated.resources.digest_screen_loading_more
import bitesizereader.composeapp.generated.resources.digest_screen_max_posts
import bitesizereader.composeapp.generated.resources.digest_screen_max_posts_placeholder
import bitesizereader.composeapp.generated.resources.digest_screen_min_relevance
import bitesizereader.composeapp.generated.resources.digest_screen_min_relevance_placeholder
import bitesizereader.composeapp.generated.resources.digest_screen_no_history
import bitesizereader.composeapp.generated.resources.digest_screen_saving_preferences
import bitesizereader.composeapp.generated.resources.digest_screen_save_preferences
import bitesizereader.composeapp.generated.resources.digest_screen_status_completed
import bitesizereader.composeapp.generated.resources.digest_screen_status_delivered
import bitesizereader.composeapp.generated.resources.digest_screen_status_failed
import bitesizereader.composeapp.generated.resources.digest_screen_status_pending
import bitesizereader.composeapp.generated.resources.digest_screen_subscribe
import bitesizereader.composeapp.generated.resources.digest_screen_subscribed
import bitesizereader.composeapp.generated.resources.digest_screen_subscriptions
import bitesizereader.composeapp.generated.resources.digest_screen_subscribing
import bitesizereader.composeapp.generated.resources.digest_screen_tab_channels
import bitesizereader.composeapp.generated.resources.digest_screen_tab_history
import bitesizereader.composeapp.generated.resources.digest_screen_tab_preferences
import bitesizereader.composeapp.generated.resources.digest_screen_timezone
import bitesizereader.composeapp.generated.resources.digest_screen_timezone_placeholder
import bitesizereader.composeapp.generated.resources.digest_screen_trigger_now
import bitesizereader.composeapp.generated.resources.digest_screen_trigger_success
import bitesizereader.composeapp.generated.resources.digest_screen_triggering
import bitesizereader.composeapp.generated.resources.digest_screen_unsubscribe
import bitesizereader.composeapp.generated.resources.settings_digest_channels
import bitesizereader.composeapp.generated.resources.submit_url_back
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming", "LongMethod")
@Composable
fun DigestScreen(
    component: DigestComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel: DigestViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

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
            onTabSelected = viewModel::selectTab,
        )

        // Tab content
        when (state.selectedTab) {
            DigestTab.CHANNELS ->
                ChannelsTab(
                    channels = state.channels,
                    trigger = state.trigger,
                    viewModel = viewModel,
                )
            DigestTab.PREFERENCES ->
                PreferencesTab(
                    preferences = state.preferences,
                    viewModel = viewModel,
                )
            DigestTab.HISTORY ->
                HistoryTab(
                    history = state.history,
                    viewModel = viewModel,
                )
        }
    }
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
        CarbonIconButton(
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
    viewModel: DigestViewModel,
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
                    onUnsubscribe = { viewModel.unsubscribe(channel.username) },
                )
            }
        }

        item {
            AddChannelForm(
                username = channels.newChannelUsername,
                onUsernameChanged = viewModel::onNewChannelUsernameChanged,
                isSubscribing = channels.isSubscribing,
                subscribeError = channels.subscribeError,
                onSubscribe = viewModel::subscribe,
            )
        }

        item {
            TriggerDigestSection(
                isTriggering = trigger.isTriggering,
                triggerSuccess = trigger.success,
                triggerError = trigger.error,
                onTrigger = viewModel::triggerDigest,
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
        CarbonIconButton(
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
    viewModel: DigestViewModel,
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
                viewModel = viewModel,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun DigestPreferencesForm(
    preferences: DigestPreferencesState,
    viewModel: DigestViewModel,
) {
    val inputState = if (preferences.isSaving) TextInputState.Disabled else TextInputState.Enabled

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        TextInput(
            label = stringResource(Res.string.digest_screen_delivery_time),
            value = preferences.editedDeliveryTime ?: preferences.preferences.deliveryTime,
            onValueChange = viewModel::onDeliveryTimeChanged,
            placeholderText = stringResource(Res.string.digest_screen_delivery_time_placeholder),
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = stringResource(Res.string.digest_screen_timezone),
            value = preferences.editedTimezone ?: preferences.preferences.timezone,
            onValueChange = viewModel::onTimezoneChanged,
            placeholderText = stringResource(Res.string.digest_screen_timezone_placeholder),
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = stringResource(Res.string.digest_screen_hours_lookback),
            value = preferences.editedHoursLookback ?: preferences.preferences.hoursLookback.toString(),
            onValueChange = viewModel::onHoursLookbackChanged,
            placeholderText = stringResource(Res.string.digest_screen_hours_lookback_placeholder),
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = stringResource(Res.string.digest_screen_max_posts),
            value = preferences.editedMaxPosts ?: preferences.preferences.maxPostsPerDigest.toString(),
            onValueChange = viewModel::onMaxPostsChanged,
            placeholderText = stringResource(Res.string.digest_screen_max_posts_placeholder),
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = stringResource(Res.string.digest_screen_min_relevance),
            value = preferences.editedMinRelevance ?: preferences.preferences.minRelevanceScore.toString(),
            onValueChange = viewModel::onMinRelevanceChanged,
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
            onClick = viewModel::savePreferences,
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
    viewModel: DigestViewModel,
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
                        onClick = { viewModel.loadHistory(loadMore = true) },
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
                text = stringResource(Res.string.digest_screen_history_channels_posts, item.channelCount, item.postCount),
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }
    }
}
