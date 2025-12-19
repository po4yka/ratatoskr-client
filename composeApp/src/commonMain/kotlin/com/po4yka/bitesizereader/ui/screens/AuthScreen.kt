package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.presentation.navigation.AuthComponent
import com.po4yka.bitesizereader.presentation.viewmodel.AuthViewModel

/**
 * Authentication screen with Telegram login using Carbon Design System
 */
@Composable
fun AuthScreen(
    component: AuthComponent,
    onLoginSuccess: () -> Unit = component::onLoginSuccess,
    modifier: Modifier = Modifier,
) {
    val viewModel: AuthViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // App Logo/Icon
            Box(
                modifier =
                    Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Carbon.theme.layer01),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "B",
                    style = Carbon.typography.heading05,
                    color = Carbon.theme.textPrimary,
                )
            }

            // App Title
            Text(
                text = "Bite-Size Reader",
                style = Carbon.typography.heading04,
                color = Carbon.theme.textPrimary,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "AI-powered summaries of web articles",
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            var showTelegramLogin by remember { mutableStateOf(false) }

            if (state.isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SmallLoading()
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logging in...",
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textSecondary,
                    )
                }
            } else {
                Button(
                    label = "Login with Telegram",
                    onClick = { showTelegramLogin = true },
                    isEnabled = !state.isLoading,
                    buttonType = ButtonType.Primary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (showTelegramLogin) {
                com.po4yka.bitesizereader.ui.auth.TelegramAuthScreen(
                    authViewModel = viewModel,
                    onAuthSuccess = {
                        showTelegramLogin = false
                    },
                    onDismiss = { showTelegramLogin = false },
                )
            }

            // Developer Login (Secret Key)
            var showDevLogin by remember { mutableStateOf(false) }
            Button(
                label = "Developer Login",
                onClick = { showDevLogin = true },
                isEnabled = !state.isLoading,
                buttonType = ButtonType.Ghost,
            )

            if (showDevLogin) {
                DeveloperLoginDialog(
                    isLoading = state.isLoading,
                    error = state.error,
                    savedCredentials = state.savedDeveloperCredentials,
                    onDismiss = { showDevLogin = false },
                    onLogin = { userId, clientId, secret, rememberCredentials ->
                        viewModel.loginWithSecret(userId, clientId, secret, rememberCredentials)
                    },
                )
            }

            // Error message
            state.error?.let { error ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Carbon.theme.supportErrorInverse)
                            .padding(16.dp),
                ) {
                    Text(
                        text = error,
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textOnColorDisabled,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Features
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FeatureItem(
                    icon = "[1]",
                    text = "Get concise summaries of any web article",
                )
                FeatureItem(
                    icon = "[2]",
                    text = "Search and organize your reading history",
                )
                FeatureItem(
                    icon = "[3]",
                    text = "Sync across all your devices",
                )
            }
        }
    }

    // Handle successful login
    if (state.isAuthenticated) {
        onLoginSuccess()
    }
}

@Composable
private fun FeatureItem(
    icon: String,
    text: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = icon,
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textSecondary,
        )
        Text(
            text = text,
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )
    }
}
