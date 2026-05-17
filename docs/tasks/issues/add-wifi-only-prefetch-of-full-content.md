---
title: Add Wi-Fi-only prefetch of full content
status: backlog
area: sync
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Wi-Fi-only prefetch of full content #repo/ratatoskr-client #area/sync #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have feature brainstorm (Sync & Offline).

## Objective

Today only TLDR is synced eagerly; `fullContent` is fetched on first detail open. Subway / airplane readers want everything ready offline. Add a user-controllable prefetch policy and a Wi-Fi-aware worker.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `UserPreferences.prefetchPolicy: { OFF, WIFI_ONLY, ALWAYS }` (default `OFF`).
- Settings entry under sync section (`SettingsScreen.kt`).
- Android: `PeriodicWorkRequest` with `NetworkType.UNMETERED` constraint when `WIFI_ONLY`.
- iOS: `URLSessionConfiguration.allowsCellularAccess = false` when `WIFI_ONLY`.
- Worker pulls full content for the N newest unread summaries (configurable, default 25).

## Constraints

- Respect user data plan: never prefetch on metered connections in `WIFI_ONLY` mode.
- Cap total prefetch download to ~50MB / cycle.
- Re-prefetch deletes stale entries (keep cache lean).

## Definition of done

- With `WIFI_ONLY` enabled, mobile-data traffic shows zero prefetch bytes; Wi-Fi traffic shows prefetch happening.
- Prefetched summaries open detail with zero network requests.
- Cache pruning prevents unbounded growth.
