---
title: Add iOS App Intents for Shortcuts and Spotlight
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add iOS App Intents for Shortcuts and Spotlight #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (iOS).

## Objective

Surface Ratatoskr in Siri / Shortcuts ("Summarize this") and make existing summaries discoverable via Spotlight search. Both leverage Apple's App Intents (iOS 16+) and CoreSpotlight indexing.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `iosApp/iosApp/AppIntents/SubmitURLIntent.swift` — `AppIntent` accepting a URL parameter, writes to `AppGroupStore` (same path as Share Extension), opens via the existing `submit-url` deep link.
- New `iosApp/iosApp/AppIntents/RatatoskrShortcuts.swift` — `AppShortcutsProvider` declaring Spotlight-discoverable shortcuts.
- Extend the existing `refreshRecentSummariesWidget` path to also index recent summaries via `CSSearchableItem` so they appear in Spotlight search.

## Constraints

- No backend changes required.
- Honor user privacy: only index summaries the user has actually opened.

## Definition of done

- "Summarize URL" appears in iOS Shortcuts app and can be added to home screen.
- Spotlight search for a summary title surfaces the summary with deep-link.
- Siri "Summarize <pasted URL>" works end-to-end.
