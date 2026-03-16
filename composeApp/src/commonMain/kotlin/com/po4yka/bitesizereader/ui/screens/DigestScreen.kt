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
import androidx.compose.material3.IconButton
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
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing

@Suppress("FunctionNaming", "LongMethod")
@Composable
fun DigestScreen(
    component: DigestComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel: DigestViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
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
            DigestTab.CHANNELS -> ChannelsTab(
                channels = state.channels,
                trigger = state.trigger,
                viewModel = viewModel,
            )
            DigestTab.PREFERENCES -> PreferencesTab(
                preferences = state.preferences,
                viewModel = viewModel,
            )
            DigestTab.HISTORY -> HistoryTab(
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
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.detailHeaderHeight)
            .background(Carbon.theme.layer01)
            .padding(horizontal = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = CarbonIcons.ArrowLeft,
                contentDescription = "Back",
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(IconSizes.md),
            )
        }
        Text(
            text = "Digest Channels",
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
        modifier = Modifier
            .fillMaxWidth()
            .background(Carbon.theme.layer01),
    ) {
        DigestTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val label = when (tab) {
                DigestTab.CHANNELS -> "Channels"
                DigestTab.PREFERENCES -> "Preferences"
                DigestTab.HISTORY -> "History"
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.Tab }
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = label,
                        style = Carbon.typography.bodyCompact01,
                        color = if (isSelected) Carbon.theme.textPrimary else Carbon.theme.textSecondary,
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(2.dp)
                                .background(Carbon.theme.borderInteractive),
                        )
                    }
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
private fun ChannelSlotUsage(usedSlots: Int, maxSlots: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = "Subscriptions ($usedSlots/$maxSlots)",
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
        modifier = Modifier
            .fillMaxWidth()
            .background(Carbon.theme.layer01)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = "Add Channel",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        TextInput(
            label = "Channel Username",
            value = username,
            onValueChange = onUsernameChanged,
            placeholderText = "@channel_name",
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
            label = if (isSubscribing) "Subscribing..." else "Subscribe",
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
            label = if (isTriggering) "Triggering..." else "Trigger Digest Now",
            onClick = onTrigger,
            isEnabled = !isTriggering,
            buttonType = ButtonType.Secondary,
            modifier = Modifier.fillMaxWidth(),
        )
        if (triggerSuccess) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Digest triggered successfully!",
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
        modifier = Modifier
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
                    text = "Subscribed: $it",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }
        }
        IconButton(
            onClick = onUnsubscribe,
            enabled = !isLoading,
        ) {
            Icon(
                imageVector = CarbonIcons.Close,
                contentDescription = "Unsubscribe",
                tint = Carbon.theme.supportError,
                modifier = Modifier.size(IconSizes.sm),
            )
        }
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
            label = "Delivery Time",
            value = preferences.editedDeliveryTime ?: preferences.preferences.deliveryTime,
            onValueChange = viewModel::onDeliveryTimeChanged,
            placeholderText = "HH:MM",
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = "Timezone",
            value = preferences.editedTimezone ?: preferences.preferences.timezone,
            onValueChange = viewModel::onTimezoneChanged,
            placeholderText = "UTC",
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = "Hours Lookback",
            value = preferences.editedHoursLookback ?: preferences.preferences.hoursLookback.toString(),
            onValueChange = viewModel::onHoursLookbackChanged,
            placeholderText = "24",
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = "Max Posts Per Digest",
            value = preferences.editedMaxPosts ?: preferences.preferences.maxPostsPerDigest.toString(),
            onValueChange = viewModel::onMaxPostsChanged,
            placeholderText = "10",
            state = inputState,
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = "Min Relevance Score",
            value = preferences.editedMinRelevance ?: preferences.preferences.minRelevanceScore.toString(),
            onValueChange = viewModel::onMinRelevanceChanged,
            placeholderText = "0.5",
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
            label = if (preferences.isSaving) "Saving..." else "Save Preferences",
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
                    text = "No digest history yet",
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
                        label = if (history.isLoading) "Loading..." else "Load More",
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
        modifier = Modifier
            .fillMaxWidth()
            .background(Carbon.theme.layer01)
            .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val statusIcon = when (item.status) {
            "delivered", "completed" -> CarbonIcons.CheckmarkFilled
            "failed" -> CarbonIcons.Close
            else -> CarbonIcons.CircleOutline
        }
        val statusColor = when (item.status) {
            "delivered", "completed" -> Carbon.theme.supportSuccess
            "failed" -> Carbon.theme.supportError
            else -> Carbon.theme.iconSecondary
        }

        Icon(
            imageVector = statusIcon,
            contentDescription = item.status,
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
                text = "${item.channelCount} channels, ${item.postCount} posts",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }
    }
}
