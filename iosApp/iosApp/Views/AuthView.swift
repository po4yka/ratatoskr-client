import SwiftUI
import Shared

struct AuthView: View {
    @State private var isShowingWebView = false

    // Placeholder URL - needs to be configured
    private let authUrl = URL(string: "https://bitsizereaderapi.po4yka.com/auth/login-widget?bot=bitesizereader_bot&origin=bitesizereader://telegram-auth")!

    var body: some View {
        VStack {
            Text("Welcome to Bite-Size Reader")
                .font(.title)
                .padding()

            Button("Login with Telegram") {
                isShowingWebView = true
            }
            .padding()
            .sheet(isPresented: $isShowingWebView) {
                TelegramAuthWebView(url: authUrl)
            }
        }
    }
}
