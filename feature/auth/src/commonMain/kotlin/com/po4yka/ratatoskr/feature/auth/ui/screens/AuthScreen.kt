package com.po4yka.ratatoskr.feature.auth.ui.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.AppLogo
import com.po4yka.ratatoskr.core.ui.components.AppSmallSpinner
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.a11y_loading
import ratatoskr.core.ui.generated.resources.auth_app_title
import ratatoskr.core.ui.generated.resources.auth_developer_login
import ratatoskr.core.ui.generated.resources.auth_feature_search
import ratatoskr.core.ui.generated.resources.auth_feature_summaries
import ratatoskr.core.ui.generated.resources.auth_feature_sync
import ratatoskr.core.ui.generated.resources.auth_login_with_telegram
import ratatoskr.core.ui.generated.resources.auth_logging_in
import ratatoskr.core.ui.generated.resources.auth_subtitle
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import com.po4yka.ratatoskr.presentation.navigation.AuthComponent

/**
 * Authentication screen with Telegram login.
 */
@Composable
fun AuthScreen(
    component: AuthComponent,
    onLoginSuccess: () -> Unit = component::onLoginSuccess,
    modifier: Modifier = Modifier,
) {
    val viewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
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
            // App Logo
            AppLogo(
                modifier = Modifier.size(100.dp),
                size = 100.dp,
            )

            // App Title
            Text(
                text = stringResource(Res.string.auth_app_title),
                style = AppTheme.type.heading04,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )

            Text(
                text = stringResource(Res.string.auth_subtitle),
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            var showTelegramLogin by remember { mutableStateOf(false) }

            if (state.isLoading) {
                val loadingDesc = stringResource(Res.string.a11y_loading)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = loadingDesc },
                ) {
                    AppSmallSpinner()
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.auth_logging_in),
                        style = AppTheme.type.bodyCompact01,
                        color = AppTheme.colors.textSecondary,
                    )
                }
            } else {
                Button(
                    onClick = { showTelegramLogin = true },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(Res.string.auth_login_with_telegram))
                }
            }

            if (showTelegramLogin) {
                com.po4yka.ratatoskr.feature.auth.ui.auth.TelegramAuthScreen(
                    isAuthenticated = state.isAuthenticated,
                    onLogin = viewModel::login,
                    onAuthSuccess = {
                        showTelegramLogin = false
                    },
                    onDismiss = { showTelegramLogin = false },
                )
            }

            // Developer Login (Secret Key)
            var showDevLogin by remember { mutableStateOf(false) }
            TextButton(
                onClick = { showDevLogin = true },
                enabled = !state.isLoading,
            ) {
                Text(stringResource(Res.string.auth_developer_login))
            }

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
                            .background(AppTheme.colors.supportErrorInverse)
                            .padding(16.dp),
                ) {
                    Text(
                        text = error,
                        style = AppTheme.type.bodyCompact01,
                        color = AppTheme.colors.textOnColorDisabled,
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
                    text = stringResource(Res.string.auth_feature_summaries),
                )
                FeatureItem(
                    icon = "[2]",
                    text = stringResource(Res.string.auth_feature_search),
                )
                FeatureItem(
                    icon = "[3]",
                    text = stringResource(Res.string.auth_feature_sync),
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
            style = AppTheme.type.headingCompact01,
            color = AppTheme.colors.textSecondary,
        )
        Text(
            text = text,
            style = AppTheme.type.bodyCompact01,
            color = AppTheme.colors.textSecondary,
        )
    }
}
