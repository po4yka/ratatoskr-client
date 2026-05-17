---
title: Move SyncSharedDtos out of core/data into feature/sync
status: backlog
area: sync
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by:
  - tighten-architecture-boundary-test-and-remove-transport-dto-from-sync-api
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Move SyncSharedDtos out of core/data into feature/sync #repo/ratatoskr-client #area/sync #status/backlog ⏫

Filed from the 2026-05-17 deep audit (K6).

## Objective

`core/data/src/commonMain/kotlin/com/po4yka/ratatoskr/data/remote/dto/SyncSharedDtos.kt:1-30` defines `SyncItemDto`, `SyncTagDto`, `SyncSummaryTagDto`, etc. — none of them referenced inside `core/data` itself. Their only consumers live in `feature/sync`, `feature/summary`, `feature/collections`. This pushes `core/data` toward a god-module and forces every feature to import `core/data` for transport types that aren't part of its mandate.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Move `SyncSharedDtos.kt` to `feature/sync/src/commonMain/kotlin/com/po4yka/ratatoskr/feature/sync/data/remote/dto/`.
- Update imports in `feature/summary` and `feature/collections` sync appliers.
- After the migration cleanup PR lands, evaluate whether these DTOs can be replaced by `core/api-generated` `SyncItem*` models instead and deleted.

## Constraints

- Must land after `tighten-architecture-boundary-test-and-remove-transport-dto-from-sync-api` (which removes these DTOs from `feature.sync.api` public surface first).
- No wire-format changes.

## Definition of done

- `core/data/.../data/remote/dto/SyncSharedDtos.kt` no longer exists.
- All sync appliers compile and pass tests.
- `core/data` no longer ships feature-specific transport types.
