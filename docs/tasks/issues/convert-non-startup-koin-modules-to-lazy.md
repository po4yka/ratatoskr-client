---
title: Convert non-startup Koin modules to lazy
status: backlog
area: kmp
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Convert non-startup Koin modules to lazy #repo/ratatoskr-client #area/kmp #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Performance).

## Objective

Cold start resolves the entire DI graph — including features the user hasn't navigated to yet. Convert `digest`, `collections`, `settings` modules to Koin `lazyModule { }` and load them on first navigation.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Convert non-critical feature modules in `composeApp/.../di/AppModules.kt` to `lazyModule`.
- `KoinInitializer.kt` calls `koinApplication.lazyModules(...)` after first composition.
- Eager modules stay: `auth`, `summary`, `sync`, core/*.

## Constraints

- No behavior change beyond startup timing.
- Decompose component factories must still resolve correctly when their feature is first navigated to.

## Definition of done

- Cold start time (Macrobenchmark) shows measurable improvement.
- All feature screens load correctly on first navigation.
