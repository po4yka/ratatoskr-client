---
title: Add SyncRepositoryImpl integration tests and offline pending-op drain coverage
status: backlog
area: sync
priority: high
owner: Senior Feature Module Engineer (Ratatoskr Client)
paperclip: POY-275
blocks: []
blocked_by: []
created: 2026-05-12
updated: 2026-05-17
---

- [ ] #task Add SyncRepositoryImpl integration tests and offline pending-op drain coverage #repo/ratatoskr-client #area/sync #status/backlog ⏫ [paperclip:POY-275]

Filed from [POY-255](/POY/issues/POY-255) QA gate (rows C12 and C14).

## Objective

Cover SyncRepositoryImpl end-to-end against a fake KtorSyncApi + SQLDelight in-memory database: bootstrap session, full sync resume from cursor, delta sync apply, idempotent re-apply of already-applied items, conflict count surfaced from PendingOperation handlers, and offline pending-op drain when the network returns.

## Owner

Senior Feature Module Engineer (Ratatoskr Client). Coordinate with KMP for shared fakes.

## Expected artifact

- New tests under feature/sync/src/commonTest/kotlin/com/po4yka/ratatoskr/data/repository/SyncRepositoryImplTest.kt.
- Cases: full sync first run; full sync resume with cursor; delta after full; apply with conflict; idempotent re-apply; pending op drained on next session; cleanup decision matches FullSyncCleanupDecisionTest contract.
- Use existing SyncItemApplierRegistry + PendingOperationRouting test fakes; do not introduce new prod dependencies.
- Run via: ./gradlew :feature:sync:allTests

## Definition of done

- Tests pass on every active source set (commonTest + iosSimulatorArm64Test).
- Tests fail when SyncRepositoryImpl regresses on cursor resume, idempotency, or conflict surfacing.
