---
title: Add continue-reading shelf on home
status: backlog
area: frontend
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add continue-reading shelf on home #repo/ratatoskr-client #area/frontend #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have feature brainstorm (Reading Progress).

## Objective

`Summary.lastReadPosition` is already tracked. Surface a horizontal "Continue" strip at the top of `SummaryListScreen.kt` showing summaries with `lastReadPosition > 0 && !isRead`, sorted by recency. Tapping deep-links into the detail screen at the saved scroll position.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `ContinueReadingShelf` composable using `RowDigest` Frost atom.
- Query in `SummaryListViewModel` populated as a `Flow<List<Summary>>`.
- Hidden when empty (no awkward "Nothing to continue" empty state).

## Constraints

- Shelf is at most 8 items; older partial reads silently drop off.
- Tap restores scroll position via existing reading-position machinery.

## Definition of done

- Strip renders only when at least one in-progress summary exists.
- Tap opens detail at the correct scroll offset.
