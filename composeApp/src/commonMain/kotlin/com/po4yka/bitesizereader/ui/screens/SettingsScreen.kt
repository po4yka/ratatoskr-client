@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.domain.model.SyncPhase
import com.po4yka.bitesizereader.domain.model.SyncProgress
import com.po4yka.bitesizereader.presentation.navigation.SettingsComponent
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsState
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
import com.po4yka.bitesizereader.ui.icons.CarbonIcons

/**
 * Settings screen using Carbon Design System
 */
@Suppress("FunctionNaming")
@Composable
fun SettingsScreen(component: SettingsComponent) {
    val viewModel: SettingsViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        SettingsHeader()

        SettingsContent(
            state = state,
            onRetryLinkStatus = viewModel::loadLinkStatus,
            onBeginLink = viewModel::beginTelegramLink,
            onUnlink = viewModel::unlinkTelegram,
            onImport = viewModel::importFromBackend,
            onCancelSync = viewModel::cancelSync,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SettingsHeader() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Carbon.theme.layer01)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Settings",
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )
    }
}

@Suppress("FunctionNaming", "LongParameterList")
@Composable
private fun SettingsContent(
    state: SettingsState,
    onRetryLinkStatus: () -> Unit,
    onBeginLink: () -> Unit,
    onUnlink: () -> Unit,
    onImport: () -> Unit,
    onCancelSync: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Account Binding",
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )

        AccountBindingCard(
            state = state,
            onRetryLinkStatus = onRetryLinkStatus,
            onBeginLink = onBeginLink,
            onUnlink = onUnlink,
        )

        state.linkNonce?.let { nonce ->
            LinkNonceCard(nonce = nonce)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Data Management",
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )

        SyncCard(
            state = state,
            onImport = onImport,
            onCancelSync = onCancelSync,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun AccountBindingCard(
    state: SettingsState,
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
            state.isLoading -> {
                SmallLoading()
                Text(
                    text = "Loading...",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }
            state.error != null -> {
                Text(
                    text = "Error: ${state.error}",
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
            state.linkStatus?.linked == true -> {
                val linkStatus = state.linkStatus
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
private fun LinkNonceCard(nonce: String) {
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
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SyncCard(
    state: SettingsState,
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

        if (state.isDownloading) {
            SyncProgressSection(
                progress = state.syncProgress,
                onCancelSync = onCancelSync,
            )
        } else {
            Button(
                label = "Import from Backend",
                onClick = onImport,
                isEnabled = !state.isLoading,
                buttonType = ButtonType.Primary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        state.downloadError?.let { error ->
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
private fun SyncProgressSection(
    progress: SyncProgress?,
    onCancelSync: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Progress header with cancel button
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
                    text = getSyncPhaseText(progress?.phase),
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

        // Progress bar
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

        // Progress details
        progress?.let { p ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val itemsText =
                    if (p.totalItems != null) {
                        "${p.processedItems} / ${p.totalItems} items"
                    } else {
                        "${p.processedItems} items"
                    }
                Text(
                    text = itemsText,
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
                if (p.currentBatch > 0) {
                    val batchText =
                        if (p.totalBatches != null) {
                            "Batch ${p.currentBatch}/${p.totalBatches}"
                        } else {
                            "Batch ${p.currentBatch}"
                        }
                    Text(
                        text = batchText,
                        style = Carbon.typography.label01,
                        color = Carbon.theme.textSecondary,
                    )
                }
            }

            // Error count warning
            if (p.errorCount > 0) {
                Text(
                    text = "${p.errorCount} items failed to sync",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportWarning,
                )
            }
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
