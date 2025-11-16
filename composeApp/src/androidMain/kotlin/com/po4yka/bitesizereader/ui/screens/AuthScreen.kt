package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.po4yka.bitesizereader.auth.TelegramAuthHelper
import com.po4yka.bitesizereader.presentation.viewmodel.LoginViewModel

/**
 * Authentication screen with Telegram login
 */
@Composable
fun AuthScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App Logo/Icon (placeholder)
            Surface(
                modifier = Modifier.size(100.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "📚",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }

            // App Title
            Text(
                text = "Bite-Size Reader",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = "AI-powered summaries of web articles",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = {
                    // Launch Telegram authentication in Custom Tab
                    TelegramAuthHelper.launchTelegramAuth(context)
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logging in...")
                } else {
                    Text("Login with Telegram")
                }
            }

            // Error message
            state.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Features
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureItem(
                    icon = "📖",
                    text = "Get concise summaries of any web article"
                )
                FeatureItem(
                    icon = "🔍",
                    text = "Search and organize your reading history"
                )
                FeatureItem(
                    icon = "📱",
                    text = "Sync across all your devices"
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
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
