---
title: Replace force-unwraps in shared Compose UI
status: backlog
area: frontend
priority: critical
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Replace force-unwraps in shared Compose UI #repo/ratatoskr-client #area/frontend #status/backlog 🔺

Filed from the 2026-05-17 deep audit (K2).

## Objective

Force-unwrap chains on independently-nullable UI state crash on recomposition races (state transition that nulls a field between the guard and the read). Particularly risky inside `core/ui/` because those components are shared across features.

Confirmed sites:

- `feature/digest/.../CustomDigestViewScreen.kt:117` — `state.digest!!.content!!`
- `feature/summary/.../SummaryDetailScreen.kt:249`, `:269`, `:853`
- `core/ui/.../InsightsSection.kt:97`
- `core/ui/.../components/SummaryCard.kt:61`
- `feature/collections/.../CollectionViewScreen.kt:788` — `collaborator.userId!!`

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Replace each `!!` with `?.let { ... }`, `?: return`, or a `when` guard that handles `null` explicitly.
- Where the state is structurally guaranteed non-null at the call site, prove it via a precondition `requireNotNull` outside the composable, or refactor the state class to enforce the invariant.

## Constraints

- No behavior change for the non-null path.
- Do not paper over the issue with `?: error("…")` — that just reorganizes the crash.

## Definition of done

- `rg "!!\b" core/ui feature/` returns zero matches in screen/component code.
- Manual smoke test on each affected screen verifies no NPE on rapid state transitions (e.g., navigating away mid-load).
