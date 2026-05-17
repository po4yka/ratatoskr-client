---
title: Wire WidgetRefreshCadence into WidgetRefreshWorker and BGAppRefreshTask
status: backlog
area: sync
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire WidgetRefreshCadence into WidgetRefreshWorker and BGAppRefreshTask #repo/ratatoskr-client #area/sync #status/backlog 🔼

Follow-up to `wire-widget-refresh-worker-and-bgapprefresh-task` (landed the
durable testable atom: `feature/sync/.../domain/usecase/WidgetRefreshCadence.kt`
with `decide(isBatteryLow, networkClass, lastSuccessAt, now, policy)` returning
`Allow` / `Defer(reason)` over precedence BATTERY_LOW → OFFLINE →
METERED_BLOCKED → RECENTLY_REFRESHED, `MIN_INTERVAL_MINUTES = 25` cadence
boundary using `>=`, clock-skew tolerance (future lastSuccessAt → fresh).
10 commonTest cases pin every branch.).

## Objective

Bring up the platform schedulers + network probe + storage that consume the
cadence atom:

1. **Network probe** in `core/common/.../util/network/`:
   - Android: `ConnectivityManager.getNetworkCapabilities(...)` mapping
     `NET_CAPABILITY_NOT_METERED` and presence of `NET_CAPABILITY_INTERNET`
     into `NetworkClass.{UNMETERED, METERED, OFFLINE}`.
   - iOS: `NWPathMonitor` reporting `path.isExpensive` for metered + connection
     status for offline.
   - Desktop: always `UNMETERED` (development target).
2. **Android `WidgetRefreshWorker`** under `androidApp/.../worker/`:
   `CoroutineWorker` that constructs the cadence inputs (battery + network +
   lastSuccessAt from a new `UserPreferences.lastWidgetRefreshAt` field +
   `UserPreferences.widgetRefreshPolicy`), calls `WidgetRefreshCadence.decide`,
   and routes through `RefreshRecentSummariesUseCase` on `Allow`. Schedule from
   `RatatoskrApp.onCreate` as a 30-min `PeriodicWorkRequestBuilder` with
   10-min flex, `NetworkType.CONNECTED`, `requiresBatteryNotLow = true`.
3. **iOS `BGAppRefreshTask`** registered in `iOSApp.registerBackgroundTasks`
   under `com.po4yka.ratatoskr.refresh`. Same cadence call. Add the identifier
   to `iosApp/iosApp/Info.plist`'s `BGTaskSchedulerPermittedIdentifiers`.
4. **DI bindings** for the new `NetworkClassProbe` actuals in
   `AndroidModule.kt`, `IosModule.kt`, `DesktopModule.kt`.
5. **Settings → Sync → Widget refresh** picker (`BracketSelector` over
   `NetworkPolicy.UNMETERED_ONLY` / `ANY_NETWORK`) backed by
   `UserPreferences.widgetRefreshPolicy`. Default `UNMETERED_ONLY`.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `core/common/.../util/network/NetworkClassProbe.kt` interface + actuals.
- `androidApp/.../worker/WidgetRefreshWorker.kt` + scheduling in
  `RatatoskrApp.kt`.
- iOS `iOSApp.swift` registration block + `Info.plist` permitted identifier.
- DI bindings in the three platform modules.
- `UserPreferences.lastWidgetRefreshAt` + `widgetRefreshPolicy` fields.
- Settings UI entry.

## Constraints

- Cadence atom is the source of truth; the OS-level WorkManager / BGTask
  constraints are belt-and-suspenders, not duplicate logic.
- Full 6h `SyncWorker` continues to fire independently — do not collide.
- Battery-friendly: cadence already short-circuits on `isBatteryLow`; the
  scheduler `requiresBatteryNotLow = true` is the second line of defense.

## Definition of done

- Widget on both platforms shows updates within 30 min of new content arriving.
- Battery use does not measurably regress (Android Battery Historian / Xcode
  Energy).
- Full 6h sync still fires on schedule.
- A metered-network device with policy = UNMETERED_ONLY shows zero refresh
  calls until the user reconnects to Wi-Fi.
