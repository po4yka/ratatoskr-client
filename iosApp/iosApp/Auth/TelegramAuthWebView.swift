import SwiftUI
import WebKit

/// WKWebView wrapper for Telegram authentication
struct TelegramAuthWebView: UIViewRepresentable {
    let onAuthSuccess: (TelegramAuthData) -> Void
    let onCancel: () -> Void

    func makeCoordinator() -> Coordinator {
        Coordinator(onAuthSuccess: onAuthSuccess, onCancel: onCancel)
    }

    func makeUIView(context: Context) -> WKWebView {
        let configuration = WKWebViewConfiguration()
        configuration.userContentController = WKUserContentController()

        // Add message handler for Telegram auth callback
        configuration.userContentController.add(context.coordinator, name: "telegramAuthHandler")

        let webView = WKWebView(frame: .zero, configuration: configuration)
        webView.navigationDelegate = context.coordinator

        // Load Telegram Login Widget
        let htmlString = TelegramAuthHelper.buildAuthHTML()
        webView.loadHTMLString(htmlString, baseURL: URL(string: "https://oauth.telegram.org"))

        return webView
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {
        // No updates needed
    }

    class Coordinator: NSObject, WKNavigationDelegate, WKScriptMessageHandler {
        let onAuthSuccess: (TelegramAuthData) -> Void
        let onCancel: () -> Void

        init(onAuthSuccess: @escaping (TelegramAuthData) -> Void, onCancel: @escaping () -> Void) {
            self.onAuthSuccess = onAuthSuccess
            self.onCancel = onCancel
        }

        func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
            if message.name == "telegramAuthHandler",
               let body = message.body as? [String: Any] {
                // Parse auth data
                if let authData = TelegramAuthData.from(dict: body) {
                    onAuthSuccess(authData)
                }
            }
        }

        func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
            if let url = navigationAction.request.url,
               url.scheme == AppConfiguration.Telegram.deepLinkScheme {
                // Handle deep link callback
                if let authData = TelegramAuthHelper.parseAuthCallback(url: url) {
                    onAuthSuccess(authData)
                }
                decisionHandler(.cancel)
                return
            }

            decisionHandler(.allow)
        }
    }
}

/// Telegram auth data model
struct TelegramAuthData {
    let id: Int64
    let authHash: String
    let authDate: Int64
    let username: String?
    let firstName: String?
    let lastName: String?
    let photoUrl: String?

    static func from(dict: [String: Any]) -> TelegramAuthData? {
        guard let id = dict["id"] as? Int64,
              let hash = dict["hash"] as? String,
              let authDate = dict["auth_date"] as? Int64 else {
            return nil
        }

        return TelegramAuthData(
            id: id,
            authHash: hash,
            authDate: authDate,
            username: dict["username"] as? String,
            firstName: dict["first_name"] as? String,
            lastName: dict["last_name"] as? String,
            photoUrl: dict["photo_url"] as? String
        )
    }
}

/// Helper for Telegram authentication
struct TelegramAuthHelper {
    /// Telegram bot username from centralized configuration
    private static var botUsername: String {
        AppConfiguration.Telegram.botUsername
    }

    /// Build HTML with embedded Telegram Login Widget
    static func buildAuthHTML() -> String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    min-height: 100vh;
                    margin: 0;
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                    background-color: #f5f5f5;
                }
                .container {
                    text-align: center;
                    padding: 20px;
                }
                h2 {
                    color: #333;
                    margin-bottom: 20px;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h2>Login with Telegram</h2>
                <script async src="https://telegram.org/js/telegram-widget.js?22"
                        data-telegram-login="\(botUsername)"
                        data-size="large"
                        data-onauth="onTelegramAuth(user)"
                        data-request-access="write">
                </script>
            </div>

            <script type="text/javascript">
                function onTelegramAuth(user) {
                    // Send auth data to native code
                    if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.telegramAuthHandler) {
                        window.webkit.messageHandlers.telegramAuthHandler.postMessage(user);
                    }
                }
            </script>
        </body>
        </html>
        """
    }

    /// Parse auth callback from deep link URL
    static func parseAuthCallback(url: URL) -> TelegramAuthData? {
        guard url.scheme == AppConfiguration.Telegram.deepLinkScheme,
              url.host == AppConfiguration.Telegram.deepLinkHost else {
            return nil
        }

        let components = URLComponents(url: url, resolvingAgainstBaseURL: false)
        guard let queryItems = components?.queryItems else {
            return nil
        }

        var dict: [String: Any] = [:]
        for item in queryItems {
            if let value = item.value {
                if let intValue = Int64(value) {
                    dict[item.name] = intValue
                } else {
                    dict[item.name] = value
                }
            }
        }

        return TelegramAuthData.from(dict: dict)
    }
}
