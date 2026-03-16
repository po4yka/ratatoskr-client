@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.domain.model.SyncPhase
import com.po4yka.bitesizereader.domain.model.SyncProgress
import com.po4yka.bitesizereader.presentation.navigation.SettingsComponent
import com.po4yka.bitesizereader.presentation.state.SettingsState
import com.po4yka.bitesizereader.presentation.state.SyncSettingsState
import com.po4yka.bitesizereader.presentation.state.TelegramLinkState
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.ui.components.DeleteAccountDialog
import com.po4yka.bitesizereader.ui.components.RequestHistorySection
import com.po4yka.bitesizereader.ui.components.ScreenHeader
import com.po4yka.bitesizereader.ui.components.SessionsSection
import com.po4yka.bitesizereader.ui.components.UserStatsCard
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Spacing

/**
 * Settings screen using Carbon Design System
 */
@Suppress("FunctionNaming")
@Composable
fun SettingsScreen(component: SettingsComponent) {
    val viewModel: SettingsViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

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
    ScreenHeader(title = "Settings")
}

@Suppress("FunctionNaming", "LongParameterList")
@Composable
private fun SettingsContent(
    state: SettingsState,
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
            text = "Account Binding",
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
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
            text = "Data Management",
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
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
            text = "Security",
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )

        SessionsSection(
            sessions = state.account.sessions,
            isLoading = state.account.isLoadingSessions,
            isExpanded = state.account.sessionsExpanded,
            onToggleExpanded = onToggleSessions,
        )

        Spacer(modifier = Modifier.height(16.dp))

        DangerZoneSection(
            onDeleteAccount = onShowDeleteConfirmation,
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
            text = "Danger Zone",
            style = Carbon.typography.heading03,
            color = Carbon.theme.supportError,
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
                text = "Delete Account",
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
            )
            Text(
                text = "Permanently delete your account and all associated data. This action cannot be undone.",
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
            Button(
                label = "Delete Account",
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
            text = "Telegram Account",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )

        when {
            telegramState.isLoading -> {
                SmallLoading()
                Text(
                    text = "Loading...",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }
            telegramState.error != null -> {
                Text(
                    text = "Error: ${telegramState.error}",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
                Button(
                    label = "Retry",
                    onClick = onRetryLinkStatus,
                    buttonType = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            telegramState.linkStatus?.linked == true -> {
                val linkStatus = telegramState.linkStatus
                Text(
                    text = "Linked to: ${linkStatus?.username ?: "Unknown"}",
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
                Button(
                    label = "Unlink Telegram",
                    onClick = onUnlink,
                    buttonType = ButtonType.TertiaryDanger,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            else -> {
                Text(
                    text = "Link your Telegram account to sync your data.",
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
                Button(
                    label = "Begin Linking",
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
            text = "Linking Code",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        Text(
            text = "Send this code to the Telegram bot to complete the linking process:",
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
            text = "After sending the code to the bot, tap \"Check Status\" to verify the link.",
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                label = "Check Status",
                onClick = onCheckStatus,
                isEnabled = !isLoading,
                buttonType = ButtonType.Primary,
                modifier = Modifier.weight(1f),
            )
            Button(
                label = "Cancel",
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
            text = "Synchronization",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        Text(
            text = "Import the latest data from the backend to synchronize your local database.",
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
                label = "Import from Backend",
                onClick = onImport,
                isEnabled = !isTelegramLoading,
                buttonType = ButtonType.Primary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        syncState.downloadError?.let { error ->
            Text(
                text = "Sync Failed: $error",
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
            text = "Cached Content",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        Text(
            text = "Full article content cached for offline reading.",
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )
        Text(
            text = "Cache size: ${formatCacheSize(cacheSize)}",
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )
        if (isClearing) {
            SmallLoading()
        } else {
            Button(
                label = "Clear Cache",
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
    if (kb < 1024) return "%.1f KB".format(kb)
    val mb = kb / 1024.0
    return "%.1f MB".format(mb)
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

        progress?.progressFraction?.let { fraction ->
            LinearProgressIndicator(
                progress = { fraction },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                color = Carbon.theme.linkPrimary,
                trackColor = Carbon.theme.borderSubtle00,
            )
        } ?: LinearProgressIndicator(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
            color = Carbon.theme.linkPrimary,
            trackColor = Carbon.theme.borderSubtle00,
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
                contentDescription = "Cancel sync",
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
            text = "${progress.errorCount} items failed to sync",
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
            text = "Language",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        Text(
            text = "Select your preferred language for summaries.",
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
                    label = "Auto",
                    isSelected = currentLanguage == "auto",
                    isEnabled = !isSaving,
                    onClick = { onLanguageChanged("auto") },
                )
                LanguageChip(
                    label = "English",
                    isSelected = currentLanguage == "en",
                    isEnabled = !isSaving,
                    onClick = { onLanguageChanged("en") },
                )
                LanguageChip(
                    label = "Russian",
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
            contentDescription = "Digest",
            tint = Carbon.theme.iconPrimary,
            modifier = Modifier.size(24.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Digest Channels",
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
            )
            Text(
                text = "Manage channel subscriptions and preferences",
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
