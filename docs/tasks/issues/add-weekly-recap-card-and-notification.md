---
title: Add weekly recap card and notification
status: backlog
area: content
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add weekly recap card and notification #repo/ratatoskr-client #area/content #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have feature brainstorm (Reading Progress).

## Objective

Drive habitual return visits by surfacing "this week you read N summaries, M minutes, top tags …". Display as a `BrutalistCard` titled "THIS WEEK" on `StatsScreen.kt`, and fire a notification Sunday 18:00 local time.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `WeeklyRecapState` derived from existing `UserStats` filtered to last 7 days.
- New Frost card on `StatsScreen.kt` rendering top-3 tags, total reading time, summary count, streak status.
- Periodic Android WorkManager `WeeklyRecapWorker` + iOS `BGAppRefreshTask` that posts a notification with the recap headline.
- Tap notification opens the recap card scrolled into view.

## Constraints

- Notification opt-in (existing channels framework).
- Aggregation done locally; no new backend endpoint.
- Sunday 18:00 timing respects user's local timezone.

## Definition of done

- Recap card renders on `StatsScreen` with this-week data.
- Notification fires on Sunday for opted-in users.
- Recap is empty-state friendly ("Read your first summary to start tracking").
