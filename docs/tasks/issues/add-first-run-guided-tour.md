---
title: Add first-run guided tour
status: backlog
area: frontend
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add first-run guided tour #repo/ratatoskr-client #area/frontend #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have feature brainstorm (Onboarding).

## Objective

A brand-new user lands on an empty `SummaryListScreen` with no obvious "what now". A 3-step Frost overlay introduces: (1) submit a URL via the FAB, (2) share into the app from any browser, (3) pin the widget to your home screen.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `OnboardingTour` overlay component using `FrostScaffold` and `BracketButton` "Next" / "Skip".
- Stored as `tour_completed: Boolean` in `UserPreferences`; default false.
- Triggered on first foreground after auth completes.
- Skippable; "Show again" entry in Settings → Help.

## Constraints

- Pure Frost styling (no Material 3 modal coachmarks).
- Cannot block deep-link entry (if user enters via share intent, tour is deferred to next plain launch).

## Definition of done

- Fresh install + first sign-in shows the tour exactly once.
- "Skip" hides forever (until manually re-triggered).
- Tour can be reset from Settings → Help → Replay tour.
