---
title: Add iOS Safari Action Extension
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add iOS Safari Action Extension #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (iOS).

## Objective

Safari Action Extensions live inside Safari's share menu (left tab) — one-tap summarize without going through the system share sheet. Mirrors the existing Share Extension's logic.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New iOS target `iosApp/SummarizeAction/` mirroring `iosApp/ShareExtension/` layout.
- `ActionViewController.swift`, `Info.plist` with `NSExtensionActivationSupportsWebURLWithMaxCount: 1`, entitlements file referencing the same app group as the Share Extension.
- Reuses `AppGroupStore.storeSharedURL` and `AppGroupContract.submitURLDeepLink()` — zero new client/server contracts.

## Constraints

- Identical app-group identifier; identical secure-storage rules.
- Must function without launching the host app (deep-link triggers host on next foreground).

## Definition of done

- Action Extension appears in Safari's share menu actions row.
- Tapping it submits the current Safari URL to Ratatoskr.
- Existing Share Extension still works unchanged.
