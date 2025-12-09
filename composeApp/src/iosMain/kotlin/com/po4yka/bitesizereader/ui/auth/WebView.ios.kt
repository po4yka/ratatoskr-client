package com.po4yka.bitesizereader.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun WebView(
    url: String,
    modifier: Modifier,
    onDeepLink: (String) -> Unit,
) {
    val navigationDelegate =
        remember {
            object : NSObject(), WKNavigationDelegateProtocol {
                override fun webView(
                    webView: WKWebView,
                    decidePolicyForNavigationAction: WKNavigationAction,
                    decisionHandler: (WKNavigationActionPolicy) -> Unit,
                ) {
                    val requestUrl = decidePolicyForNavigationAction.request.URL?.absoluteString
                    if (requestUrl != null && requestUrl.startsWith("bitesizereader://")) {
                        onDeepLink(requestUrl)
                        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                    } else {
                        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
                    }
                }
            }
        }

    UIKitView(
        modifier = modifier,
        factory = {
            WKWebView().apply {
                this.navigationDelegate = navigationDelegate
                val nsUrl = NSURL.URLWithString(url)
                if (nsUrl != null) {
                    loadRequest(NSURLRequest.requestWithURL(nsUrl))
                }
            }
        },
        update = {
            // Update logic if needed
        },
    )
}
