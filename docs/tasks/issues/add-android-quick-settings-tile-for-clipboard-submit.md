---
title: Add Android Quick Settings tile for clipboard submit
status: backlog
area: frontend
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Android Quick Settings tile for clipboard submit #repo/ratatoskr-client #area/frontend #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (Android).

## Objective

Two-tap capture from any app: swipe down the status bar, tap "Submit to Ratatoskr", and the current clipboard URL is sent for summarization.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `androidApp/.../tile/SubmitUrlTileService.kt` extending `TileService` (Android 7+).
- Manifest service entry with `android.service.quicksettings.action.QS_TILE` intent filter.
- Reads primary clipboard, validates against the same `httpUrlRegex` used by `MainActivity.toLaunchAction()`.
- Launches `MainActivity` via `PendingIntent.getActivity` with `AppLaunchAction.SubmitUrl(prefilledUrl=…)`.

## Constraints

- Tile shows disabled state when clipboard is empty or non-URL.
- Tile must function while device is locked (use `startActivityAndCollapse`).
- No background work — capture happens via existing app submission flow.

## Definition of done

- Tile installable via long-press on the QS panel.
- Tap with a clipboard URL submits successfully.
- Tap with no/invalid clipboard shows a Frost-styled toast.
