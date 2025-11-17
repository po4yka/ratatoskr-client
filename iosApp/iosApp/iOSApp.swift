import SwiftUI
import Shared
import BackgroundTasks

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @Environment(\.scenePhase) var scenePhase

    var body: some Scene {
        WindowGroup {
            ContentView(
                rootComponent: appDelegate.rootComponent,
                koinHelper: appDelegate.koinHelper
            )
        }
        .onChange(of: scenePhase) { newPhase in
            if newPhase == .active {
                // Check for shared URL from Share Extension when app becomes active
                checkForSharedURL()
            }
        }
    }

    /// Check if Share Extension saved a URL to shared storage
    private func checkForSharedURL() {
        if let sharedDefaults = UserDefaults(suiteName: "group.com.po4yka.bitesizereader"),
           let sharedURL = sharedDefaults.string(forKey: "sharedURL") {

            print("[MainApp] Found shared URL: \(sharedURL)")

            // Navigate to Submit URL screen with prefilled URL
            appDelegate.rootComponent.navigateToSubmitUrl(prefilledUrl: sharedURL)

            // Clear the shared URL so it's only processed once
            sharedDefaults.removeObject(forKey: "sharedURL")
            sharedDefaults.removeObject(forKey: "sharedURLTimestamp")
            sharedDefaults.synchronize()
        }
    }
}

/// App delegate for initialization and background tasks
class AppDelegate: NSObject, UIApplicationDelegate {
    let rootComponent: RootComponent
    let koinHelper: KoinHelper

    // Background task identifier
    static let syncTaskIdentifier = "com.po4yka.bitesizereader.sync"

    override init() {
        // Initialize Koin
        let koinApp = KoinInitializerKt.initKoin(appDeclaration: { _ in })
        koinHelper = KoinHelper(koin: koinApp.koin)

        // Create root navigation component
        rootComponent = RootComponent(componentContext: DefaultComponentContext(lifecycle: ApplicationLifecycle()))

        super.init()

        // Register background tasks
        registerBackgroundTasks()
    }

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        // Schedule initial background sync
        scheduleBackgroundSync()
        return true
    }

    // MARK: - Background Tasks

    /// Register background task handlers
    private func registerBackgroundTasks() {
        #if !targetEnvironment(simulator)
        // Background tasks don't work in simulator, only on device
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: AppDelegate.syncTaskIdentifier,
            using: nil
        ) { task in
            self.handleBackgroundSync(task: task as! BGProcessingTask)
        }
        print("[BackgroundTasks] Registered background sync task")
        #else
        print("[BackgroundTasks] Skipping registration (simulator)")
        #endif
    }

    /// Handle background sync task execution
    private func handleBackgroundSync(task: BGProcessingTask) {
        print("[BackgroundTasks] Background sync task started")

        // Schedule next sync
        scheduleBackgroundSync()

        // Set expiration handler
        task.expirationHandler = {
            print("[BackgroundTasks] Task expired before completion")
            task.setTaskCompleted(success: false)
        }

        // Perform sync in background
        Task {
            do {
                // Get SyncDataUseCase from Koin
                guard let syncUseCase = koinHelper.koin.get(objCClass: SyncDataUseCase.self, qualifier: nil, parameters: nil) as? SyncDataUseCase else {
                    print("[BackgroundTasks] ERROR: Could not get SyncDataUseCase from Koin")
                    task.setTaskCompleted(success: false)
                    return
                }

                // Execute sync
                let result = try await syncUseCase.invoke(forceFullSync: false)

                if result.isSuccess {
                    print("[BackgroundTasks] Background sync completed successfully")
                    task.setTaskCompleted(success: true)
                } else {
                    print("[BackgroundTasks] Background sync failed")
                    task.setTaskCompleted(success: false)
                }
            } catch {
                print("[BackgroundTasks] Background sync error: \(error)")
                task.setTaskCompleted(success: false)
            }
        }
    }

    /// Schedule next background sync
    func scheduleBackgroundSync() {
        #if !targetEnvironment(simulator)
        let request = BGProcessingTaskRequest(identifier: AppDelegate.syncTaskIdentifier)
        request.requiresNetworkConnectivity = true
        request.requiresExternalPower = false

        // Schedule for 6 hours from now (to match Android)
        request.earliestBeginDate = Date(timeIntervalSinceNow: 6 * 60 * 60)

        do {
            try BGTaskScheduler.shared.submit(request)
            print("[BackgroundTasks] Scheduled next background sync for 6 hours from now")
        } catch {
            print("[BackgroundTasks] Could not schedule background sync: \(error)")
        }
        #else
        print("[BackgroundTasks] Skipping schedule (simulator)")
        #endif
    }
}

/// iOS lifecycle implementation
private class ApplicationLifecycle: LifecycleRegistry {
    init() {
        super.init()
        onCreate()
    }
}
