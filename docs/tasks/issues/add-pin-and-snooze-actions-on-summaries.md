---
title: Add pin and snooze-until actions on summaries
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add pin and snooze-until actions on summaries #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have feature brainstorm (Discovery & Organization).

## Objective

Triage workflow: pin time-sensitive reads to the top, snooze others until evening / weekend / specific date. Pin and snooze are complementary — pinned items stay visible; snoozed items disappear from the main list until their due date.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New SQLDelight columns: `pinnedAt INTEGER`, `snoozedUntil INTEGER` on the summary table (migration via `sqldelight-migrations` skill).
- Swipe actions in `SummaryListScreen.kt` add Pin and Snooze (with a Frost time-bracket picker: Tonight / Tomorrow / This weekend / Pick…).
- List query: pinned first, then unread, then read; snoozed items hidden unless `Snoozed` chip filter active.
- New strings `swipeable_summary_pin`, `swipeable_summary_snooze`.

## Constraints

- Pure local state initially; sync via a feature-owned applier in a follow-up.
- Snoozed items re-appear automatically at due time (Android WorkManager periodic check, iOS BGAppRefreshTask).

## Definition of done

- Pin/snooze persists across app restarts.
- Snoozed-list filter chip shows hidden items.
- Snoozed item auto-restores at due time without needing the app foregrounded.
