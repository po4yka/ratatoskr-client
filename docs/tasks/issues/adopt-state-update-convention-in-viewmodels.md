---
title: Adopt _state.update {} convention across all ViewModels
status: backlog
area: kmp
priority: critical
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Adopt _state.update {} convention across all ViewModels #repo/ratatoskr-client #area/kmp #status/backlog 🔺

Filed from the 2026-05-17 deep audit (cross-cutting finding CC5).

## Objective

Replace every `_state.value = _state.value.copy(...)` lost-update pattern with `_state.update { it.copy(...) }`. Concurrent coroutines in `AuthViewModel.kt:45-159`, `SummaryDetailViewModel.kt:74,103,111-123,374-378` (including the nested `viewModelScope.launch` at `:125-137` that races the minute-tick loop at `:61-68`) silently drop emissions today. `SummaryListViewModel` already follows the correct pattern — adopt it everywhere.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- All ViewModels in `feature/*/presentation/viewmodel/` use `_state.update { ... }` exclusively.
- `SummaryDetailViewModel.fetchFullContent` (`:125-137`) captures the originating `summaryId` and aborts the write when `_state.value.summary?.id != id`.
- A regression test demonstrating an interleaved double-write keeps both fields (no lost update).

## Constraints

- No behavior change for single-write paths.
- Cancellation contract preserved — do not swallow `CancellationException`.

## Definition of done

- `rg "_state\.value\s*=\s*_state\.value\.copy"` returns zero matches across `feature/*`.
- New ViewModel state-write test in at least one feature module proves `update {}` semantics under contention.
- Reviewer pass confirms no remaining `value = value.copy` reads anywhere in the repo.
