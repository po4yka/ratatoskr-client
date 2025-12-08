package com.po4yka.bitesizereader.ui.auth

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun WebView(
    url: String,
    modifier: Modifier,
    onDeepLink: (String) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val requestUrl = request?.url?.toString() ?: return false
                        if (requestUrl.startsWith("bitesizereader://")) {
                            onDeepLink(requestUrl)
                            return true
                        }
                        return false
                    }
                }
                loadUrl(url)
            }
        },
        update = { webView ->
            // If we needed to update the URL dynamically we would do it here,
            // but for this auth flow it's usually static or one-off.
        }
    )
}
