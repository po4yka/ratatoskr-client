---
title: Tune background refresh cadence cross-platform
status: backlog
area: sync
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Tune background refresh cadence cross-platform #repo/ratatoskr-client #area/sync #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (cross-platform).

## Objective

Today iOS registers a 6-hour `BGProcessingTaskRequest` and Android `SyncWorker` runs periodically with a wider window. Add a short-cadence top-up so the widget stays fresh: 30-min `BGAppRefreshTask` on iOS, 30-min flex `PeriodicWorkRequest` on Android. Long task remains the deep 6-hour sync.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- iOS: register a second identifier `com.po4yka.ratatoskr.refresh` as `BGAppRefreshTaskRequest` in `iOSApp.registerBackgroundTasks`; add to `Info.plist`'s `BGTaskSchedulerPermittedIdentifiers`.
- Android: alongside `SyncWorker`, register a new `WidgetRefreshWorker` with `PeriodicWorkRequest(repeatInterval = 30.min, flexTimeInterval = 10.min)` and `NetworkType.UNMETERED`.
- New use case `RefreshRecentSummariesUseCase(limit = 3)` consumed by both.

## Constraints

- Short refresh is cheap (3 newest summaries only) — must not collide with full sync.
- Battery-friendly: skip if `BatteryManager.BATTERY_LOW`.

## Definition of done

- Widget on both platforms shows updates within 30 min of new content arriving.
- Battery use does not measurably regress (Android Battery Historian / Xcode Energy).
- Full 6h sync still fires on schedule.
