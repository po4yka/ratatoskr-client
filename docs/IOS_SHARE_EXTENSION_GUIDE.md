# iOS Share Extension & Background Tasks Implementation Guide

This guide provides step-by-step instructions for implementing the iOS Share Extension and Background Tasks features.

**Status**: Android implementation complete ✅ | iOS requires Xcode configuration

---

## Overview

### Completed Features

✅ **Android**:
- Share Intent receiver (receive URLs from other apps)
- WorkManager background sync (periodic sync every 6 hours)
- Prefilled URL support in SubmitURL screen

✅ **iOS**:
- Prefilled URL support in SubmitURL screen (ready for Share Extension)

### To Implement (Requires Xcode)

❌ **iOS Share Extension** - Submit URLs from Safari/other apps
❌ **iOS Background Tasks** - Background refresh for sync

---

## Part 1: iOS Share Extension

### What is a Share Extension?

A Share Extension allows users to submit URLs directly from Safari, Chrome, or any other app by using the system share sheet. When users tap "Share" on a webpage, they'll see "Bite-Size Reader" as an option.

### Step 1: Create Share Extension Target in Xcode

1. Open `iosApp.xcodeproj` in Xcode
2. **File → New → Target**
3. Select **Share Extension**
4. Configure:
   - **Product Name**: `ShareExtension`
   - **Team**: Your development team
   - **Organization Identifier**: `com.po4yka.bitesizereader`
   - **Bundle Identifier**: `com.po4yka.bitesizereader.ShareExtension`
   - **Language**: Swift
   - **Activate scheme**: Yes

### Step 2: Configure Info.plist for Share Extension

Open `ShareExtension/Info.plist` and configure:

```xml
<key>NSExtension</key>
<dict>
    <key>NSExtensionAttributes</key>
    <dict>
        <key>NSExtensionActivationRule</key>
        <dict>
            <!-- Accept URLs only -->
            <key>NSExtensionActivationSupportsWebURLWithMaxCount</key>
            <integer>1</integer>
            <key>NSExtensionActivationSupportsWebPageWithMaxCount</key>
            <integer>1</integer>
        </dict>
    </dict>
    <key>NSExtensionMainStoryboard</key>
    <string>MainInterface</string>
    <key>NSExtensionPointIdentifier</key>
    <string>com.apple.share-services</string>
</dict>
```

### Step 3: Implement ShareViewController

Replace the auto-generated `ShareViewController.swift` with:

```swift
import UIKit
import Social
import MobileCoreServices
import UniformTypeIdentifiers

class ShareViewController: SLComposeServiceViewController {

    override func isContentValid() -> Bool {
        // Always valid since we just extract URL
        return true
    }

    override func didSelectPost() {
        // Extract URL from share context
        if let item = extensionContext?.inputItems.first as? NSExtensionItem,
           let attachments = item.attachments {

            for attachment in attachments {
                // Check for URL
                if attachment.hasItemConformingToTypeIdentifier(UTType.url.identifier) {
                    attachment.loadItem(forTypeIdentifier: UTType.url.identifier, options: nil) { [weak self] (url, error) in
                        if let shareURL = url as? URL {
                            self?.saveSharedURL(shareURL.absoluteString)
                        }
                        self?.extensionContext?.completeRequest(returningItems: [], completionHandler: nil)
                    }
                    return
                }

                // Check for web page (fallback)
                if attachment.hasItemConformingToTypeIdentifier(UTType.propertyList.identifier) {
                    attachment.loadItem(forTypeIdentifier: UTType.propertyList.identifier, options: nil) { [weak self] (data, error) in
                        if let dictionary = data as? [String: Any],
                           let results = dictionary[NSExtensionJavaScriptPreprocessingResultsKey] as? [String: Any],
                           let urlString = results["URL"] as? String {
                            self?.saveSharedURL(urlString)
                        }
                        self?.extensionContext?.completeRequest(returningItems: [], completionHandler: nil)
                    }
                    return
                }
            }
        }

        // No valid URL found
        extensionContext?.completeRequest(returningItems: [], completionHandler: nil)
    }

    override func configurationItems() -> [Any]! {
        // No configuration needed
        return []
    }

    /// Save shared URL to UserDefaults (shared with main app via App Group)
    private func saveSharedURL(_ url: String) {
        // Use App Group to share data with main app
        if let sharedDefaults = UserDefaults(suiteName: "group.com.po4yka.bitesizereader") {
            sharedDefaults.set(url, forKey: "sharedURL")
            sharedDefaults.set(Date(), forKey: "sharedURLDate")
            sharedDefaults.synchronize()
        }
    }
}
```

### Step 4: Configure App Groups

Both the main app and share extension need access to shared storage.

1. **Main App Target**:
   - Select `iosApp` target
   - **Signing & Capabilities** tab
   - Click **+ Capability**
   - Add **App Groups**
   - Enable group: `group.com.po4yka.bitesizereader`

2. **Share Extension Target**:
   - Select `ShareExtension` target
   - **Signing & Capabilities** tab
   - Click **+ Capability**
   - Add **App Groups**
   - Enable group: `group.com.po4yka.bitesizereader`

### Step 5: Update Main App to Handle Shared URLs

Add this to `iOSApp.swift`:

```swift
import SwiftUI
import Shared

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
                // Check for shared URL from Share Extension
                checkForSharedURL()
            }
        }
    }

    private func checkForSharedURL() {
        if let sharedDefaults = UserDefaults(suiteName: "group.com.po4yka.bitesizereader"),
           let sharedURL = sharedDefaults.string(forKey: "sharedURL") {

            // Navigate to Submit URL screen with prefilled URL
            appDelegate.rootComponent.navigateToSubmitUrl(prefilledUrl: sharedURL)

            // Clear the shared URL
            sharedDefaults.removeObject(forKey: "sharedURL")
            sharedDefaults.synchronize()
        }
    }
}
```

### Step 6: Test Share Extension

1. **Build and Run** the main app on a device/simulator
2. Open **Safari**
3. Navigate to any webpage
4. Tap **Share button**
5. Look for **Bite-Size Reader** in the share sheet
6. Tap it → URL should be submitted to the app

---

## Part 2: iOS Background Tasks

### What are Background Tasks?

Background Tasks allow the app to perform sync operations in the background, even when the app is not running. iOS schedules these tasks opportunistically based on system conditions (battery, network, etc.).

### Step 1: Enable Background Modes

1. Select `iosApp` target in Xcode
2. **Signing & Capabilities** tab
3. Click **+ Capability**
4. Add **Background Modes**
5. Enable:
   - ☑️ **Background fetch**
   - ☑️ **Background processing**

### Step 2: Register Background Tasks

Add this to `AppDelegate.swift`:

```swift
import UIKit
import BackgroundTasks
import Shared

class AppDelegate: NSObject, UIApplicationDelegate {
    let rootComponent: RootComponent
    let koinHelper: KoinHelper

    // Background task identifiers
    static let syncTaskIdentifier = "com.po4yka.bitesizereader.sync"

    override init() {
        // ... existing init code ...

        super.init()

        // Register background tasks
        registerBackgroundTasks()
    }

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        // Schedule initial background tasks
        scheduleBackgroundSync()
        return true
    }

    // MARK: - Background Tasks

    private func registerBackgroundTasks() {
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: AppDelegate.syncTaskIdentifier,
            using: nil
        ) { task in
            self.handleBackgroundSync(task: task as! BGProcessingTask)
        }
    }

    private func handleBackgroundSync(task: BGProcessingTask) {
        // Schedule next sync
        scheduleBackgroundSync()

        // Set expiration handler
        task.expirationHandler = {
            task.setTaskCompleted(success: false)
        }

        // Perform sync
        Task {
            do {
                let syncUseCase: SyncDataUseCase = koinHelper.koin.get()
                let result = try await syncUseCase.invoke(forceFullSync: false)

                task.setTaskCompleted(success: result.isSuccess)
            } catch {
                task.setTaskCompleted(success: false)
            }
        }
    }

    func scheduleBackgroundSync() {
        let request = BGProcessingTaskRequest(identifier: AppDelegate.syncTaskIdentifier)
        request.requiresNetworkConnectivity = true
        request.requiresExternalPower = false

        // Schedule for 6 hours from now (to match Android)
        request.earliestBeginDate = Date(timeIntervalSinceNow: 6 * 60 * 60)

        do {
            try BGTaskScheduler.shared.submit(request)
            print("Background sync scheduled successfully")
        } catch {
            print("Could not schedule background sync: \\(error)")
        }
    }
}
```

### Step 3: Update Info.plist

Add background task identifiers to `Info.plist`:

```xml
<key>BGTaskSchedulerPermittedIdentifiers</key>
<array>
    <string>com.po4yka.bitesizereader.sync</string>
</array>
```

### Step 4: Test Background Tasks

Background tasks are difficult to test naturally. Use these Xcode debugging commands:

```bash
# In Xcode debugger console:

# Simulate background fetch (when app is in background)
e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateLaunchForTaskWithIdentifier:@"com.po4yka.bitesizereader.sync"]

# Simulate expiration
e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateExpirationForTaskWithIdentifier:@"com.po4yka.bitesizereader.sync"]
```

---

## Summary of Implementation Status

### ✅ Completed (Android)

1. **Share Intent**
   - ✅ AndroidManifest.xml configured
   - ✅ MainActivity handles shared URLs
   - ✅ URL extraction from share text
   - ✅ Navigation to SubmitURL with prefilled URL

2. **WorkManager Background Sync**
   - ✅ SyncWorker implementation
   - ✅ WorkManagerInitializer for scheduling
   - ✅ Periodic sync every 6 hours
   - ✅ Network and battery constraints
   - ✅ Exponential backoff on failure

### ✅ Completed (iOS - Preparation)

1. **Prefilled URL Support**
   - ✅ Screen.SubmitUrl accepts optional prefilledUrl
   - ✅ RootComponent navigation updated
   - ✅ ContentView handles prefilled URLs
   - ✅ SubmitURLViewModelWrapper.setURL() method

### ⚠️ Requires Manual Xcode Configuration (iOS)

1. **Share Extension**
   - ❌ Create extension target (requires Xcode)
   - ❌ Configure App Groups (requires Xcode)
   - Code is provided above ✅

2. **Background Tasks**
   - ❌ Enable background modes capability (requires Xcode)
   - ❌ Register task identifiers in Info.plist (requires Xcode)
   - Code is provided above ✅

---

## Testing Checklist

### Android

- [ ] Share a URL from Chrome → Opens app with URL prefilled
- [ ] Share a URL from any app → Opens app with URL prefilled
- [ ] Background sync runs every ~6 hours (check WorkManager logs)
- [ ] Sync respects network and battery constraints
- [ ] Sync retries on failure with exponential backoff

### iOS

- [ ] Share a URL from Safari → Opens app with URL prefilled
- [ ] Share a URL from Chrome → Opens app with URL prefilled
- [ ] Background sync runs periodically (test with Xcode debugger)
- [ ] App launches from background to handle shared URL

---

## Troubleshooting

### Android WorkManager Not Running

- Check battery optimization settings
- Verify network connectivity
- Check WorkManager logs: `adb logcat | grep SyncWorker`
- Trigger immediate sync for testing: `WorkManagerInitializer.triggerImmediateSync(context)`

### iOS Share Extension Not Appearing

- Verify App Group is enabled on both targets
- Check extension's Info.plist configuration
- Rebuild the project completely
- Check that extension target is selected in scheme

### iOS Background Tasks Not Running

- Background tasks only run when app is in background
- iOS schedules tasks opportunistically (not guaranteed)
- Use Xcode debugger commands to force execution
- Check console logs for scheduling errors

---

## Next Steps

1. **Immediate**: Use this guide to configure iOS features in Xcode
2. **Testing**: Thoroughly test both platforms
3. **Documentation**: Update user-facing docs about sharing features
4. **Polish**: Add user feedback (toast messages) when URLs are shared

---

**Created**: 2025-11-17
**Status**: Android complete, iOS needs Xcode configuration
**Maintained by**: Development Team
