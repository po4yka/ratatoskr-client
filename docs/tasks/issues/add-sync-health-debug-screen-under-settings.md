---
title: Add sync-health debug screen under Settings
status: backlog
area: observability
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add sync-health debug screen under Settings #repo/ratatoskr-client #area/observability #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Observability).

## Objective

`feature/sync` already orchestrates the sync graph but exposes nothing to the user when things go wrong. Surface a debug view (debug builds + opt-in release) showing last sync timestamp, per-applier counts, retry counts, pending-op queue depth, last error per applier.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `SyncDebugScreen.kt` in `feature/settings/.../ui/screens/`.
- `SyncRepository` exposes a `StateFlow<SyncHealth>` consumed by the screen.
- Frost-styled table: applier name | last run | success/fail | last error.
- "Trigger sync now" button reuses existing `SyncDataUseCase`.

## Constraints

- Behind a debug flag in release builds (Settings → Help → Diagnostics → enable).
- No PII in error messages displayed (use category + correlation id, not raw payloads).

## Definition of done

- Screen renders live sync state, updating as sync runs.
- Manual sync trigger fires and updates the table.
- Available in release builds only after user enables diagnostics.
