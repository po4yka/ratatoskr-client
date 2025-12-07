package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.bitesizereader.presentation.navigation.SettingsComponent
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(component: SettingsComponent) {
    val viewModel: SettingsViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Account Binding",
                style = MaterialTheme.typography.titleLarge
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Telegram",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (state.isLoading) {
                        CircularProgressIndicator()
                    } else if (state.linkStatus != null) {
                        if (state.linkStatus!!.linked) {
                            Text("Status: Linked")
                            state.linkStatus!!.username?.let {
                                Text("Username: @$it")
                            }
                            state.linkStatus!!.telegramId?.let {
                                Text("ID: $it")
                            }
                            Button(
                                onClick = { viewModel.unlinkTelegram() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Unlink")
                            }
                        } else {
                            Text("Status: Not Linked")
                            Button(onClick = { viewModel.beginTelegramLink() }) {
                                Text("Link Telegram Account")
                            }
                        }
                    } else if (state.error != null) {
                        Text(
                            text = "Error: ${state.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                        OutlinedButton(onClick = { viewModel.loadLinkStatus() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            // Nonce display for manual linking if needed (Debugging / temporary flow until WebView)
            state.linkNonce?.let { nonce ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Linking Started", style = MaterialTheme.typography.titleSmall)
                        Text("Nonce generated. Use the Telegram Login Widget with this bot to complete linking.")
                        SelectionContainer {
                            Text(text = nonce, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
