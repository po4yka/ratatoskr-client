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
import com.po4yka.bitesizereader.presentation.state.DigestState
import com.po4yka.bitesizereader.presentation.state.DigestTab
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
            DigestTab.CHANNELS -> ChannelsTab(state = state, viewModel = viewModel)
            DigestTab.PREFERENCES -> PreferencesTab(state = state, viewModel = viewModel)
            DigestTab.HISTORY -> HistoryTab(state = state, viewModel = viewModel)
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

@Suppress("FunctionNaming", "LongMethod")
@Composable
private fun ChannelsTab(
    state: DigestState,
    viewModel: DigestViewModel,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Slot usage
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "Subscriptions (${state.subscriptionInfo.usedSlots}/${state.subscriptionInfo.maxSlots})",
                    style = Carbon.typography.headingCompact01,
                    color = Carbon.theme.textPrimary,
                )
                if (state.subscriptionInfo.maxSlots > 0) {
                    ProgressBar(
                        value = state.subscriptionInfo.usedSlots.toFloat() /
                            state.subscriptionInfo.maxSlots.toFloat(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        // Channel list
        if (state.isLoadingChannels && state.subscriptionInfo.channels.isEmpty()) {
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
                items = state.subscriptionInfo.channels,
                key = { it.username },
            ) { channel ->
                DigestChannelRow(
                    channel = channel,
                    isLoading = state.isSubscribing,
                    onUnsubscribe = { viewModel.unsubscribe(channel.username) },
                )
            }
        }

        // Add channel form
        item {
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
                    value = state.newChannelUsername,
                    onValueChange = viewModel::onNewChannelUsernameChanged,
                    placeholderText = "@channel_name",
                    state = if (state.isSubscribing) TextInputState.Disabled else TextInputState.Enabled,
                    modifier = Modifier.fillMaxWidth(),
                )
                state.subscribeError?.let { error ->
                    Text(
                        text = error,
                        style = Carbon.typography.label01,
                        color = Carbon.theme.supportError,
                    )
                }
                Button(
                    label = if (state.isSubscribing) "Subscribing..." else "Subscribe",
                    onClick = viewModel::subscribe,
                    isEnabled = !state.isSubscribing && state.newChannelUsername.isNotBlank(),
                    buttonType = ButtonType.Primary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Trigger digest button
        item {
            Spacer(modifier = Modifier.height(Spacing.md))
            Button(
                label = if (state.isTriggering) "Triggering..." else "Trigger Digest Now",
                onClick = viewModel::triggerDigest,
                isEnabled = !state.isTriggering,
                buttonType = ButtonType.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )
            if (state.triggerSuccess) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Digest triggered successfully!",
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.supportSuccess,
                )
            }
            state.triggerError?.let { error ->
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = error,
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
            }
        }

        // Error
        state.channelsError?.let { error ->
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

@Suppress("FunctionNaming", "LongMethod")
@Composable
private fun PreferencesTab(
    state: DigestState,
    viewModel: DigestViewModel,
) {
    if (state.isLoadingPreferences) {
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
            TextInput(
                label = "Delivery Time",
                value = state.editedDeliveryTime ?: state.preferences.deliveryTime,
                onValueChange = viewModel::onDeliveryTimeChanged,
                placeholderText = "HH:MM",
                state = if (state.isSavingPreferences) TextInputState.Disabled else TextInputState.Enabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            TextInput(
                label = "Timezone",
                value = state.editedTimezone ?: state.preferences.timezone,
                onValueChange = viewModel::onTimezoneChanged,
                placeholderText = "UTC",
                state = if (state.isSavingPreferences) TextInputState.Disabled else TextInputState.Enabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            TextInput(
                label = "Hours Lookback",
                value = state.editedHoursLookback ?: state.preferences.hoursLookback.toString(),
                onValueChange = viewModel::onHoursLookbackChanged,
                placeholderText = "24",
                state = if (state.isSavingPreferences) TextInputState.Disabled else TextInputState.Enabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            TextInput(
                label = "Max Posts Per Digest",
                value = state.editedMaxPosts ?: state.preferences.maxPostsPerDigest.toString(),
                onValueChange = viewModel::onMaxPostsChanged,
                placeholderText = "10",
                state = if (state.isSavingPreferences) TextInputState.Disabled else TextInputState.Enabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            TextInput(
                label = "Min Relevance Score",
                value = state.editedMinRelevance ?: state.preferences.minRelevanceScore.toString(),
                onValueChange = viewModel::onMinRelevanceChanged,
                placeholderText = "0.5",
                state = if (state.isSavingPreferences) TextInputState.Disabled else TextInputState.Enabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        state.savePreferencesError?.let { error ->
            item {
                Text(
                    text = error,
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
            }
        }

        state.preferencesError?.let { error ->
            item {
                Text(
                    text = error,
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
            }
        }

        item {
            Button(
                label = if (state.isSavingPreferences) "Saving..." else "Save Preferences",
                onClick = viewModel::savePreferences,
                isEnabled = !state.isSavingPreferences,
                buttonType = ButtonType.Primary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun HistoryTab(
    state: DigestState,
    viewModel: DigestViewModel,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        if (state.isLoadingHistory && state.historyItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    SmallLoading()
                }
            }
        } else if (state.historyItems.isEmpty()) {
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
                items = state.historyItems,
                key = { it.id },
            ) { item ->
                DigestHistoryRow(item = item)
            }

            if (state.hasMoreHistory) {
                item {
                    Button(
                        label = if (state.isLoadingHistory) "Loading..." else "Load More",
                        onClick = { viewModel.loadHistory(loadMore = true) },
                        isEnabled = !state.isLoadingHistory,
                        buttonType = ButtonType.Ghost,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        state.historyError?.let { error ->
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
