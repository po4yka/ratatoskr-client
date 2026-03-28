@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.progressbar.IndeterminateProgressBar
import com.gabrieldrn.carbon.progressbar.ProgressBar
import com.gabrieldrn.carbon.progressbar.ProgressBarState
import com.po4yka.bitesizereader.domain.model.SyncPhase
import com.po4yka.bitesizereader.domain.model.SyncProgress
import com.po4yka.bitesizereader.presentation.navigation.SettingsComponent
import com.po4yka.bitesizereader.presentation.state.ReadingGoalState
import com.po4yka.bitesizereader.presentation.state.SettingsState
import com.po4yka.bitesizereader.presentation.state.SyncSettingsState
import com.po4yka.bitesizereader.presentation.state.TelegramLinkState
import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.ui.components.DeleteAccountDialog
import com.po4yka.bitesizereader.ui.components.RequestHistorySection
import com.po4yka.bitesizereader.ui.components.ScreenHeader
import com.po4yka.bitesizereader.ui.components.SessionsSection
import com.po4yka.bitesizereader.ui.components.UserStatsCard
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.Spacing
import kotlin.math.round
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.a11y_sync_progress
import bitesizereader.composeapp.generated.resources.settings_account_binding
import bitesizereader.composeapp.generated.resources.settings_goal_target
import bitesizereader.composeapp.generated.resources.settings_linked_to
import bitesizereader.composeapp.generated.resources.settings_begin_linking
import bitesizereader.composeapp.generated.resources.settings_cache_prompt
import bitesizereader.composeapp.generated.resources.settings_cached_content
import bitesizereader.composeapp.generated.resources.settings_cancel
import bitesizereader.composeapp.generated.resources.settings_cancel_sync
import bitesizereader.composeapp.generated.resources.settings_check_status
import bitesizereader.composeapp.generated.resources.settings_check_status_prompt
import bitesizereader.composeapp.generated.resources.settings_clear_cache
import bitesizereader.composeapp.generated.resources.settings_cache_size
import bitesizereader.composeapp.generated.resources.settings_current_streak
import bitesizereader.composeapp.generated.resources.settings_daily_reading_goal
import bitesizereader.composeapp.generated.resources.settings_danger_zone
import bitesizereader.composeapp.generated.resources.settings_data_management
import bitesizereader.composeapp.generated.resources.settings_delete_account
import bitesizereader.composeapp.generated.resources.settings_delete_account_description
import bitesizereader.composeapp.generated.resources.settings_digest_icon
import bitesizereader.composeapp.generated.resources.settings_digest_channels
import bitesizereader.composeapp.generated.resources.settings_digest_channels_description
import bitesizereader.composeapp.generated.resources.settings_error_prefix
import bitesizereader.composeapp.generated.resources.settings_goal_disabled
import bitesizereader.composeapp.generated.resources.settings_goal_enable_prompt
import bitesizereader.composeapp.generated.resources.settings_goal_enabled
import bitesizereader.composeapp.generated.resources.settings_import_from_backend
import bitesizereader.composeapp.generated.resources.settings_legal
import bitesizereader.composeapp.generated.resources.settings_language
import bitesizereader.composeapp.generated.resources.settings_language_auto
import bitesizereader.composeapp.generated.resources.settings_language_english
import bitesizereader.composeapp.generated.resources.settings_language_prompt
import bitesizereader.composeapp.generated.resources.settings_language_russian
import bitesizereader.composeapp.generated.resources.settings_link_telegram_prompt
import bitesizereader.composeapp.generated.resources.settings_linking_code
import bitesizereader.composeapp.generated.resources.settings_linking_code_prompt
import bitesizereader.composeapp.generated.resources.settings_loading
import bitesizereader.composeapp.generated.resources.settings_longest_streak
import bitesizereader.composeapp.generated.resources.settings_reading_goals
import bitesizereader.composeapp.generated.resources.settings_retry
import bitesizereader.composeapp.generated.resources.settings_security
import bitesizereader.composeapp.generated.resources.settings_sync_failed
import bitesizereader.composeapp.generated.resources.settings_sync_failed_items
import bitesizereader.composeapp.generated.resources.settings_synchronization
import bitesizereader.composeapp.generated.resources.settings_sync_prompt
import bitesizereader.composeapp.generated.resources.settings_telegram_account
import bitesizereader.composeapp.generated.resources.settings_title
import bitesizereader.composeapp.generated.resources.settings_unlink_telegram
import bitesizereader.composeapp.generated.resources.privacy_policy
import bitesizereader.composeapp.generated.resources.terms_of_service
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * Settings screen using Carbon Design System
 */
@Suppress("FunctionNaming")
@Composable
fun SettingsScreen(component: SettingsComponent) {
    val viewModel: SettingsViewModel = component.viewModel
    val state by viewModel.state.collectAsState()
    val readingGoalViewModel: ReadingGoalViewModel = koinInject()
    val readingGoalState by readingGoalViewModel.state.collectAsState()

    val onRetryLinkStatus = remember<() -> Unit>(viewModel) { viewModel::loadLinkStatus }
    val onBeginLink = remember<() -> Unit>(viewModel) { viewModel::beginTelegramLink }
    val onUnlink = remember<() -> Unit>(viewModel) { viewModel::unlinkTelegram }
    val onCheckLinkStatus = remember<() -> Unit>(viewModel) { viewModel::loadLinkStatus }
    val onCancelLink = remember<() -> Unit>(viewModel) { viewModel::cancelTelegramLink }
    val onImport = remember<() -> Unit>(viewModel) { viewModel::importFromBackend }
    val onCancelSync = remember<() -> Unit>(viewModel) { viewModel::cancelSync }
    val onClearCache = remember<() -> Unit>(viewModel) { viewModel::clearContentCache }
    val onShowDeleteConfirmation = remember<() -> Unit>(viewModel) { viewModel::showDeleteConfirmation }
    val onToggleSessions = remember<() -> Unit>(viewModel) { viewModel::toggleSessionsExpanded }
    val onToggleRequests = remember<() -> Unit>(viewModel) { viewModel::toggleRequestsExpanded }
    val onRetryRequest = remember<(Request) -> Unit>(viewModel) { viewModel::retryRequest }
    val onDigestClicked = remember<() -> Unit>(component) { component::onDigestClicked }
    val onLanguageChanged = remember<(String) -> Unit>(viewModel) { viewModel::updateLanguagePreference }
    val onDeleteAccount = remember<() -> Unit>(viewModel) { viewModel::deleteAccount }
    val onHideDeleteConfirmation = remember<() -> Unit>(viewModel) { viewModel::hideDeleteConfirmation }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        SettingsHeader()

        SettingsContent(
            state = state,
            readingGoalState = readingGoalState,
            onRetryLinkStatus = onRetryLinkStatus,
            onBeginLink = onBeginLink,
            onUnlink = onUnlink,
            onCheckLinkStatus = onCheckLinkStatus,
            onCancelLink = onCancelLink,
            onImport = onImport,
            onCancelSync = onCancelSync,
            onClearCache = onClearCache,
            onShowDeleteConfirmation = onShowDeleteConfirmation,
            onToggleSessions = onToggleSessions,
            onToggleRequests = onToggleRequests,
            onRetryRequest = onRetryRequest,
            onDigestClicked = onDigestClicked,
            onLanguageChanged = onLanguageChanged,
            onToggleGoalEnabled = { readingGoalViewModel.toggleEnabled() },
            onGoalTargetChanged = { readingGoalViewModel.setDailyTarget(it) },
        )

        // Delete Account Confirmation Dialog
        if (state.account.showDeleteConfirmation) {
            DeleteAccountDialog(
                isDeleting = state.account.isDeleting,
                error = state.account.deleteError,
                onConfirm = onDeleteAccount,
                onDismiss = onHideDeleteConfirmation,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SettingsHeader() {
    ScreenHeader(title = stringResource(Res.string.settings_title))
}

@Suppress("FunctionNaming", "LongParameterList", "LongMethod")
@Composable
private fun SettingsContent(
    state: SettingsState,
    readingGoalState: ReadingGoalState,
    onRetryLinkStatus: () -> Unit,
    onBeginLink: () -> Unit,
    onUnlink: () -> Unit,
    onCheckLinkStatus: () -> Unit,
    onCancelLink: () -> Unit,
    onImport: () -> Unit,
    onCancelSync: () -> Unit,
    onClearCache: () -> Unit,
    onShowDeleteConfirmation: () -> Unit,
    onToggleSessions: () -> Unit,
    onToggleRequests: () -> Unit,
    onRetryRequest: (Request) -> Unit,
    onDigestClicked: () -> Unit = {},
    onLanguageChanged: (String) -> Unit = {},
    onToggleGoalEnabled: () -> Unit = {},
    onGoalTargetChanged: (Int) -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // User Stats Card
        UserStatsCard(
            stats = state.account.userStats,
            isLoading = state.account.isLoadingStats,
        )

        Text(
            text = stringResource(Res.string.settings_account_binding),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )

        AccountBindingCard(
            telegramState = state.telegram,
            onRetryLinkStatus = onRetryLinkStatus,
            onBeginLink = onBeginLink,
            onUnlink = onUnlink,
        )

        state.telegram.linkNonce?.let { nonce ->
            LinkNonceCard(
                nonce = nonce,
                isLoading = state.telegram.isLoading,
                onCheckStatus = onCheckLinkStatus,
                onCancel = onCancelLink,
            )
        }

        // Digest Channels navigation
        DigestNavigationRow(onClick = onDigestClicked)

        // Language preference
        LanguagePreferenceCard(
            currentLanguage = state.account.userPreferences?.langPreference ?: "auto",
            isLoading = state.account.isLoadingPreferences,
            isSaving = state.account.isSavingPreferences,
            onLanguageChanged = onLanguageChanged,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.settings_reading_goals),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )

        ReadingGoalsCard(
            readingGoalState = readingGoalState,
            onToggleEnabled = onToggleGoalEnabled,
            onTargetChanged = onGoalTargetChanged,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.settings_data_management),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )

        SyncCard(
            syncState = state.sync,
            isTelegramLoading = state.telegram.isLoading,
            onImport = onImport,
            onCancelSync = onCancelSync,
        )

        CacheManagementCard(
            cacheSize = state.sync.cacheSize,
            isClearing = state.sync.isClearingCache,
            onClearCache = onClearCache,
        )

        RequestHistorySection(
            requests = state.sync.requests,
            isLoading = state.sync.isLoadingRequests,
            isExpanded = state.sync.requestsExpanded,
            onToggleExpanded = onToggleRequests,
            onRetryRequest = onRetryRequest,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.settings_security),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )

        SessionsSection(
            sessions = state.account.sessions,
            isLoading = state.account.isLoadingSessions,
            isExpanded = state.account.sessionsExpanded,
            onToggleExpanded = onToggleSessions,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LegalSection()

        Spacer(modifier = Modifier.height(16.dp))

        DangerZoneSection(
            onDeleteAccount = onShowDeleteConfirmation,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun LegalSection() {
    val uriHandler = LocalUriHandler.current

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_legal),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Carbon.theme.layer01),
        ) {
            LegalRow(
                label = stringResource(Res.string.privacy_policy),
                onClick = { uriHandler.openUri("https://bitsizereaderapi.po4yka.com/web/privacy.html") },
            )

            androidx.compose.foundation.layout.Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(1.dp)
                        .background(Carbon.theme.borderSubtle00),
            )

            LegalRow(
                label = stringResource(Res.string.terms_of_service),
                onClick = { uriHandler.openUri("https://bitsizereaderapi.po4yka.com/web/terms.html") },
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun LegalRow(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .semantics { role = Role.Button }
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = CarbonIcons.Document,
            contentDescription = null,
            tint = Carbon.theme.iconSecondary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun DangerZoneSection(onDeleteAccount: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_danger_zone),
            style = Carbon.typography.heading03,
            color = Carbon.theme.supportError,
            modifier = Modifier.semantics { heading() },
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Carbon.theme.layer01)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.settings_delete_account),
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
            )
            Text(
                text = stringResource(Res.string.settings_delete_account_description),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
            Button(
                label = stringResource(Res.string.settings_delete_account),
                onClick = onDeleteAccount,
                buttonType = ButtonType.PrimaryDanger,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun AccountBindingCard(
    telegramState: TelegramLinkState,
    onRetryLinkStatus: () -> Unit,
    onBeginLink: () -> Unit,
    onUnlink: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_telegram_account),
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )

        when {
            telegramState.isLoading -> {
                SmallLoading()
                Text(
                    text = stringResource(Res.string.settings_loading),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }
            telegramState.error != null -> {
                Text(
                    text = stringResource(Res.string.settings_error_prefix, telegramState.error.orEmpty()),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
                Button(
                    label = stringResource(Res.string.settings_retry),
                    onClick = onRetryLinkStatus,
                    buttonType = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            telegramState.linkStatus?.linked == true -> {
                val linkStatus = telegramState.linkStatus
                Text(
                    text = stringResource(Res.string.settings_linked_to, linkStatus?.username ?: "Unknown"),
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
                Button(
                    label = stringResource(Res.string.settings_unlink_telegram),
                    onClick = onUnlink,
                    buttonType = ButtonType.TertiaryDanger,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            else -> {
                Text(
                    text = stringResource(Res.string.settings_link_telegram_prompt),
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
                Button(
                    label = stringResource(Res.string.settings_begin_linking),
                    onClick = onBeginLink,
                    buttonType = ButtonType.Primary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun LinkNonceCard(
    nonce: String,
    isLoading: Boolean,
    onCheckStatus: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_linking_code),
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        Text(
            text = stringResource(Res.string.settings_linking_code_prompt),
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )
        SelectionContainer {
            Text(
                text = nonce,
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
        Text(
            text = stringResource(Res.string.settings_check_status_prompt),
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                label = stringResource(Res.string.settings_check_status),
                onClick = onCheckStatus,
                isEnabled = !isLoading,
                buttonType = ButtonType.Primary,
                modifier = Modifier.weight(1f),
            )
            Button(
                label = stringResource(Res.string.settings_cancel),
                onClick = onCancel,
                isEnabled = !isLoading,
                buttonType = ButtonType.Secondary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SyncCard(
    syncState: SyncSettingsState,
    isTelegramLoading: Boolean,
    onImport: () -> Unit,
    onCancelSync: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_synchronization),
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        Text(
            text = stringResource(Res.string.settings_sync_prompt),
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )

        if (syncState.isDownloading) {
            SyncProgressSection(
                progress = syncState.syncProgress,
                onCancelSync = onCancelSync,
            )
        } else {
            Button(
                label = stringResource(Res.string.settings_import_from_backend),
                onClick = onImport,
                isEnabled = !isTelegramLoading,
                buttonType = ButtonType.Primary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        syncState.downloadError?.let { error ->
            Text(
                text = stringResource(Res.string.settings_sync_failed, error),
                style = Carbon.typography.label01,
                color = Carbon.theme.supportError,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CacheManagementCard(
    cacheSize: Long,
    isClearing: Boolean,
    onClearCache: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_cached_content),
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        Text(
            text = stringResource(Res.string.settings_cache_prompt),
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )
        Text(
            text = stringResource(Res.string.settings_cache_size, formatCacheSize(cacheSize)),
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )
        if (isClearing) {
            SmallLoading()
        } else {
            Button(
                label = stringResource(Res.string.settings_clear_cache),
                onClick = onClearCache,
                isEnabled = cacheSize > 0,
                buttonType = ButtonType.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun formatCacheSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "${round(kb * 10) / 10} KB"
    val mb = kb / 1024.0
    return "${round(mb * 10) / 10} MB"
}

@Suppress("FunctionNaming")
@Composable
private fun SyncProgressSection(
    progress: SyncProgress?,
    onCancelSync: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SyncProgressHeader(
            phaseText = getSyncPhaseText(progress?.phase),
            onCancelSync = onCancelSync,
        )

        val syncProgressDesc =
            stringResource(
                Res.string.a11y_sync_progress,
                getSyncPhaseText(progress?.phase),
            )
        progress?.progressFraction?.let { fraction ->
            ProgressBar(
                value = fraction,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Dimensions.progressBarThickness)
                        .clip(RoundedCornerShape(2.dp))
                        .semantics { contentDescription = syncProgressDesc },
            )
        } ?: IndeterminateProgressBar(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(Dimensions.progressBarThickness)
                    .clip(RoundedCornerShape(2.dp))
                    .semantics { contentDescription = syncProgressDesc },
            state = ProgressBarState.Active,
        )

        if (progress != null) {
            SyncProgressDetails(progress = progress)
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SyncProgressHeader(
    phaseText: String,
    onCancelSync: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmallLoading()
            Text(
                text = phaseText,
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }
        IconButton(
            onClick = onCancelSync,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = CarbonIcons.Close,
                contentDescription = stringResource(Res.string.settings_cancel_sync),
                tint = Carbon.theme.iconSecondary,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SyncProgressDetails(progress: SyncProgress) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val itemsText =
            if (progress.totalItems != null) {
                "${progress.processedItems} / ${progress.totalItems} items"
            } else {
                "${progress.processedItems} items"
            }
        Text(
            text = itemsText,
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )
        if (progress.currentBatch > 0) {
            val batchText =
                if (progress.totalBatches != null) {
                    "Batch ${progress.currentBatch}/${progress.totalBatches}"
                } else {
                    "Batch ${progress.currentBatch}"
                }
            Text(
                text = batchText,
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }
    }

    if (progress.errorCount > 0) {
        Text(
            text = stringResource(Res.string.settings_sync_failed_items, progress.errorCount),
            style = Carbon.typography.label01,
            color = Carbon.theme.supportWarning,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun LanguagePreferenceCard(
    currentLanguage: String,
    isLoading: Boolean,
    isSaving: Boolean,
    onLanguageChanged: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_language),
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        Text(
            text = stringResource(Res.string.settings_language_prompt),
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )

        if (isLoading) {
            SmallLoading()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                LanguageChip(
                    label = stringResource(Res.string.settings_language_auto),
                    isSelected = currentLanguage == "auto",
                    isEnabled = !isSaving,
                    onClick = { onLanguageChanged("auto") },
                )
                LanguageChip(
                    label = stringResource(Res.string.settings_language_english),
                    isSelected = currentLanguage == "en",
                    isEnabled = !isSaving,
                    onClick = { onLanguageChanged("en") },
                )
                LanguageChip(
                    label = stringResource(Res.string.settings_language_russian),
                    isSelected = currentLanguage == "ru",
                    isEnabled = !isSaving,
                    onClick = { onLanguageChanged("ru") },
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun LanguageChip(
    label: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) Carbon.theme.linkPrimary else Carbon.theme.layer02
    val textColor = if (isSelected) Carbon.theme.textOnColor else Carbon.theme.textPrimary

    Text(
        text = label,
        style = Carbon.typography.label01,
        color = if (isEnabled) textColor else Carbon.theme.textDisabled,
        modifier =
            modifier
                .semantics { role = Role.Button }
                .background(backgroundColor)
                .clickable(enabled = isEnabled && !isSelected, onClick = onClick)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
    )
}

@Suppress("FunctionNaming")
@Composable
private fun DigestNavigationRow(onClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .semantics { role = Role.Button }
                .clickable(onClick = onClick)
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = CarbonIcons.Notification,
            contentDescription = stringResource(Res.string.settings_digest_icon),
            tint = Carbon.theme.iconPrimary,
            modifier = Modifier.size(24.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.settings_digest_channels),
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
            )
            Text(
                text = stringResource(Res.string.settings_digest_channels_description),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
        }
    }
}

private fun getSyncPhaseText(phase: SyncPhase?): String =
    when (phase) {
        SyncPhase.CREATING_SESSION -> "Creating session..."
        SyncPhase.FETCHING_FULL -> "Downloading data..."
        SyncPhase.FETCHING_DELTA -> "Fetching updates..."
        SyncPhase.PROCESSING -> "Processing..."
        SyncPhase.VALIDATING -> "Validating..."
        SyncPhase.COMPLETED -> "Completed"
        SyncPhase.FAILED -> "Failed"
        SyncPhase.CANCELLED -> "Cancelled"
        null -> "Synchronizing..."
    }

@Suppress("FunctionNaming", "LongParameterList")
@Composable
private fun ReadingGoalsCard(
    readingGoalState: ReadingGoalState,
    onToggleEnabled: () -> Unit,
    onTargetChanged: (Int) -> Unit,
) {
    val goal = readingGoalState.goalProgress?.goal
    val isEnabled = goal?.isEnabled ?: false

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.settings_daily_reading_goal),
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
            )
            val toggleText =
                if (isEnabled) {
                    stringResource(Res.string.settings_goal_enabled)
                } else {
                    stringResource(Res.string.settings_goal_disabled)
                }
            Text(
                text = toggleText,
                style = Carbon.typography.label01,
                color = if (isEnabled) Carbon.theme.supportSuccess else Carbon.theme.textSecondary,
                modifier =
                    Modifier
                        .semantics { role = Role.Button }
                        .clickable(onClick = onToggleEnabled)
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            )
        }

        if (goal != null) {
            Text(
                text = stringResource(Res.string.settings_goal_target, goal.dailyTargetMin),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val targets = listOf(5, 10, 15, 20, 30, 45, 60)
                targets.forEach { minutes ->
                    val isSelected = goal.dailyTargetMin == minutes
                    val bgColor = if (isSelected) Carbon.theme.linkPrimary else Carbon.theme.layer02
                    val textColor = if (isSelected) Carbon.theme.textOnColor else Carbon.theme.textPrimary
                    Text(
                        text = "${minutes}m",
                        style = Carbon.typography.label01,
                        color = textColor,
                        modifier =
                            Modifier
                                .background(bgColor)
                                .clickable(enabled = isEnabled && !isSelected) { onTargetChanged(minutes) }
                                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                    )
                }
            }

            if (goal.currentStreakDays > 0 || goal.longestStreakDays > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    Column {
                        Text(
                            text = "${goal.currentStreakDays}",
                            style = Carbon.typography.heading03,
                            color = Carbon.theme.textPrimary,
                        )
                        Text(
                            text = stringResource(Res.string.settings_current_streak),
                            style = Carbon.typography.label01,
                            color = Carbon.theme.textSecondary,
                        )
                    }
                    Column {
                        Text(
                            text = "${goal.longestStreakDays}",
                            style = Carbon.typography.heading03,
                            color = Carbon.theme.textPrimary,
                        )
                        Text(
                            text = stringResource(Res.string.settings_longest_streak),
                            style = Carbon.typography.label01,
                            color = Carbon.theme.textSecondary,
                        )
                    }
                }
            }
        } else {
            Text(
                text = stringResource(Res.string.settings_goal_enable_prompt),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
        }
    }
}
