import SwiftUI
import WebKit

struct TelegramAuthWebView: UIViewRepresentable {
    let url: URL
    
    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
        webView.navigationDelegate = context.coordinator
        return webView
    }
    
    func updateUIView(_ uiView: WKWebView, context: Context) {
        let request = URLRequest(url: url)
        uiView.load(request)
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, WKNavigationDelegate {
        var parent: TelegramAuthWebView
        
        init(_ parent: TelegramAuthWebView) {
            self.parent = parent
        }
        
        func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
            if let url = navigationAction.request.url, url.scheme == "bitesizereader" {
                // Handle deep link callback
                // Parse params and notify ViewModel
                // NotificationCenter.default.post(name: .telegramAuthSuccess, object: nil, userInfo: ["url": url])
                decisionHandler(.cancel)
                return
            }
            decisionHandler(.allow)
        }
    }
}