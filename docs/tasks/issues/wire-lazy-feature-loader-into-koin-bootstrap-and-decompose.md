---
title: Wire LazyFeatureLoader into KoinInitializer and Decompose component factories
status: backlog
area: kmp
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire LazyFeatureLoader into KoinInitializer and Decompose component factories #repo/ratatoskr-client #area/kmp #status/backlog 🔽

Follow-up to `convert-non-startup-koin-modules-to-lazy` (landed the durable atom: `core/common/.../util/di/LazyFeatureLoader.kt` with idempotent `ensureLoaded(FeatureKey)` semantics and 4 commonTest cases pinning load-once behavior, independent per-feature tracking, and snapshot immutability).

## Objective

Use the loader to actually defer module loads:

1. **`composeApp/.../di/KoinInitializer.kt`** — register `LazyFeatureLoader` as a `@Single`, bound to a `loadOperation` closure that calls `koin.loadModules(modulesFor(featureKey))`. The map of feature → modules holds the digest, collections, and settings module sets that were previously eager.
2. **Drop digest/collections/settings from `appModules()`** so they no longer load at startup. The eager set stays: auth, summary, sync, core/* (and the existing `*FeatureBindings` modules for auth, summary, sync).
3. **Decompose component factories for digest, collections, settings** — at the top of each factory's `create` function, call `lazyFeatureLoader.ensureLoaded(FeatureKey.DIGEST)` (etc.) so the module loads on first navigation. Component factories are themselves resolved from Koin, so they get the loader via constructor injection.
4. **Macrobenchmark coverage** — add a cold-start macrobenchmark gated on a flag so CI can detect regressions if a future change re-eagers a module.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `composeApp/.../di/KoinInitializer.kt` registers the loader as `@Single` and wires `loadOperation`.
- `composeApp/.../di/AppModules.kt` no longer includes `digest`, `collections`, `settings` in `appModules()`.
- `feature/digest/.../presentation/navigation/DigestComponentFactory.kt` (and equivalents for collections + settings) call `lazyFeatureLoader.ensureLoaded(...)` on first navigation.
- Macrobenchmark for cold start under `androidBenchmark/`.

## Constraints

- No behavior change to DI graph from a feature-user perspective — first navigation must resolve the same factories as before; subsequent navigations are no-op idempotent loads.
- Eager modules stay: auth, summary, sync, core/*. The Decompose root and the bootstrap path must not require any deferred module.

## Definition of done

- Macrobenchmark shows measurable cold-start improvement vs the eager-load baseline.
- All feature screens load correctly on first navigation (verify by manual smoke + the existing route tests once they land).
- No regression in feature-to-feature navigation timing.
