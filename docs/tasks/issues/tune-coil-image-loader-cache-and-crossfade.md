---
title: Tune Coil image loader cache and crossfade
status: backlog
area: frontend
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Tune Coil image loader cache and crossfade #repo/ratatoskr-client #area/frontend #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Performance).

## Objective

`composeApp/src/commonMain/.../di/ImageLoaderModule.kt` uses Coil 3 defaults. Explicitly size the memory and disk caches and configure crossfade so list scrolling doesn't re-fetch proxied summary thumbnails.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `MemoryCache.Builder().maxSizePercent(0.25)`.
- `DiskCache.Builder().directory(...).maxSizeBytes(64.MiB)`.
- `crossfade(150)` default.
- Same tuning applied in `composeApp/src/androidMain/.../ImageLoaderFactory.kt`.

## Constraints

- Keep Frost zero-radius image rendering.
- No-op for desktop dev target where image fetching is rare.

## Definition of done

- Scrolling list of 50+ summaries with thumbnails shows zero repeat network requests.
- Cache size respects the 64 MiB cap.
