import SwiftUI
import Shared
import BackgroundTasks
import WidgetKit
import os.log

/// Logger for iOS app events
private let logger = Logger(subsystem: "com.po4yka.bitesizereader", category: "app")

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @Environment(\.scenePhase) var scenePhase
    private let recentSummariesSnapshotPublisher = RecentSummariesSnapshotPublisher()

    var body: some Scene {
        WindowGroup {
            ContentView(rootComponent: appDelegate.rootComponent)
        }
        .onChange(of: scenePhase) { newPhase in
            switch newPhase {
            case .active:
                appDelegate.lifecycle.onStart()
                appDelegate.lifecycle.onResume()
                checkForSharedURL()
                refreshRecentSummariesWidget()
            case .inactive:
                appDelegate.lifecycle.onPause()
                appDelegate.lifecycle.onStop()
            case .background:
                appDelegate.lifecycle.onPause()
                appDelegate.lifecycle.onStop()
            @unknown default:
                break
            }
        }
        .onOpenURL { url in
            // Handle deep links from widget
            handleDeepLink(url)
        }
    }

    /// Check if Share Extension saved a URL to shared storage
    private func checkForSharedURL() {
        if let sharedURL = AppGroupStore.consumeSharedURL() {
            logger.debug("Found shared URL: \(sharedURL)")
            appDelegate.rootComponent.navigateToSubmitUrl(prefilledUrl: sharedURL)
        }
    }

    /// Handle deep links from widget
    private func handleDeepLink(_ url: URL) {
        logger.debug("Received deep link: \(url)")

        // Parse widget deep link: bitesizereader://summary/{id}
        guard url.scheme == "bitesizereader" else {
            logger.warning("Unknown URL scheme: \(url.scheme ?? "nil")")
            return
        }

        if url.host == "summary", let summaryId = url.pathComponents.last {
            logger.debug("Opening summary with ID: \(summaryId)")
            appDelegate.rootComponent.navigateToSummaryDetail(summaryId: summaryId)
        } else if url.host == "submit-url" || url.host == "share" {
            checkForSharedURL()
        } else {
            logger.warning("Could not parse summary ID from URL: \(url)")
        }
    }

    private func refreshRecentSummariesWidget() {
        Task {
            do {
                _ = try await recentSummariesSnapshotPublisher.publish(limit: 3)
                WidgetCenter.shared.reloadTimelines(ofKind: AppGroupContract.recentSummariesWidgetKind)
            } catch {
                logger.error("Could not refresh widget snapshot: \(error.localizedDescription)")
            }
        }
    }
}

/// App delegate for initialization and background tasks
class AppDelegate: NSObject, UIApplicationDelegate {
    let lifecycle = ApplicationLifecycle()
    let rootComponent: RootComponent
    let koinHelper: KoinHelper

    // Background task identifier
    static let syncTaskIdentifier = "com.po4yka.bitesizereader.sync"

    override init() {
        // Initialize Koin
        let koinApp = KoinInitializerKt.initKoin(modules: KoinInitializerKt.appModules(), appDeclaration: { _ in })
        koinHelper = KoinHelper(koin: koinApp.koin)

        // Create root navigation component using the shared lifecycle
        rootComponent = RootComponent(componentContext: DefaultComponentContext(lifecycle: lifecycle))

        super.init()

        // Register background tasks
        registerBackgroundTasks()
    }

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        // Schedule initial background sync
        scheduleBackgroundSync()
        return true
    }

    func applicationWillTerminate(_ application: UIApplication) {
        lifecycle.onDestroy()
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
        logger.debug("Registered background sync task")
        #else
        logger.debug("Skipping registration (simulator)")
        #endif
    }

    /// Handle background sync task execution
    private func handleBackgroundSync(task: BGProcessingTask) {
        logger.debug("Background sync task started")

        // Schedule next sync
        scheduleBackgroundSync()

        // Set expiration handler
        task.expirationHandler = {
            logger.warning("Task expired before completion")
            task.setTaskCompleted(success: false)
        }

        // Perform sync in background
        Task {
            do {
                // Get SyncDataUseCase from Koin
                guard let syncUseCase = koinHelper.koin.get(objCClass: SyncDataUseCase.self, qualifier: nil, parameters: nil) as? SyncDataUseCase else {
                    logger.error("Could not get SyncDataUseCase from Koin")
                    task.setTaskCompleted(success: false)
                    return
                }

                // Execute sync (delta sync for background, not full sync)
                // invoke() returns Unit and throws on error via SKIE interop
                try await syncUseCase.invoke(forceFull: false)
                _ = try await RecentSummariesSnapshotPublisher().publish(limit: 3)
                WidgetCenter.shared.reloadTimelines(ofKind: AppGroupContract.recentSummariesWidgetKind)

                logger.debug("Background sync completed successfully")
                task.setTaskCompleted(success: true)
            } catch {
                logger.error("Background sync error: \(error)")
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
            logger.debug("Scheduled next background sync for 6 hours from now")
        } catch {
            logger.error("Could not schedule background sync: \(error)")
        }
        #else
        logger.debug("Skipping schedule (simulator)")
        #endif
    }
}

/// iOS lifecycle implementation that starts in the CREATED state.
/// Subsequent state transitions (start, resume, pause, stop, destroy)
/// are driven by SwiftUI's scenePhase and UIApplicationDelegate callbacks.
class ApplicationLifecycle: LifecycleRegistry {
    override init() {
        super.init()
        onCreate()
    }
}
