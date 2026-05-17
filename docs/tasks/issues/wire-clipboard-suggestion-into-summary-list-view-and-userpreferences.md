---
title: Wire ClipboardSuggestionResolver into SummaryListScreen and persist dismissals
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire ClipboardSuggestionResolver into SummaryListScreen and persist dismissals #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Follow-up to `wire-clipboard-url-detector-banner-on-summary-list` (landed the `ClipboardSuggestionResolver` decision atom: pure function that takes `hasClipboardUrl` + `urlIfKnown` + `recentlyDismissed` + `libraryContains` and returns `Show(urlIfKnown)` / `Hide(reason)`; plus the `appendDismissed` rolling-window helper bounded at 16 entries. 9 commonTest cases cover the iOS unknown-URL path, the Android known-URL path, library-vs-dismissal precedence, dedup, and oldest-eviction).

## Objective

Bring up the actual banner UX on the summary list:

1. **`SummaryListViewModel`** subscribes to the activity-lifecycle resume signal and probes `ClipboardProbe.hasUrl()` (Android can also pre-fetch `readUrl()` without a prompt). On the resume tick it calls `ClipboardSuggestionResolver.resolve(...)` with the current `UserPreferences.recentlyDismissedClipboardUrls` and a `SummaryRepository.exists(url)` lookup, and pushes the `Show`/`Hide` result onto a new `clipboardSuggestion: ClipboardSuggestion?` field on the list state.
2. **Frost banner composable** above the list — `[ Submit <truncated-url> ]` brackets + `[ Dismiss ]` brackets. On iOS, `Show.urlIfKnown == null` so render `[ Submit URL from clipboard ]` instead of truncating.
3. **Submit handler** calls `probe.readUrl()` (the only path that may trigger the iOS paste prompt) and routes to `SubmitURLScreen` with the URL prefilled.
4. **Dismiss handler** calls `appendDismissed(prev, url)`, writes the result to `UserPreferences.recentlyDismissedClipboardUrls`, and clears the suggestion from state.
5. **DI bindings** for the existing `ClipboardProbe` actuals (`AndroidClipboardProbe`, `IosClipboardProbe`, `DesktopClipboardProbe`) in their platform Koin modules.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `UserPreferences.recentlyDismissedClipboardUrls: List<String>` field with a default empty list.
- DI: `AndroidClipboardProbe`, `IosClipboardProbe`, `DesktopClipboardProbe` registered as `@Single(binds = [ClipboardProbe::class])`.
- `feature/summary/.../presentation/viewmodel/SummaryListViewModel.kt` subscribes to lifecycle resume and pushes a `ClipboardSuggestion` state on resolve.
- `feature/summary/.../feature/summary/ui/components/ClipboardSuggestionBanner.kt` — Frost banner using `BracketButton` only.
- Wire above the `SummaryListScreen` lazy list, scoping the banner to the top.

## Constraints

- iOS: `readUrl()` is the only call that may trigger the system "X pasted from Y" toast.
- Banner uses Frost atoms only — no Material chrome.

## Definition of done

- Banner appears on Android within ~200ms of resume when the clipboard contains a valid `https?://` URL.
- iOS shows the banner without triggering the system paste toast until the user taps Submit.
- Dismissed URLs never re-prompt during the same app session.
- URLs already in the library do not surface the banner (verify with a sample summary).
