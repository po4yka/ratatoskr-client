---
name: sync-orchestration
description:
  Use when modifying the session-based sync layer in feature/sync,
  adding new sync item types, registering pending-operation handlers,
  injecting feature-owned appliers, or debugging sync conflicts /
  drift. feature/sync owns orchestration only; the appliers and
  pending-operation handlers live in the owning feature modules and
  are injected through the SyncItemApplierRegistry and
  PendingOperationFlusher. Trigger on changes to feature/sync/**,
  any feature's data/sync/ package, or the sync DI wiring.
user-invocable: false
---

# Sync Orchestration

Sync is session-based and lives in
`feature/sync/.../data/repository/`. The critical design rule:

> **`feature/sync` owns orchestration only.** It pulls server
> changes, walks pending local operations, and resolves conflicts in
> a type-generic way. It does **not** know how to apply a `Summary`
> change or a `Collection` change. Those appliers and pending-
> operation handlers are owned by the relevant feature module and
> registered into the sync layer at DI time.

## Where each piece lives

Inside `feature/sync/.../data/repository/`:

- `SyncRepositoryImpl.kt` — public entry point used by use cases.
- `SyncSessionCoordinator.kt` — drives one session end-to-end.
- `SyncItemApplierRegistry.kt` — type-keyed lookup of feature-owned
  appliers.
- `PendingOperationFlusher.kt` — drains queued local mutations
  against the backend via feature-owned handlers.
- `SyncHelpers.kt`, `SyncEnvelopeMapper.kt`, `SyncMapper.kt` —
  internal transport-layer plumbing.

In the owning feature (examples):

- `feature/summary/.../data/sync/SummaryPendingOperationHandlers.kt`
- `feature/collections/.../data/sync/...`
- Feature appliers wire themselves in via the feature's DI module
  (e.g. `feature/summary/.../di/SummaryFeatureBindings.kt`) with
  `@Single(binds = [SyncItemApplier::class, ...])`.

## Hard rules

- **Never put feature-specific logic in `feature/sync`.** If you
  find yourself writing `if (item is SummaryDelta)` inside sync
  code, stop. Dispatch is type-keyed through
  `SyncItemApplierRegistry`; the applier itself is owned by the
  feature.
- **Don't bypass sync to push local mutations.** Repository mutations
  on the owning feature go through a local write + pending-operation
  enqueue. The next sync cycle drains the queue.
- **Idempotent appliers.** An applier may receive the same delta
  twice (network retry, recovery, replay). Writes must be safe to
  repeat.
- **Conflict resolution lives at the orchestrator, not the
  applier.** If conflict semantics need to change, that's a sync-
  layer change, not a per-feature change.

## Adding a new synced resource type

1. Define the delta DTO and the pending-operation envelope in the
   owning feature's `data/remote/dto/` and `data/sync/` packages.
2. Implement an applier in the owning feature's `data/sync/` package
   that turns a delta into local writes via the feature's
   repository. Bind it with `@Single(binds = [SyncItemApplier::class])`.
3. Implement the corresponding pending-operation handler(s) for
   queued local mutations. Bind them so
   `PendingOperationFlusher` can route to them by operation type.
4. If the resource introduces a new local table or column, follow
   the `sqldelight-migrations` skill for the schema change.

## Debugging drift

When a device shows stale or missing data after sync:

1. Confirm the applier ran by tracing through
   `SyncSessionCoordinator` and the relevant feature applier.
2. Confirm `PendingOperationFlusher` isn't repeatedly failing
   against the backend — a stuck head blocks the queue.
3. Check whether the server is sending the expected delta. If the
   shape changed, it's a contract issue — see the
   `openapi-spec-bump` skill.
4. Tests under
   `feature/sync/src/commonTest/.../SyncItemApplierRegistryTest.kt`
   and `PendingOperationRoutingTest.kt` are good regression
   templates.
