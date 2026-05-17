---
title: Add iOS interactive widget mark-read
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add iOS interactive widget mark-read #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (iOS).

## Objective

`iosApp/RecentSummariesWidget/` shows a glanceable timeline today. iOS 17+ supports `Button(intent:)` for inline widget actions. Add a Mark-Read button per row to match the Android Glance interactive-widget feature.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `iosApp/RecentSummariesWidget/MarkReadIntent.swift` — `AppIntent` with `openAppWhenRun = false`.
- `RecentSummariesView.swift` rows wrap content with `Button(intent: MarkReadIntent(summaryId: …))` styled to Frost brackets.
- Intent writes to app-group storage; host app drains pending mutations on next foreground (same pattern as `checkForSharedURL`).

## Constraints

- Pure Frost styling — bracketed text button, INK on PAGE.
- Drift-safe — the host must reconcile pending widget mutations with backend state on next sync.

## Definition of done

- Tapping Mark-Read on the widget updates the summary state without launching the app.
- Host app reflects the change after next sync cycle.
- Widget re-renders within standard WidgetKit timeline budget.
