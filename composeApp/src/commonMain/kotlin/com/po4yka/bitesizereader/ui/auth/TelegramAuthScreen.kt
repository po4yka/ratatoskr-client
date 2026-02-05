package com.po4yka.bitesizereader.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.presentation.viewmodel.AuthViewModel
import com.po4yka.bitesizereader.util.UrlDecoder
import com.po4yka.bitesizereader.util.config.AppConfig
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Telegram authentication screen using Carbon Design System
 */
@Composable
fun TelegramAuthScreen(
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
    onDismiss: () -> Unit,
) {
    val authState by authViewModel.state.collectAsState()
    var loginStarted by remember { mutableStateOf(false) }

    LaunchedEffect(authState.isAuthenticated) {
        if (loginStarted && authState.isAuthenticated) {
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
                .background(Carbon.theme.background),
    ) {
        // Close button
        Box(Modifier.padding(top = 16.dp, start = 16.dp)) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Carbon.theme.iconPrimary,
                )
            }
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
                    authViewModel.login(authData)
                } else {
                    onDismiss()
                }
            },
        )
    }
}

private fun parseTelegramAuthData(url: String): AuthRequestDto? {
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

        return AuthRequestDto(
            id = id,
            firstName = params["first_name"] ?: "",
            lastName = params["last_name"],
            username = params["username"],
            photoUrl = params["photo_url"],
            authDate = params["auth_date"]?.toLongOrNull() ?: 0L,
            hash = hash,
        )
    } catch (e: Exception) {
        logger.error(e) { "Failed to parse Telegram auth data from URL: $url" }
        return null
    }
}
