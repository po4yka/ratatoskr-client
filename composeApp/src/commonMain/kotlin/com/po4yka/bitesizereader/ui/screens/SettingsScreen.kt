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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.progressbar.ProgressBar
import com.gabrieldrn.carbon.progressbar.ProgressBarState
import com.po4yka.bitesizereader.domain.usecase.DownloadMode
import com.po4yka.bitesizereader.presentation.navigation.SettingsComponent
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsState
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

private const val BytesPerMb = 1_048_576.0
private const val RoundingFactor = 10.0

/**
 * Settings screen using Carbon Design System
 */
@Suppress("FunctionNaming")
@Composable
fun SettingsScreen(component: SettingsComponent) {
    val viewModel: SettingsViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Carbon.theme.background)
    ) {
        // Header
        CarbonSettingsHeader(title = "Settings")

        // Content
        SettingsContent(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            onRetryLinkStatus = viewModel::loadLinkStatus,
            onBeginLink = viewModel::beginTelegramLink,
            onUnlink = viewModel::unlinkTelegram,
            onBackup = { viewModel.downloadDatabase(DownloadMode.BACKUP) },
            onImport = { viewModel.downloadDatabase(DownloadMode.IMPORT) },
            onCancelDownload = viewModel::cancelDownload
        )
    }
}

@Composable
private fun CarbonSettingsHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Carbon.theme.layer01)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )
    }
}

@Composable
private fun SettingsContent(
    state: SettingsState,
    modifier: Modifier = Modifier,
    onRetryLinkStatus: () -> Unit,
    onBeginLink: () -> Unit,
    onUnlink: () -> Unit,
    onBackup: () -> Unit,
    onImport: () -> Unit,
    onCancelDownload: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
            onUnlink = onUnlink
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

        BackupCard(
            state = state,
            onBackup = onBackup,
            onImport = onImport,
            onCancelDownload = onCancelDownload
        )
    }
}

@Composable
private fun AccountBindingCard(
    state: SettingsState,
    onRetryLinkStatus: () -> Unit,
    onBeginLink: () -> Unit,
    onUnlink: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Carbon.theme.layer01)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Telegram",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )

        when {
            state.isLoading -> SmallLoading()
            state.linkStatus != null -> {
                val status = state.linkStatus
                if (status?.linked == true) {
                    Text(
                        text = "Status: Linked",
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textPrimary,
                    )
                    status.username?.let {
                        Text(
                            text = "Username: @$it",
                            style = Carbon.typography.label01,
                            color = Carbon.theme.textSecondary,
                        )
                    }
                    status.telegramId?.let {
                        Text(
                            text = "ID: $it",
                            style = Carbon.typography.label01,
                            color = Carbon.theme.textSecondary,
                        )
                    }
                    Button(
                        label = "Unlink",
                        onClick = onUnlink,
                        buttonType = ButtonType.PrimaryDanger,
                    )
                } else {
                    Text(
                        text = "Status: Not Linked",
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textPrimary,
                    )
                    Button(
                        label = "Link Telegram Account",
                        onClick = onBeginLink,
                        buttonType = ButtonType.Primary,
                    )
                }
            }
            state.error != null -> {
                Text(
                    text = "Error: ${state.error}",
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.supportError,
                )
                Button(
                    label = "Retry",
                    onClick = onRetryLinkStatus,
                    buttonType = ButtonType.Secondary,
                )
            }
        }
    }
}

@Composable
private fun LinkNonceCard(nonce: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Carbon.theme.layer01)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Linking Started",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        Text(
            text = "Nonce generated. Use the Telegram Login Widget with this bot to complete linking.",
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )
        SelectionContainer {
            Text(
                text = nonce,
                style = Carbon.typography.label01,
                color = Carbon.theme.textPrimary,
            )
        }
    }
}

@Composable
private fun BackupCard(
    state: SettingsState,
    onBackup: () -> Unit,
    onImport: () -> Unit,
    onCancelDownload: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Carbon.theme.layer01)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Backup",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )
        Text(
            text = "Download a full copy of your articles and summaries.",
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )

        if (state.isDownloading) {
            val progress = if (state.downloadTotal > 0) {
                state.downloadProgress.toFloat() / state.downloadTotal.toFloat()
            } else {
                0f
            }

            ProgressBar(
                value = progress,
                modifier = Modifier.fillMaxWidth(),
                state = ProgressBarState.Active,
            )

            val progressText = progressText(state.downloadProgress, state.downloadTotal)
            Text(
                text = "Processing: $progressText",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )

            Button(
                label = "Cancel Operation",
                onClick = onCancelDownload,
                buttonType = ButtonType.PrimaryDanger,
            )
        } else {
            Text(
                text = "Backup your data to a file or import the latest data from the cloud.",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )

            Button(
                label = "Backup to File",
                onClick = onBackup,
                isEnabled = !state.isLoading,
                buttonType = ButtonType.Primary,
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                label = "Import from Cloud",
                onClick = onImport,
                isEnabled = !state.isLoading,
                buttonType = ButtonType.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        state.downloadError?.let { error ->
            Text(
                text = "Download Failed: $error",
                style = Carbon.typography.label01,
                color = Carbon.theme.supportError,
            )
        }
    }
}

private fun progressText(progressBytes: Long, totalBytes: Long): String {
    val downloaded = formatMegabytes(progressBytes)
    val total = if (totalBytes > 0) formatMegabytes(totalBytes) else 0.0
    return "$downloaded MB / $total MB"
}

private fun formatMegabytes(bytes: Long): Double {
    val mb = bytes / BytesPerMb
    return (mb * RoundingFactor).roundToInt() / RoundingFactor
}
