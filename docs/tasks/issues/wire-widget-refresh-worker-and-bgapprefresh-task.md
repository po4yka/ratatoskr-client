---
title: Wire RefreshRecentSummariesUseCase into WidgetRefreshWorker + BGAppRefreshTask
status: backlog
area: sync
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire RefreshRecentSummariesUseCase into WidgetRefreshWorker + BGAppRefreshTask #repo/ratatoskr-client #area/sync #status/backlog 🔼

Follow-up to `tune-background-refresh-cadence-cross-platform` (landed the durable testable atom: `core/common/.../util/battery/BatteryStatus.kt` interface and `feature/sync/.../domain/usecase/RefreshRecentSummariesUseCase.kt` that gates a `sync(forceFull = false)` call on `BatteryStatus.isLow()`. 4 commonTest cases cover the battery-low short-circuit, the healthy-battery sync path, the failure-wrapping path, and prove that the battery check runs before the sync call.)

## Objective

Bring up the platform schedulers + platform `BatteryStatus` actuals that consume the use case:

1. **`BatteryStatus` platform actuals** in `core/common`:
   - Android: `Context.getSystemService(BatteryManager::class.java)` reading `BATTERY_PROPERTY_CAPACITY` (or the cached `ACTION_BATTERY_LOW` broadcast state).
   - iOS: `UIDevice.current.batteryLevel < 0.20 && batteryState != .charging`. Remember to enable `UIDevice.current.isBatteryMonitoringEnabled = true` at app bootstrap.
   - Desktop: always-false stub (development target only).
2. **Android `WidgetRefreshWorker`** under `androidApp/.../worker/` — a `CoroutineWorker` that invokes the use case once and writes the latest data via the existing widget broadcast pipeline. Schedule from `RatatoskrApp.onCreate` as `PeriodicWorkRequestBuilder<WidgetRefreshWorker>(repeatInterval = 30, TimeUnit.MINUTES, flexTimeInterval = 10, TimeUnit.MINUTES).setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build())`.
3. **iOS `BGAppRefreshTaskRequest`** registered in `iOSApp.registerBackgroundTasks` under a new identifier `com.po4yka.ratatoskr.refresh`. Add the identifier to `iosApp/iosApp/Info.plist`'s `BGTaskSchedulerPermittedIdentifiers` array (alongside the existing 6-hour `com.po4yka.ratatoskr.sync` entry).
4. **DI bindings** for `BatteryStatus` actuals in `AndroidModule.kt`, `IosModule.kt`, `DesktopModule.kt`.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `core/common/src/{androidMain,iosMain,desktopMain}/.../util/battery/BatteryStatus.kt` — actuals.
- `androidApp/src/main/kotlin/.../worker/WidgetRefreshWorker.kt` + scheduling in `RatatoskrApp.kt`.
- iOS `iOSApp.swift` registration block + `Info.plist` `BGTaskSchedulerPermittedIdentifiers` extension.
- DI bindings in the three platform Koin modules.

## Constraints

- Short refresh must not collide with the full 6-hour sync; both are independent jobs scheduled by separate identifiers.
- Battery-friendly: the use case already short-circuits on `BatteryStatus.isLow()`; the platform schedulers should additionally set the network constraint (Android `NetworkType.UNMETERED`) and the OS's own battery-aware constraints (`requiresBatteryNotLow = true` on WorkManager).
- Full 6h sync continues to fire on schedule — do not touch the existing `SyncWorker` / `BGProcessingTaskRequest` paths.

## Definition of done

- Widget on both platforms shows updates within 30 min of new content arriving.
- Battery use does not measurably regress (Android Battery Historian / Xcode Energy).
- Full 6h sync still fires on schedule.
