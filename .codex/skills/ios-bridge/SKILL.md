---
name: ios-bridge
description:
  Use when modifying SwiftUI host code in iosApp/, the CocoaPods
  ComposeApp framework export, KeychainSettings secure storage, the
  iOS share extension, the Recent Summaries widget, app-group
  identifiers, or deep-link routing on iOS. Trigger on changes under
  iosApp/iosApp/, iosApp/ShareExtension/, iosApp/RecentSummariesWidget/,
  Podfile / project.yml, or anywhere expect/actual iOS bindings live.
user-invocable: false
---

# iOS Bridge

`iosApp/` is the SwiftUI host around the `ComposeApp` framework
exported from `composeApp/` via CocoaPods. The bridge has three
moving parts that must agree on shared contracts: the main app, the
share extension, and the Recent Summaries widget.

## File layout

- `iosApp/iosApp.xcworkspace` — open this, not `iosApp.xcodeproj`,
  whenever CocoaPods integration matters.
- `iosApp/iosApp/` — SwiftUI host; app lifecycle in `iOSApp.swift`.
- `iosApp/ShareExtension/` — receives shared URLs and writes to the
  shared app group.
- `iosApp/RecentSummariesWidget/` — WidgetKit extension reading the
  shared app group.
- `iosApp/Podfile`, `iosApp/project.yml` — CocoaPods + XcodeGen
  configuration.

## Hard rules

- **Open the workspace, not the project.** The Pod-managed
  `ComposeApp.framework` is only visible through the workspace.
- **Three identifiers must stay in sync** across main app, share
  extension, and widget: app-group ID, keychain access group, and
  the deep-link URL scheme. If you change one, audit the other two.
- **Secure storage on iOS uses `KeychainSettings`**
  (com.russhwolf multiplatform-settings backed by the Keychain). Do
  not add a parallel UserDefaults-based secret store.
- **Networking uses the Darwin Ktor engine.** Don't add URLSession
  paths for shared HTTP calls — they bypass the bearer-refresh
  layer in `core/data/.../ApiClient.kt`.
- **SKIE is configured but currently disabled** in
  `composeApp/build.gradle.kts` because the active Kotlin version
  is ahead of supported SKIE versions. Don't write Swift code that
  assumes new SKIE-generated APIs are available.

## App startup + background sync

`iosApp/iosApp/iOSApp.swift` owns the iOS app lifecycle. Background
sync is registered there and routes into the same KMP sync
repository the Android app uses
(`feature/sync/.../data/repository/SyncRepositoryImpl.kt`). See the
`sync-orchestration` skill before changing sync wiring.

## Share extension contract

The share extension serializes the incoming URL into the shared app
group (a `UserDefaults` group container). The main app reads that
queue at launch and dispatches it through `AppCompositionRoot`'s
launch-action handling.

If you change the queue key, payload shape, or app-group ID, update
both ends:

- `iosApp/ShareExtension/` (the writer)
- The launch-action consumer in
  `composeApp/.../app/AppCompositionRoot.kt` (Darwin source set
  binding on the KMP side).

## Widget contract

`RecentSummariesWidget` reads its data set from the shared app group
(populated by the main app on each successful sync). It does **not**
hit the network — anything it shows must already be cached locally.

## Adding `expect`/`actual` for iOS

iOS actuals for KMP modules live in:

- `core/common/src/iosMain/`
- `core/data/src/iosMain/`
- `composeApp/src/iosMain/` (rare; only for app-shell concerns)

`core/data/src/iosMain/.../di/IosModule.kt` is a **valid Koin DSL
exception** — generated `.module` extensions are not visible from
`iosMain`, so DSL is the only option. Do not "fix" it.
