---
title: Add iOS Live Activity for summarization progress
status: backlog
area: frontend
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add iOS Live Activity for summarization progress #repo/ratatoskr-client #area/frontend #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (iOS).

## Objective

Long URL summarization can take 30s+. A Live Activity in the Dynamic Island shows "Summarizing…" with progress so the user can leave the app and return when done.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `iosApp/iosApp/LiveActivity/SummarizingActivityAttributes.swift` declaring `ActivityAttributes`.
- `ActivityConfiguration` registered in a widget-extension variant of `RecentSummariesWidget`.
- KMP host triggers `Activity.request(...)` on submit, updates state on completion or failure.

## Constraints

- Frost-styled Dynamic Island content — no Material chrome.
- Activity ends within 5s of completion to avoid staleness.
- No remote push tokens required — local timer-driven updates.

## Definition of done

- Submitting a URL spawns a Live Activity visible in the Dynamic Island.
- Activity dismisses on completion and deep-links to the new summary.
- Failure state shows red Spark accent and an error message.
