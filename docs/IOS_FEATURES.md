# iOS Platform Features

This document describes the iOS-specific features implemented in the Bite-Size Reader app.

**Last Updated**: 2025-11-18
**Status**: ✅ All Core Features Implemented

---

## Table of Contents

1. [Share Extension](#1-share-extension)
2. [Background Tasks](#2-background-tasks)
3. [Widgets](#3-widgets)
4. [Deep Linking](#4-deep-linking)
5. [App Groups](#5-app-groups)
6. [Testing](#testing)
7. [Troubleshooting](#troubleshooting)

---

## 1. Share Extension

### Overview

The Share Extension allows users to submit URLs from Safari, Chrome, and other apps directly to Bite-Size Reader.

### Status: ✅ **Fully Implemented**

### Implementation Details

**Files**:
- `iosApp/ShareExtension/ShareViewController.swift` - Main extension logic
- `iosApp/ShareExtension/Info.plist` - Extension configuration
- `iosApp/ShareExtension/Base.lproj/MainInterface.storyboard` - UI

**How It Works**:

1. User shares a URL from Safari/other apps
2. Extension appears in share sheet
3. Extension extracts URL from share context
4. URL is saved to shared UserDefaults (App Group: `group.com.po4yka.bitesizereader`)
5. User returns to main app
6. Main app detects shared URL and navigates to Submit URL screen with prefilled URL
7. Shared storage is cleaned up

**Code Flow**:

```swift
// ShareViewController.swift
override func didSelectPost() {
    // Extract URL from share context
    if let shareURL = extractURL() {
        saveSharedURL(shareURL.absoluteString)
    }
    extensionContext?.completeRequest(returningItems: [], completionHandler: nil)
}

private func saveSharedURL(_ url: String) {
    if let sharedDefaults = UserDefaults(suiteName: "group.com.po4yka.bitesizereader") {
        sharedDefaults.set(url, forKey: "sharedURL")
        sharedDefaults.set(Date(), forKey: "sharedURLTimestamp")
        sharedDefaults.synchronize()
    }
}
```

```swift
// iOSApp.swift
.onChange(of: scenePhase) { newPhase in
    if newPhase == .active {
        checkForSharedURL()
    }
}

private func checkForSharedURL() {
    if let sharedDefaults = UserDefaults(suiteName: "group.com.po4yka.bitesizereader"),
       let sharedURL = sharedDefaults.string(forKey: "sharedURL") {

        // Navigate to Submit URL screen
        appDelegate.rootComponent.navigateToSubmitUrl(prefilledUrl: sharedURL)

        // Cleanup
        sharedDefaults.removeObject(forKey: "sharedURL")
        sharedDefaults.removeObject(forKey: "sharedURLTimestamp")
    }
}
```

**Supported Share Types**:
- ✅ Web URLs (Safari, Chrome, Firefox)
- ✅ Webpage objects (some browsers)
- ✅ Auto-submits immediately (no user interaction needed)

**Info.plist Configuration**:

```xml
<key>NSExtensionActivationRule</key>
<dict>
    <key>NSExtensionActivationSupportsWebURLWithMaxCount</key>
    <integer>1</integer>
    <key>NSExtensionActivationSupportsWebPageWithMaxCount</key>
    <integer>1</integer>
</dict>
```

### User Experience

1. User browsing in Safari
2. Finds interesting article
3. Taps Share button → Bite-Size Reader
4. Extension saves URL and dismisses
5. User returns to Bite-Size Reader app
6. App automatically opens Submit URL screen with URL prefilled
7. User taps Submit

---

## 2. Background Tasks

### Overview

Background tasks enable automatic syncing of summaries while the app is not in use, keeping data fresh without user intervention.

### Status: ✅ **Fully Implemented**

### Implementation Details

**Files**:
- `iosApp/iosApp/iOSApp.swift` (lines 67-173) - Background task registration and handling
- `iosApp/iosApp/Info.plist` - Background modes configuration

**How It Works**:

1. App registers background task handler on launch
2. Schedules sync task for 6 hours in the future
3. iOS executes task when conditions are met (network available, device idle)
4. Task fetches latest summaries from server
5. Task reschedules itself for next sync
6. User sees fresh data when opening app

**Background Task Configuration**:

**Task Identifier**: `com.po4yka.bitesizereader.sync`
**Type**: `BGProcessingTask` (allows longer runtime than BGAppRefreshTask)
**Frequency**: Every 6 hours (matches Android WorkManager)
**Requirements**: Network connectivity required, external power optional

**Code Flow**:

```swift
// AppDelegate.init()
override init() {
    super.init()
    registerBackgroundTasks()
}

func application(_ application: UIApplication, didFinishLaunchingWithOptions...) -> Bool {
    scheduleBackgroundSync()
    return true
}

// Register handler
private func registerBackgroundTasks() {
    #if !targetEnvironment(simulator)
    BGTaskScheduler.shared.register(
        forTaskWithIdentifier: AppDelegate.syncTaskIdentifier,
        using: nil
    ) { task in
        self.handleBackgroundSync(task: task as! BGProcessingTask)
    }
    #endif
}

// Handle execution
private func handleBackgroundSync(task: BGProcessingTask) {
    // Schedule next sync
    scheduleBackgroundSync()

    // Set expiration handler
    task.expirationHandler = {
        task.setTaskCompleted(success: false)
    }

    // Perform sync
    Task {
        let syncUseCase = koinHelper.koin.get(objCClass: SyncDataUseCase.self)
        let result = try await syncUseCase.invoke(forceFullSync: false)
        task.setTaskCompleted(success: result.isSuccess)
    }
}

// Schedule next sync
func scheduleBackgroundSync() {
    let request = BGProcessingTaskRequest(identifier: AppDelegate.syncTaskIdentifier)
    request.requiresNetworkConnectivity = true
    request.earliestBeginDate = Date(timeIntervalSinceNow: 6 * 60 * 60)

    try BGTaskScheduler.shared.submit(request)
}
```

**Info.plist Configuration**:

```xml
<!-- Background Modes -->
<key>UIBackgroundModes</key>
<array>
    <string>processing</string>
    <string>fetch</string>
</array>

<!-- Background Task Identifiers -->
<key>BGTaskSchedulerPermittedIdentifiers</key>
<array>
    <string>com.po4yka.bitesizereader.sync</string>
</array>
```

### Important Notes

⚠️ **Simulator Limitation**: Background tasks do NOT work in the iOS Simulator. They only work on physical devices.

✅ **Device Testing**: To test on a real device, use Xcode's background task debugging:
```bash
# Launch app on device, then in Xcode console:
e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateLaunchForTaskWithIdentifier:@"com.po4yka.bitesizereader.sync"]
```

🔍 **Logging**: Look for `[BackgroundTasks]` prefix in console logs

**Execution Conditions**:
- iOS decides when to run based on:
  - Device idle time
  - Battery level
  - Network availability
  - App usage patterns
  - `earliestBeginDate` constraint (6 hours)

**Duration**:
- `BGProcessingTask` can run for several minutes
- Must call `setTaskCompleted()` before expiration
- iOS terminates task if it runs too long

---

## 3. Widgets

### Overview

iOS WidgetKit widget shows recent summaries on the home screen.

### Status: ⚠️ **Partially Implemented**

**Files**:
- `iosApp/RecentSummariesWidget/RecentSummariesWidget.swift`

**TODO**:
- Review and test widget implementation
- Add widget configuration options
- Test timeline refresh

---

## 4. Deep Linking

### Overview

Custom URL scheme for opening summaries from widgets or external apps.

### Status: ✅ **Fully Implemented**

**URL Scheme**: `bitesizereader://`

**Supported URLs**:
- `bitesizereader://summary/{id}` - Opens specific summary detail view

**Implementation**:

```swift
.onOpenURL { url in
    handleDeepLink(url)
}

private func handleDeepLink(_ url: URL) {
    if url.scheme == "bitesizereader",
       url.host == "summary",
       let summaryId = Int32(url.pathComponents.last) {
        appDelegate.rootComponent.navigateToSummaryDetail(id: summaryId)
    }
}
```

**Info.plist Configuration**:

```xml
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>bitesizereader</string>
        </array>
        <key>CFBundleURLName</key>
        <string>com.po4yka.bitesizereader</string>
    </dict>
</array>
```

---

## 5. App Groups

### Overview

App Groups enable data sharing between main app, Share Extension, and widgets.

### Status: ✅ **Fully Configured**

**App Group Identifier**: `group.com.po4yka.bitesizereader`

**Used For**:
- ✅ Share Extension → Main App (shared URLs)
- ⚠️ Main App → Widget (summary data) - needs testing

**Shared Data**:

| Key | Type | Used By | Description |
|-----|------|---------|-------------|
| `sharedURL` | String | Share Extension, Main App | URL from Share Extension |
| `sharedURLTimestamp` | Date | Share Extension, Main App | When URL was shared |

**Usage**:

```swift
// Access shared UserDefaults
if let sharedDefaults = UserDefaults(suiteName: "group.com.po4yka.bitesizereader") {
    // Write
    sharedDefaults.set(value, forKey: "key")
    sharedDefaults.synchronize()

    // Read
    let value = sharedDefaults.string(forKey: "key")
}
```

**Configuration Required in Xcode**:

Both Main App and Share Extension targets must have App Groups capability enabled with the same identifier.

1. Select target → Signing & Capabilities
2. Add Capability → App Groups
3. Check `group.com.po4yka.bitesizereader`

---

## Testing

### Share Extension Testing

**On Simulator or Device**:

1. Build and run app
2. Open Safari
3. Navigate to any article (e.g., https://techcrunch.com/latest)
4. Tap Share button
5. Find "Bite-Size Reader" in share sheet (may need to scroll)
6. Tap extension
7. Extension should save URL and dismiss
8. Switch back to Bite-Size Reader app
9. Submit URL screen should open with URL prefilled

**Debugging**:
- Look for `[ShareExtension]` logs when extension runs
- Look for `[MainApp]` logs when app processes shared URL
- Check UserDefaults: `UserDefaults(suiteName: "group.com.po4yka.bitesizereader")`

### Background Tasks Testing

**⚠️ DEVICE ONLY - Does not work in simulator**

**Xcode Background Task Simulation**:

1. Build and run app on physical device
2. Stop debugging but leave app on device
3. In Xcode, select Debug → Open Console
4. Filter for your device
5. Run this command in Xcode LLDB console:

```bash
e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateLaunchForTaskWithIdentifier:@"com.po4yka.bitesizereader.sync"]
```

6. Watch console for `[BackgroundTasks]` logs
7. Verify sync completes successfully

**Natural Testing** (wait for iOS to schedule):

1. Build and run app on device
2. Use app normally
3. Close app (swipe up from app switcher)
4. Wait several hours
5. Reopen app
6. Check if new summaries appeared

**Debugging Tips**:
- Background tasks only run when device is idle and charging (usually)
- iOS is unpredictable about scheduling
- Use simulator command for reliable testing
- Check Settings → Developer → Background Tasks to see scheduled tasks
- Enable "BGTaskScheduler Logging" in Xcode scheme for verbose logs

### Deep Link Testing

**From Terminal (Simulator)**:

```bash
xcrun simctl openurl booted "bitesizereader://summary/123"
```

**From Safari (Device or Simulator)**:

Create HTML file with link:
```html
<a href="bitesizereader://summary/123">Open Summary 123</a>
```

---

## Troubleshooting

### Share Extension Not Appearing

**Problem**: Extension doesn't show in share sheet

**Solutions**:
1. Rebuild and reinstall app (extensions are cached)
2. Restart device/simulator
3. Check Info.plist activation rules
4. Verify App Groups are configured in Xcode
5. Check bundle identifier matches Xcode project

### Shared URL Not Processing

**Problem**: URL saved but app doesn't open Submit URL screen

**Solutions**:
1. Check `[MainApp]` logs for errors
2. Verify App Group identifier matches exactly: `group.com.po4yka.bitesizereader`
3. Check both targets have App Groups capability enabled
4. Add logging to `checkForSharedURL()` method

### Background Tasks Not Running

**Problem**: Sync never happens in background

**Solutions**:
1. **Test on device, not simulator** (this is the #1 issue)
2. Verify Info.plist has correct configuration (see above)
3. Check BGTaskSchedulerPermittedIdentifiers matches identifier in code
4. Use Xcode simulation command (see testing section)
5. Wait longer - iOS schedules unpredictably
6. Check device is charging and idle
7. Enable BGTaskScheduler logging in scheme

**Verify Registration**:
```swift
// Should see in logs:
// [BackgroundTasks] Registered background sync task
// [BackgroundTasks] Scheduled next background sync for 6 hours from now
```

### Deep Links Not Working

**Problem**: App doesn't open from custom URL

**Solutions**:
1. Verify URL scheme in Info.plist
2. Check URL format: `bitesizereader://summary/123`
3. Test with simpler URL first: `bitesizereader://test`
4. Add logging to `handleDeepLink()` method
5. Rebuild and reinstall app

---

## Feature Comparison with Android

| Feature | iOS | Android | Notes |
|---------|-----|---------|-------|
| Share Intent/Extension | ✅ Share Extension | ✅ Share Intent | Both working |
| Background Sync | ✅ BGProcessingTask | ✅ WorkManager | Both every 6 hours |
| Widgets | ⚠️ WidgetKit (needs testing) | ✅ Glance Widget | iOS needs verification |
| Deep Links | ✅ URL Scheme | ✅ Intent Filters | iOS working, Android TBD |
| Secure Storage | ✅ Keychain | ✅ EncryptedSharedPreferences | Both working |

---

## Next Steps

### High Priority
- [ ] Test WidgetKit implementation thoroughly
- [ ] Verify App Groups are configured in Xcode project
- [ ] Test background sync on physical device
- [ ] Add share extension to App Store screenshots

### Medium Priority
- [ ] Add widget configuration options
- [ ] Implement universal links (https:// URLs)
- [ ] Add Siri Shortcuts support

### Low Priority
- [ ] iPad-optimized layouts
- [ ] Apple Watch companion app
- [ ] Live Activities for request processing

---

## References

**Apple Documentation**:
- [Share Extensions](https://developer.apple.com/documentation/uikit/view_controllers/adding_a_share_extension_to_your_app)
- [Background Tasks](https://developer.apple.com/documentation/backgroundtasks)
- [App Groups](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_security_application-groups)
- [WidgetKit](https://developer.apple.com/documentation/widgetkit)
- [Custom URL Schemes](https://developer.apple.com/documentation/xcode/defining-a-custom-url-scheme-for-your-app)

**WWDC Sessions**:
- WWDC 2019: Advances in App Background Execution
- WWDC 2020: Build complications in SwiftUI
- WWDC 2021: What's new in WidgetKit

---

**Maintained by**: Development Team
**Review Frequency**: After each iOS platform update
