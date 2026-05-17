---
title: Wire clipboard URL-detector banner onto SummaryListScreen
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire clipboard URL-detector banner onto SummaryListScreen #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Follow-up to `add-clipboard-url-detector-banner` (landed the `ClipboardProbe` interface in `core/common` with Android `ClipboardManager`, iOS `UIPasteboard.detectPatterns(.probableWebURL)` two-stage, and desktop AWT actuals — none of which prompt before the user accepts the banner).

## Objective

Bring up the actual "Submit URL" affordance: a one-tap banner above the summary list when the clipboard probe reports a URL that isn't already in the user's library.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- DI binding: register `ClipboardProbe` actuals (`AndroidClipboardProbe`, `IosClipboardProbe`, `DesktopClipboardProbe`) in their platform Koin modules.
- `SummaryListViewModel` subscribes to lifecycle resume and probes `hasUrl()` (no prompt). On true, push a `ClipboardSuggestion` UI state.
- New Frost banner composable above the list: `[ Submit <truncated-url> ]` + `[ Dismiss ]` brackets. Tapping Submit calls `readUrl()` (the only place the iOS prompt can fire) and routes to `SubmitURLScreen` with the URL prefilled.
- Dismissed URLs persist to `UserPreferences.recentlyDismissedClipboardUrls` (rolling window of, say, 16) so the banner does not re-prompt for the same URL during the same session.
- Skip the banner when the URL already exists in the user's library (cross-check against the summary table).

## Constraints

- iOS: `readUrl()` is the only call that may trigger the system "X pasted from Y" toast.
- Banner uses Frost atoms only — no Material chrome.

## Definition of done

- Banner appears on Android within ~200ms of resume when the clipboard contains a valid `https?://` URL.
- iOS shows the banner without triggering the system "X pasted from Y" toast until the user taps Submit.
- Dismissed URLs never re-prompt during the same app session.
