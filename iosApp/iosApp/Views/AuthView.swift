import SwiftUI
import Shared

/// Authentication view with Telegram login
struct AuthView: View {
    @ObservedObject var viewModel: LoginViewModelWrapper
    let onLoginSuccess: () -> Void
    @State private var showAuthWebView = false

    var body: some View {
        VStack(spacing: 30) {
            Spacer()

            // App Icon
            ZStack {
                Circle()
                    .fill(Color.accentColor.opacity(0.2))
                    .frame(width: 120, height: 120)

                Text("📚")
                    .font(.system(size: 60))
            }

            // App Title
            VStack(spacing: 8) {
                Text("Bite-Size Reader")
                    .font(.largeTitle)
                    .fontWeight(.bold)

                Text("AI-powered summaries of web articles")
                    .font(.body)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
            }

            Spacer().frame(height: 40)

            // Login Button
            Button(action: {
                showAuthWebView = true
            }) {
                HStack {
                    if viewModel.state.isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        Text("Logging in...")
                            .fontWeight(.semibold)
                    } else {
                        Text("Login with Telegram")
                            .fontWeight(.semibold)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.accentColor)
                .foregroundColor(.white)
                .cornerRadius(10)
            }
            .disabled(viewModel.state.isLoading)
            .padding(.horizontal)

            // Error message
            if let error = viewModel.state.error {
                Text(error)
                    .font(.caption)
                    .foregroundColor(.red)
                    .multilineTextAlignment(.center)
                    .padding()
                    .background(Color.red.opacity(0.1))
                    .cornerRadius(8)
                    .padding(.horizontal)
            }

            Spacer().frame(height: 40)

            // Features
            VStack(alignment: .leading, spacing: 16) {
                FeatureRow(icon: "📖", text: "Get concise summaries of any web article")
                FeatureRow(icon: "🔍", text: "Search and organize your reading history")
                FeatureRow(icon: "📱", text: "Sync across all your devices")
            }
            .padding(.horizontal)

            Spacer()
        }
        .sheet(isPresented: $showAuthWebView) {
            NavigationView {
                TelegramAuthWebView(
                    onAuthSuccess: { authData in
                        // Call ViewModel with auth data
                        viewModel.loginWithTelegram(
                            telegramUserId: authData.id,
                            authHash: authData.authHash,
                            authDate: authData.authDate,
                            username: authData.username,
                            firstName: authData.firstName,
                            lastName: authData.lastName,
                            photoUrl: authData.photoUrl,
                            clientId: "ios"
                        )
                        showAuthWebView = false
                    },
                    onCancel: {
                        showAuthWebView = false
                    }
                )
                .navigationTitle("Telegram Login")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button("Cancel") {
                            showAuthWebView = false
                        }
                    }
                }
            }
        }
        .onChange(of: viewModel.state.isAuthenticated) { isAuthenticated in
            if isAuthenticated {
                onLoginSuccess()
            }
        }
    }
}

struct FeatureRow: View {
    let icon: String
    let text: String

    var body: some View {
        HStack(spacing: 12) {
            Text(icon)
                .font(.title2)

            Text(text)
                .font(.body)
                .foregroundColor(.secondary)
        }
    }
}
