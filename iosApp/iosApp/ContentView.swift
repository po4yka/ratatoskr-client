import SwiftUI
import ComposeApp
import Shared

/// Main content view hosting the shared Compose Multiplatform UI
struct ContentView: View {
    @State private var isTelegramSheetPresented = false
    @State private var pendingLoginViewModel: AuthViewModel?

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
                        viewModel.login(
                            authData: AuthRequestDto(
                                id = authData.id,
                                firstName = authData.firstName,
                                lastName = authData.lastName ?: "",
                                username = authData.username,
                                photoUrl = authData.photoUrl,
                                authDate = authData.authDate,
                                hash = authData.authHash
                            )
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
    let onLoginRequest: (AuthViewModel) -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewController(rootComponent: rootComponent, onLoginClick: onLoginRequest)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
