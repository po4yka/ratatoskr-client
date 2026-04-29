package com.po4yka.ratatoskr.feature.auth.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.telegram_auth_close
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.feature.auth.util.UrlDecoder
import com.po4yka.ratatoskr.core.ui.components.AppIconButton
import com.po4yka.ratatoskr.domain.model.TelegramAuthData
import com.po4yka.ratatoskr.util.config.AppConfig
import com.po4yka.ratatoskr.util.redactQueryAndFragment
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.compose.resources.stringResource

private val logger = KotlinLogging.logger {}

/** Telegram authentication screen. */
@Composable
fun TelegramAuthScreen(
    isAuthenticated: Boolean,
    onLogin: (TelegramAuthData) -> Unit,
    onAuthSuccess: () -> Unit,
    onDismiss: () -> Unit,
) {
    var loginStarted by remember { mutableStateOf(false) }

    LaunchedEffect(isAuthenticated) {
        if (loginStarted && isAuthenticated) {
            onAuthSuccess()
        }
    }

    val botUsername = AppConfig.Telegram.botUsername
    val origin = AppConfig.Telegram.callbackUrl
    val loginUrl =
        remember {
            "${AppConfig.Api.baseUrl}/v1/auth/login-widget?bot=$botUsername&origin=$origin"
        }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
    ) {
        // Close button
        Box(Modifier.padding(top = 16.dp, start = 16.dp)) {
            AppIconButton(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.telegram_auth_close),
                onClick = onDismiss,
            )
        }

        // WebView for authentication
        WebView(
            url = loginUrl,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp),
            onDeepLink = { url ->
                if (loginStarted) return@WebView
                val authData = parseTelegramAuthData(url)
                if (authData != null) {
                    loginStarted = true
                    onLogin(authData)
                } else {
                    onDismiss()
                }
            },
        )
    }
}

private fun parseTelegramAuthData(url: String): TelegramAuthData? {
    try {
        val query = url.substringAfter("?", "")
        if (query.isEmpty()) return null

        val params =
            query.split("&").associate {
                val parts = it.split("=")
                val key = parts[0]
                val value = if (parts.size > 1) UrlDecoder.decode(parts[1]) else ""
                key to value
            }

        val id = params["id"] ?: return null
        val hash = params["hash"] ?: return null

        return TelegramAuthData(
            id = id,
            firstName = params["first_name"] ?: "",
            lastName = params["last_name"],
            username = params["username"],
            photoUrl = params["photo_url"],
            authDate = params["auth_date"]?.toLongOrNull() ?: 0L,
            hash = hash,
        )
    } catch (e: Exception) {
        logger.error(e) { "Failed to parse Telegram auth data from callback: ${url.redactQueryAndFragment()}" }
        return null
    }
}
