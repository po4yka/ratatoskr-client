import SwiftUI
import ComposeApp
import Shared

/// Main content view hosting the shared Compose Multiplatform UI
/// Main content view hosting the shared Compose Multiplatform UI
struct ContentView: View {
    private let rootComponent: RootComponent

    init(rootComponent: RootComponent) {
        self.rootComponent = rootComponent
    }

    var body: some View {
        ComposeRootView(
            rootComponent: rootComponent
        )
        .ignoresSafeArea(.keyboard) // Optional: handle keyboard better if needed
    }
}

private struct ComposeRootView: UIViewControllerRepresentable {
    let rootComponent: RootComponent

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewController(rootComponent: rootComponent)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
