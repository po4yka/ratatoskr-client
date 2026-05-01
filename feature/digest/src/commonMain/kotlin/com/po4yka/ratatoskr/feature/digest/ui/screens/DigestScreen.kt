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
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import com.po4yka.ratatoskr.core.ui.components.frost.BracketField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton
import com.po4yka.ratatoskr.core.ui.components.AppSmallSpinner
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.DigestChannel
import com.po4yka.ratatoskr.domain.model.DigestHistoryItem
import com.po4yka.ratatoskr.presentation.navigation.DigestComponent
import com.po4yka.ratatoskr.presentation.state.DigestChannelsState
import com.po4yka.ratatoskr.presentation.state.DigestHistoryState
import com.po4yka.ratatoskr.presentation.state.DigestPreferencesState
import com.po4yka.ratatoskr.presentation.state.DigestTab
import com.po4yka.ratatoskr.presentation.state.DigestTriggerState
import com.po4yka.ratatoskr.core.ui.components.AppIconButton
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.digest_screen_add_channel
import ratatoskr.core.ui.generated.resources.digest_screen_channel_username
import ratatoskr.core.ui.generated.resources.digest_screen_delivery_time
import ratatoskr.core.ui.generated.resources.digest_screen_history_channels_posts
import ratatoskr.core.ui.generated.resources.digest_screen_hours_lookback
import ratatoskr.core.ui.generated.resources.digest_screen_load_more
import ratatoskr.core.ui.generated.resources.digest_screen_loading_more
import ratatoskr.core.ui.generated.resources.digest_screen_max_posts
import ratatoskr.core.ui.generated.resources.digest_screen_min_relevance
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
                .background(AppTheme.frostColors.page),
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
                .height(56.dp)
                .background(AppTheme.frostColors.page)
                .padding(horizontal = AppTheme.spacing.cell),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIconButton(
            imageVector = AppIcons.ArrowLeft,
            contentDescription = stringResource(Res.string.submit_url_back),
            onClick = onBackClick,
            iconSize = IconSizes.md,
        )
        FrostText(
            text = stringResource(Res.string.settings_digest_channels),
            style = AppTheme.frostType.monoEmph,
            color = AppTheme.frostColors.ink,
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
                .background(AppTheme.frostColors.page),
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
                        .padding(vertical = AppTheme.spacing.cell),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FrostText(
                    text = label,
                    style = AppTheme.frostType.monoBody,
                    color =
                        if (isSelected) {
                            AppTheme.frostColors.ink
                        } else {
                            AppTheme.frostColors.ink.copy(
                                alpha = AppTheme.alpha.secondary,
                            )
                        },
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier =
                            Modifier
                                .width(48.dp)
                                .height(2.dp)
                                .background(AppTheme.frostColors.ink),
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
        modifier = Modifier.fillMaxSize().padding(AppTheme.spacing.line),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.line),
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
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    AppSmallSpinner()
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
                FrostText(
                    text = error,
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.spark,
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
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell)) {
        FrostText(
            text = stringResource(Res.string.digest_screen_subscriptions, usedSlots, maxSlots),
            style = AppTheme.frostType.monoEmph,
            color = AppTheme.frostColors.ink,
        )
        if (maxSlots > 0) {
            // note: Frost two-color rule — deterministic progress bar in ink
            val fraction = (usedSlots.toFloat() / maxSlots.toFloat()).coerceIn(0f, 1f)
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(AppTheme.frostColors.ink.copy(alpha = AppTheme.border.separatorAlpha)),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(fraction)
                            .height(4.dp)
                            .background(AppTheme.frostColors.ink),
                )
            }
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
                .background(AppTheme.frostColors.page)
                .padding(AppTheme.spacing.line),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
    ) {
        FrostText(
            text = stringResource(Res.string.digest_screen_add_channel),
            style = AppTheme.frostType.monoEmph,
            color = AppTheme.frostColors.ink,
        )
        BracketField(
            value = username,
            onValueChange = onUsernameChanged,
            label = stringResource(Res.string.digest_screen_channel_username),
            enabled = !isSubscribing,
            modifier = Modifier.fillMaxWidth(),
        )
        if (subscribeError != null) {
            FrostText(
                text = subscribeError,
                style = AppTheme.frostType.monoXs,
                color = AppTheme.frostColors.spark,
            )
        }
        BracketButton(
            label =
                if (isSubscribing) {
                    stringResource(Res.string.digest_screen_subscribing)
                } else {
                    stringResource(Res.string.digest_screen_subscribe)
                },
            onClick = onSubscribe,
            enabled = !isSubscribing && username.isNotBlank(),
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
        Spacer(modifier = Modifier.height(AppTheme.spacing.line))
        BracketButton(
            label =
                if (isTriggering) {
                    stringResource(Res.string.digest_screen_triggering)
                } else {
                    stringResource(Res.string.digest_screen_trigger_now)
                },
            onClick = onTrigger,
            enabled = !isTriggering,
            modifier = Modifier.fillMaxWidth(),
        )
        if (triggerSuccess) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.cell))
            FrostText(
                text = stringResource(Res.string.digest_screen_trigger_success),
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.active),
            )
        }
        if (triggerError != null) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.cell))
            FrostText(
                text = triggerError,
                style = AppTheme.frostType.monoXs,
                color = AppTheme.frostColors.spark,
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
                .background(AppTheme.frostColors.page)
                .padding(AppTheme.spacing.cell),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            FrostText(
                text = channel.username,
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink,
            )
            channel.subscribedAt?.let {
                FrostText(
                    text = stringResource(Res.string.digest_screen_subscribed, it),
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                )
            }
        }
        AppIconButton(
            imageVector = AppIcons.Close,
            contentDescription = stringResource(Res.string.digest_screen_unsubscribe),
            onClick = onUnsubscribe,
            enabled = !isLoading,
            tint = AppTheme.frostColors.spark,
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
            AppSmallSpinner()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(AppTheme.spacing.line),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.line),
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
    val enabled = !preferences.isSaving

    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.line)) {
        BracketField(
            value = preferences.editedDeliveryTime ?: preferences.preferences.deliveryTime,
            onValueChange = actions::onDeliveryTimeChanged,
            label = stringResource(Res.string.digest_screen_delivery_time),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )

        BracketField(
            value = preferences.editedTimezone ?: preferences.preferences.timezone,
            onValueChange = actions::onTimezoneChanged,
            label = stringResource(Res.string.digest_screen_timezone),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )

        BracketField(
            value = preferences.editedHoursLookback ?: preferences.preferences.hoursLookback.toString(),
            onValueChange = actions::onHoursLookbackChanged,
            label = stringResource(Res.string.digest_screen_hours_lookback),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )

        BracketField(
            value = preferences.editedMaxPosts ?: preferences.preferences.maxPostsPerDigest.toString(),
            onValueChange = actions::onMaxPostsChanged,
            label = stringResource(Res.string.digest_screen_max_posts),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )

        BracketField(
            value = preferences.editedMinRelevance ?: preferences.preferences.minRelevanceScore.toString(),
            onValueChange = actions::onMinRelevanceChanged,
            label = stringResource(Res.string.digest_screen_min_relevance),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )

        preferences.saveError?.let { error ->
            FrostText(
                text = error,
                style = AppTheme.frostType.monoXs,
                color = AppTheme.frostColors.spark,
            )
        }

        preferences.error?.let { error ->
            FrostText(
                text = error,
                style = AppTheme.frostType.monoXs,
                color = AppTheme.frostColors.spark,
            )
        }

        BracketButton(
            label =
                if (preferences.isSaving) {
                    stringResource(Res.string.digest_screen_saving_preferences)
                } else {
                    stringResource(Res.string.digest_screen_save_preferences)
                },
            onClick = actions::savePreferences,
            enabled = !preferences.isSaving,
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
        modifier = Modifier.fillMaxSize().padding(AppTheme.spacing.line),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
    ) {
        if (history.isLoading && history.items.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    AppSmallSpinner()
                }
            }
        } else if (history.items.isEmpty()) {
            item {
                FrostText(
                    text = stringResource(Res.string.digest_screen_no_history),
                    style = AppTheme.frostType.monoBody,
                    color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                    modifier = Modifier.padding(AppTheme.spacing.line),
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
                    BracketButton(
                        label =
                            if (history.isLoading) {
                                stringResource(Res.string.digest_screen_loading_more)
                            } else {
                                stringResource(Res.string.digest_screen_load_more)
                            },
                        onClick = { actions.loadHistory(loadMore = true) },
                        enabled = !history.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        history.error?.let { error ->
            item {
                FrostText(
                    text = error,
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.spark,
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
                .background(AppTheme.frostColors.page)
                .padding(AppTheme.spacing.cell),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val statusIcon =
            when (item.status) {
                "delivered", "completed" -> AppIcons.CheckmarkFilled
                "failed" -> AppIcons.Close
                else -> AppIcons.CircleOutline
            }
        val statusColor =
            when (item.status) {
                "delivered", "completed" -> AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.active)
                "failed" -> AppTheme.frostColors.spark
                else -> AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary)
            }

        FrostIcon(
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

        Spacer(modifier = Modifier.width(AppTheme.spacing.cell))

        Column(modifier = Modifier.weight(1f)) {
            FrostText(
                text = item.deliveredAt,
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink,
            )
            FrostText(
                text =
                    stringResource(
                        Res.string.digest_screen_history_channels_posts,
                        item.channelCount,
                        item.postCount,
                    ),
                style = AppTheme.frostType.monoXs,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
            )
        }
    }
}
