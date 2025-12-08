package com.po4yka.bitesizereader.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.presentation.viewmodel.AuthViewModel
import com.po4yka.bitesizereader.util.UrlDecoder
import com.po4yka.bitesizereader.util.config.AppConfig
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

@Composable
fun TelegramAuthScreen(
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    val botUsername = AppConfig.Telegram.botUsername
    val origin = AppConfig.Telegram.callbackUrl
    // Construct the URL to open. Using the logic from the previous native implementation.
    val loginUrl = remember {
        "${AppConfig.Api.baseUrl}/v1/auth/login-widget?bot=$botUsername&origin=$origin"
    }

    Scaffold(
        topBar = {
            Box(Modifier.padding(top = 16.dp, start = 16.dp)) {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            WebView(
                url = loginUrl,
                modifier = Modifier.fillMaxSize(),
                onDeepLink = { url ->
                    val authData = parseTelegramAuthData(url)
                    if (authData != null) {
                        authViewModel.login(authData)
                        onAuthSuccess()
                    } else {
                        // Handle error or just close?
                        onDismiss()
                    }
                }
            )
        }
    }
}

private fun parseTelegramAuthData(url: String): AuthRequestDto? {
    try {
        // url format: bitesizereader://telegram-auth?id=...&...
        val query = url.substringAfter("?", "")
        if (query.isEmpty()) return null

        val params = query.split("&").associate {
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
            hash = hash
        )
    } catch (e: Exception) {
        logger.error(e) { "Failed to parse Telegram auth data from URL: $url" }
        return null
    }
}
