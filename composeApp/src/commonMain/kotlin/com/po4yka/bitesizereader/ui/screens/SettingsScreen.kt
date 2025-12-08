package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.bitesizereader.presentation.navigation.SettingsComponent
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsState
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
import com.po4yka.bitesizereader.domain.usecase.DownloadMode
import kotlin.math.roundToInt

private const val BytesPerMb = 1_048_576.0

private const val RoundingFactor = 10.0

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionNaming")
@Composable
fun SettingsScreen(component: SettingsComponent) {
    val viewModel: SettingsViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        SettingsContent(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
        Text(text = "Account Binding", style = MaterialTheme.typography.titleLarge)

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

        Text(text = "Data Management", style = MaterialTheme.typography.titleLarge)

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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Telegram", style = MaterialTheme.typography.titleMedium)

            when {
                state.isLoading -> CircularProgressIndicator()
                state.linkStatus != null -> {
                    val status = state.linkStatus
                    if (status?.linked == true) {
                        Text("Status: Linked")
                        status.username?.let { Text("Username: @$it") }
                        status.telegramId?.let { Text("ID: $it") }
                        Button(
                            onClick = onUnlink,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Unlink")
                        }
                    } else {
                        Text("Status: Not Linked")
                        Button(onClick = onBeginLink) {
                            Text("Link Telegram Account")
                        }
                    }
                }
                state.error != null -> {
                    Text(
                        text = "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                    OutlinedButton(onClick = onRetryLinkStatus) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkNonceCard(nonce: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Linking Started", style = MaterialTheme.typography.titleSmall)
            Text(
                "Nonce generated. Use the Telegram Login Widget with this bot to complete linking."
            )
            SelectionContainer {
                Text(text = nonce, style = MaterialTheme.typography.bodySmall)
            }
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Backup", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Download a full copy of your articles and summaries.",
                style = MaterialTheme.typography.bodyMedium
            )

            if (state.isDownloading) {
                LinearProgressIndicator(
                    progress = {
                        if (state.downloadTotal > 0) {
                            state.downloadProgress.toFloat() / state.downloadTotal.toFloat()
                        } else {
                            0f
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                val progressText = progressText(state.downloadProgress, state.downloadTotal)

                Text(
                    text = "Processing: $progressText",
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    onClick = onCancelDownload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancel Operation")
                }
            } else {
                Text(
                    text = "Backup your data to a file or import the latest data from the cloud.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onBackup,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Backup to File")
                }

                Button(
                    onClick = onImport,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import from Cloud")
                }
            }

            state.downloadError?.let { error ->
                Text(
                    text = "Download Failed: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
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
