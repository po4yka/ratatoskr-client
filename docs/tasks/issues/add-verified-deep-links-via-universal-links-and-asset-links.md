---
title: Add verified deep links via Universal Links and Asset Links
status: backlog
area: ops
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add verified deep links via Universal Links and Asset Links #repo/ratatoskr-client #area/ops #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (cross-platform).

## Objective

Current deep links use the `ratatoskr://summary/{id}` scheme — opens fine but Telegram/email previews are weak and the OS shows a chooser. Upgrade to verified `https://ratatoskr.po4yka.com/s/{id}` so the OS opens the app deterministically with rich previews.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Android: new `<intent-filter android:autoVerify="true">` for `https://ratatoskr.po4yka.com/s/*` in `AndroidManifest.xml`. Reuses existing intent parsing in `MainActivity.handleIntent`.
- iOS: `com.apple.developer.associated-domains` entitlement in `Ratatoskr.entitlements`; `application(_:continue:restorationHandler:)` extended in `iOSApp.swift` to handle `NSUserActivity` (Universal Links).
- Backend hosts `.well-known/assetlinks.json` (Android) and `apple-app-site-association` (iOS).
- Keep `ratatoskr://` scheme as fallback for the widget.

## Constraints

- Both platforms must verify successfully — `adb shell pm verify-app-links com.po4yka.ratatoskr` on Android, `swcutil dl` on iOS.
- Backend files must be served as `application/json` over HTTPS with no redirects.

## Definition of done

- Tapping `https://ratatoskr.po4yka.com/s/abc123` in any app opens the Ratatoskr app directly without chooser.
- iOS share-sheet previews show the proper app icon + title.
- Existing `ratatoskr://` deep links continue working.
