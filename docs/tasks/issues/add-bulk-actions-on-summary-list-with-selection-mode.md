---
title: Add bulk actions on summary list with selection mode
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add bulk actions on summary list with selection mode #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have feature brainstorm (Discovery & Organization).

## Objective

Triaging a 50-item backlog item-by-item is friction. Long-press a summary to enter selection mode; show a Frost bracket toolbar with bulk Archive / Mark read / Add to collection / Delete.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `selectedIds: Set<String>` in `SummaryListState`.
- `SelectionAppBar` Compose component (Frost bracket toolbar) replacing the normal app bar when non-empty.
- Wire to existing `SummaryListActionHandler` methods, batched.
- Back / Esc clears selection.

## Constraints

- Selection state cleared on screen navigation away (don't surprise the user).
- Concurrent bulk ops use `_state.update {}` (see active task `adopt-state-update-convention-in-viewmodels`).
- TalkBack/VoiceOver announces selection count changes.

## Definition of done

- Long-press starts selection; tap toggles items.
- Bulk archive of 10+ items finishes within 1s on mid-range Android.
- Accessibility announcements via `liveRegion` on selection-count changes.
