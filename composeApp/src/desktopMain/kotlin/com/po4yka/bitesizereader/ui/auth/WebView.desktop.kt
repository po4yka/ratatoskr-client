package com.po4yka.bitesizereader.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.awt.Desktop
import java.net.URI

/**
 * Desktop stub implementation of WebView
 * Opens the URL in the default browser instead of embedding
 */
@Composable
actual fun WebView(
    url: String,
    modifier: Modifier,
    onDeepLink: (String) -> Unit,
) {
    // On desktop, we open the URL in the default browser
    // and show a message to the user
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Opening authentication in browser...")
    }

    // Open URL in default browser
    try {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
