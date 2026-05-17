---
title: Wire SyncHealthSnapshot into SyncRepository and bring up SyncDebugScreen
status: backlog
area: observability
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire SyncHealthSnapshot into SyncRepository and bring up SyncDebugScreen #repo/ratatoskr-client #area/observability #status/backlog 🔼

Follow-up to `add-sync-health-debug-screen-under-settings` (landed the
durable testable atom: `feature/sync/.../domain/health/SyncHealthSnapshot.kt`
with `SyncErrorCategory` / `SyncErrorCategoryHint` / `SyncErrorSummary` /
`SyncApplierRow` value types, a `categorizeError(raw, correlationId, hint)`
PII-safe path from raw failures to displayable summaries, and a
`SyncApplierRow.render()` row contract. 9 commonTest cases cover hint
mapping, PII-leak regression guards, correlation id sanitization, the
`hasAnyFailure` aggregate, and the render column format.).

## Objective

Bring up the platform surfaces that consume the snapshot:

1. **`SyncRepository`** extension: `val health: StateFlow<SyncHealthSnapshot>`
   updated on every applier run. Each catch site in the sync graph calls
   `SyncHealthSnapshot.categorizeError(raw, correlationId, hint)` and pushes
   the row through the StateFlow.
2. **`feature/settings/.../ui/screens/SyncDebugScreen.kt`** — Frost-styled
   table consuming the StateFlow. Each row renders via the existing
   `SyncApplierRow.render()` contract. Top bar shows last-sync timestamp +
   pending-op depth. Bottom action: `BracketButton("Trigger sync now")` →
   `SyncDataUseCase.invoke()`.
3. **Release-build opt-in** — Settings → Help → Diagnostics → "Enable
   diagnostics" Frost switch. Persisted to `UserPreferences.diagnosticsEnabled`.
   The Sync health entry only appears when the switch is on; in debug
   builds it's always visible.
4. **Correlation id source** — each sync applier passes the existing trace
   id (from `Api.client` request hooks or a fresh UUID at retry boundary)
   so log grep across server + client uses the same id.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `feature/sync/.../domain/repository/SyncRepository.kt` — new `health` flow.
- `feature/sync/.../data/repository/SyncRepositoryImpl.kt` — updates
  `MutableStateFlow<SyncHealthSnapshot>` at every applier outcome.
- `feature/settings/.../ui/screens/SyncDebugScreen.kt` Frost table screen.
- `core/common/.../domain/UserPreferences.kt` — new `diagnosticsEnabled` field.
- Settings → Help section updated with the toggle and conditional entry.

## Constraints

- No PII in displayed errors — the atom enforces it; the caller must pass
  category + correlation id and never the raw exception message.
- Behind the diagnostics toggle in release builds.
- Manual sync trigger reuses existing `SyncDataUseCase` (do not duplicate).

## Definition of done

- Screen renders live sync state, updating as sync runs.
- Manual sync trigger fires and updates the table.
- Available in release builds only after the user enables diagnostics.
- Synthetic error injection lands as `NETWORK (trace-…)` with no message
  body leakage.
