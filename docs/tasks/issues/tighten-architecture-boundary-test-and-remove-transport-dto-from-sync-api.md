---
title: Tighten architecture boundary test and remove transport DTOs from feature.sync.api
status: backlog
area: kmp
priority: critical
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Tighten architecture boundary test and remove transport DTOs from feature.sync.api #repo/ratatoskr-client #area/kmp #status/backlog 🔺

Filed from the 2026-05-17 deep audit (CC4 + K5).

## Objective

The architecture-boundary test only checks paths containing `/domain/` (`composeApp/src/androidHostTest/.../di/ArchitectureBoundaryTest.kt:32-37`), so it misses transport-type leaks through public `api/` packages: `feature/sync/.../feature/sync/api/SyncExtensions.kt:3` exports `data.remote.dto.SyncItemDto`, and `SyncItemApplier` (in the same public surface) takes `SyncItemDto` as input. Every consumer of "sync's public contract" therefore transitively depends on a transport DTO.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Introduce `SyncEntity` sealed class in `feature.sync.api`. Map `SyncItemDto → SyncEntity` once inside `SyncRepositoryImpl`.
- Change every `SyncItemApplier` signature (in `feature/sync`, `feature/summary/.../sync/`, `feature/collections/.../sync/`) to consume `SyncEntity` only.
- Broaden `ArchitectureBoundaryTest` to forbid `data.remote.*` imports from any path matching `*/api/*` or `*/domain/*` (drop the `/domain/`-only filter).
- Run the test on JVM/desktop too (not only `androidHostTest`).

## Constraints

- No serialization changes — transport DTOs remain in `data.remote.dto`, just no longer crossing the public-contract boundary.
- Sync wire format unchanged.

## Definition of done

- `ArchitectureBoundaryTest` fails if a transport DTO is referenced from any `api/` or `domain/` package.
- Sync still applies all known item types end-to-end in integration test (covered by the existing `add-syncrepositoryimpl-integration-tests` task).
- No `data.remote.dto.*` imports in `feature/*/api/` or any `domain/` package.
