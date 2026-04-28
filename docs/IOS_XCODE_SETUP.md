# iOS Share Extension & Background Tasks - Xcode Setup Guide

**Status**: All code files created  | Requires Xcode project configuration

This guide walks you through configuring the Xcode project to enable Share Extension and Background Tasks.

---

## Overview

All Swift code files have been created and are ready to use:

### Created Files

1. **Share Extension**:
   - `iosApp/ShareExtension/ShareViewController.swift` (76 LOC)
   - `iosApp/ShareExtension/Info.plist`
   - `iosApp/ShareExtension/Base.lproj/MainInterface.storyboard`

2. **Main App Updates**:
   - `iosApp/iosApp/iOSApp.swift` (Updated with background tasks + shared URL handling)

### What Needs Xcode Configuration

1. Create Share Extension target
2. Enable App Groups capability
3. Enable Background Modes capability
4. Add files to correct targets
5. Configure Info.plist entries

**Estimated Time**: 15-20 minutes

---

## Part 1: Create Share Extension Target

### Step 1: Add Share Extension Target

1. Open `iosApp.xcodeproj` in Xcode
2. Click on the project in the navigator (top-level "iosApp")
3. Click the **+ button** at the bottom of the targets list
4. Select **Share Extension** template
5. Configure:
   - **Product Name**: `ShareExtension`
   - **Team**: Your development team
   - **Organization Identifier**: `com.po4yka.ratatoskr`
   - **Bundle Identifier**: `com.po4yka.ratatoskr.ShareExtension`
   - **Language**: Swift
   - **Activate scheme**: Click "Cancel" (we'll activate later)

### Step 2: Replace Auto-Generated Files

Xcode created some files automatically. Replace them with our versions:

1. **Delete** these auto-generated files from `ShareExtension` folder in Xcode:
   - `ShareViewController.swift`
   - `Info.plist`
   - `MainInterface.storyboard`

2. **Add** our files to the ShareExtension target:
   - In Xcode, right-click on `ShareExtension` folder
   - Select **Add Files to "iosApp"**
   - Navigate to `iosApp/ShareExtension/`
   - Select all 3 files:
     - `ShareViewController.swift`
     - `Info.plist`
     - `Base.lproj/MainInterface.storyboard`
   - **Important**: Check **only** "ShareExtension" in **Target Membership**
   - Click **Add**

### Step 3: Verify Bundle Identifier

1. Select **ShareExtension** target
2. Go to **General** tab
3. Verify **Bundle Identifier** is: `com.po4yka.ratatoskr.ShareExtension`
4. Set **Deployment Target** to iOS 15.0 (or match main app)

---

## Part 2: Configure App Groups

App Groups allow the main app and Share Extension to share data.

### Step 1: Enable App Groups for Main App

1. Select **iosApp** target (main app)
2. Go to **Signing & Capabilities** tab
3. Click **+ Capability**
4. Select **App Groups**
5. Click **+ button** to add a new group
6. Enter group ID: `group.com.po4yka.ratatoskr`
7. Check the checkbox next to the group

### Step 2: Enable App Groups for Share Extension

1. Select **ShareExtension** target
2. Go to **Signing & Capabilities** tab
3. Click **+ Capability**
4. Select **App Groups**
5. Enable the **same group**: `group.com.po4yka.ratatoskr`
6. Check the checkbox

### Step 3: Verify App Group Configuration

Both targets should now show `group.com.po4yka.ratatoskr` in their App Groups.

---

## Part 3: Configure Background Tasks

### Step 1: Enable Background Modes for Main App

1. Select **iosApp** target (main app)
2. Go to **Signing & Capabilities** tab
3. Click **+ Capability**
4. Select **Background Modes**
5. Enable these checkboxes:
   -  **Background fetch**
   -  **Background processing**

### Step 2: Register Background Task Identifier

1. Select **iosApp** target
2. Go to **Info** tab
3. Click **+ button** to add a new entry
4. Add the following:
   - **Key**: `BGTaskSchedulerPermittedIdentifiers`
   - **Type**: Array
5. Add item to array:
   - **Item 0**: `com.po4yka.ratatoskr.sync` (String)

**Alternative**: Edit `Info.plist` directly and add:

```xml
<key>BGTaskSchedulerPermittedIdentifiers</key>
<array>
    <string>com.po4yka.ratatoskr.sync</string>
</array>
```

---

## Part 4: Update Info.plist (Main App)

Ensure the main app's Info.plist has these entries:

### Required Entries

1. **App Group** (Added automatically by Xcode capability)

2. **Background Task Identifiers** (Added in Part 3, Step 2)

3. **URL Scheme** (if not already present, for Telegram auth):
```xml
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>ratatoskr</string>
        </array>
        <key>CFBundleURLName</key>
        <string>com.po4yka.ratatoskr</string>
    </dict>
</array>
```

---

## Part 5: Build and Test

### Step 1: Build All Targets

1. **Build Main App**:
   - Select scheme: **iosApp**
   - Product → Build (⌘B)
   - Fix any build errors

2. **Build Share Extension**:
   - Select scheme: **ShareExtension**
   - Product → Build (⌘B)
   - Fix any build errors

### Step 2: Run on Device (Recommended)

Share Extension and Background Tasks work best on real devices, not simulator.

1. Connect an iOS device
2. Select device in Xcode
3. Select **iosApp** scheme
4. Click **Run** ()
5. App should install and launch

### Step 3: Test Share Extension

1. Open **Safari** on the device
2. Navigate to any webpage (e.g., https://techcrunch.com/article)
3. Tap **Share button** (square with arrow)
4. Scroll down and tap **More** if "Ratatoskr" isn't visible
5. Enable **ShareExtension** toggle
6. Tap **Done**
7. Now tap **Share** again
8. Tap **ShareExtension**
9. App should open with URL prefilled!

---

## Part 6: Test Background Tasks

Background tasks only run on real devices (not simulator).

### Testing with Xcode Debugger

1. **Run app on device**
2. Put app in background (home button)
3. In Xcode, pause execution (⏸)
4. In **LLDB console**, paste:

```bash
e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateLaunchForTaskWithIdentifier:@"com.po4yka.ratatoskr.sync"]
```

5. Resume execution ()
6. Check console output for:
   - `[BackgroundTasks] Background sync task started`
   - `[BackgroundTasks] Background sync completed successfully`

### Testing Expiration

```bash
e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateExpirationForTaskWithIdentifier:@"com.po4yka.ratatoskr.sync"]
```

---

## Troubleshooting

### Share Extension Not Appearing

**Problem**: "ShareExtension" doesn't appear in share sheet

**Solutions**:
1. Verify ShareExtension is installed (check Settings → General → Extensions)
2. Rebuild and reinstall the app
3. Check App Group is enabled on both targets
4. Verify Info.plist activation rules in ShareExtension

### Shared URL Not Opening App

**Problem**: URL is shared but app doesn't open

**Solutions**:
1. Verify App Group name matches exactly: `group.com.po4yka.ratatoskr`
2. Check console logs for errors
3. Ensure both targets have the capability enabled

### Background Tasks Not Running

**Problem**: Background sync never executes

**Solutions**:
1. Verify you're testing on a real device (not simulator)
2. Check Background Modes are enabled
3. Verify task identifier in Info.plist matches code
4. Use Xcode debugger commands to force execution
5. Check console for scheduling errors

### Build Errors

**Problem**: Compilation fails

**Common Issues**:
1. **Missing import**: Ensure `import BackgroundTasks` is in iOSApp.swift
2. **Target membership**: Verify files are in correct targets
3. **Deployment target**: Match across all targets (iOS 15.0+)
4. **Signing**: Ensure all targets have valid signing

---

## Verification Checklist

Before considering setup complete, verify:

### Main App
- [ ] App Groups capability enabled
- [ ] Background Modes capability enabled (fetch + processing)
- [ ] BGTaskSchedulerPermittedIdentifiers in Info.plist
- [ ] iOSApp.swift imports BackgroundTasks
- [ ] App builds without errors

### Share Extension
- [ ] Share Extension target exists
- [ ] Our ShareViewController.swift is in target
- [ ] Our Info.plist is in target
- [ ] Our MainInterface.storyboard is in target
- [ ] App Groups capability enabled with same group
- [ ] Bundle ID: com.po4yka.ratatoskr.ShareExtension
- [ ] Extension builds without errors

### Functionality
- [ ] Main app runs on device
- [ ] Share Extension appears in Safari share sheet
- [ ] Sharing URL opens app with URL prefilled
- [ ] Background task can be triggered with debugger
- [ ] Console logs show correct messages

---

## File Structure After Setup

```
iosApp/
 iosApp/
    iOSApp.swift ( UPDATED - Background tasks + shared URL)
    ContentView.swift
    Info.plist ( UPDATED - BGTaskSchedulerPermittedIdentifiers)
    Views/
    ViewModels/
    ...

 ShareExtension/ ( NEW TARGET)
    ShareViewController.swift ( CREATED)
    Info.plist ( CREATED)
    Base.lproj/
        MainInterface.storyboard ( CREATED)

 iosApp.xcodeproj/ ( MODIFIED in Xcode)
     project.pbxproj (targets, capabilities, etc.)
```

---

## Next Steps After Setup

1. **Test thoroughly** on a real device
2. **Test with different apps** (Safari, Chrome, Twitter, etc.)
3. **Monitor background sync** in real-world usage
4. **Update app store description** to mention sharing feature
5. **Create user documentation** about sharing URLs

---

## Additional Resources

- [Apple: App Extensions](https://developer.apple.com/app-extensions/)
- [Apple: Share Extension](https://developer.apple.com/documentation/uikit/share_extension)
- [Apple: Background Tasks](https://developer.apple.com/documentation/backgroundtasks)
- [Apple: App Groups](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_security_application-groups)

---

## Summary

### What We Did

 Created all Swift code files for Share Extension
 Created all Swift code for Background Tasks
 Updated main app to handle shared URLs
 Updated main app with background sync scheduling

### What You Need to Do in Xcode

1. Create Share Extension target (5 min)
2. Add our files to the target (2 min)
3. Enable App Groups on both targets (3 min)
4. Enable Background Modes on main app (2 min)
5. Add BGTaskSchedulerPermittedIdentifiers to Info.plist (2 min)
6. Build and test (5 min)

**Total**: 15-20 minutes

After following this guide, iOS will have full parity with Android for platform features!

---

**Created**: 2025-11-17
**Status**: Ready for Xcode configuration
**Maintained by**: Development Team
