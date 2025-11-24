import SwiftUI
import ComposeApp
import Shared

/// Main content view hosting the shared Compose Multiplatform UI
struct ContentView: View {
    @State private var isTelegramSheetPresented = false
    @State private var pendingLoginViewModel: LoginViewModel?

    private let rootComponent: RootComponent

    init(rootComponent: RootComponent) {
        self.rootComponent = rootComponent
    }

    var body: some View {
        ComposeRootView(
            rootComponent: rootComponent,
            onLoginRequest: { viewModel in
                pendingLoginViewModel = viewModel
                isTelegramSheetPresented = true
            }
        )
        .sheet(isPresented: $isTelegramSheetPresented) {
            if let viewModel = pendingLoginViewModel {
                TelegramAuthWebView(
                    onAuthSuccess: { authData in
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
                        rootComponent.navigateToSummaryList()
                        isTelegramSheetPresented = false
                    },
                    onCancel: { isTelegramSheetPresented = false }
                )
                .ignoresSafeArea()
            }
        }
    }
}

private struct ComposeRootView: UIViewControllerRepresentable {
    let rootComponent: RootComponent
    let onLoginRequest: (LoginViewModel) -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewController(rootComponent: rootComponent, onLoginClick: onLoginRequest)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
