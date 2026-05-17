---
title: Add clipboard URL detector banner
status: backlog
area: frontend
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add clipboard URL detector banner #repo/ratatoskr-client #area/frontend #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have feature brainstorm (Capture & Submission).

## Objective

When the user opens the app with a URL on the system clipboard, surface a one-tap submit banner at the top of `SummaryListScreen` — avoids the friction of navigating to submit, pasting, hitting submit.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `expect ClipboardProbe.peekUrl(): String?` in `core/common`. Android `actual` uses `ClipboardManager`; iOS `actual` uses `UIPasteboard.general.detectPatterns([.probableWebURL])` to avoid the iOS paste prompt.
- Banner above the list with `[ Submit <truncated-url> ]` and `[ Dismiss ]` Frost brackets.
- Dismissed URLs remembered in `UserPreferences` to avoid re-prompting.

## Constraints

- iOS: must use the detection-pattern API (iOS 16+) — no plain `UIPasteboard.string` read on foreground.
- Banner shown only when the clipboard URL is unrelated to anything already in the user's library.

## Definition of done

- Banner appears on Android within 200ms of resume when clipboard contains a valid `https?://` URL.
- iOS shows banner without triggering the system "X pasted from Y" toast.
- Dismissed URLs never re-prompt during the same app session.
