import SwiftUI
import Shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView(
                rootComponent: appDelegate.rootComponent,
                koinHelper: appDelegate.koinHelper
            )
        }
    }
}

/// App delegate for initialization
class AppDelegate: NSObject, UIApplicationDelegate {
    let rootComponent: RootComponent
    let koinHelper: KoinHelper

    override init() {
        // Initialize Koin
        let koinApp = KoinInitializerKt.initKoin(appDeclaration: { _ in })
        koinHelper = KoinHelper(koin: koinApp.koin)

        // Create root navigation component
        rootComponent = RootComponent(componentContext: DefaultComponentContext(lifecycle: ApplicationLifecycle()))

        super.init()
    }

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        return true
    }
}

/// iOS lifecycle implementation
private class ApplicationLifecycle: LifecycleRegistry {
    init() {
        super.init()
        onCreate()
    }
}
