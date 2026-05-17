---
title: Wire RatatoskrDeepLinkParser into Android and iOS deep-link entry points + ship verified-link metadata
status: backlog
area: ops
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire RatatoskrDeepLinkParser into Android and iOS deep-link entry points + ship verified-link metadata #repo/ratatoskr-client #area/ops #status/backlog 🔼

Follow-up to `add-verified-deep-links-via-universal-links-and-asset-links`
(landed the durable testable atom: `core/common/.../util/deeplink/RatatoskrDeepLinkParser.kt`
with a sealed `RatatoskrDeepLink` hierarchy — `OpenSummary(id) | SubmitUrl(url) | Unknown(raw)` —
supporting `https://ratatoskr.po4yka.com/s/{id}` Universal/App Links plus
`ratatoskr://summary/{id}` and `ratatoskr://submit-url?url=…` custom-scheme
widget fallbacks, with strict path shape, phishing-host rejection, hand-rolled
percent-decoding, and case-insensitive scheme/host. 12 commonTest cases pin
the contract.).

## Objective

Bring up the platform entry points that consume the parser + ship the
verified-link metadata so the OS opens the app deterministically:

1. **Android**: new `<intent-filter android:autoVerify="true">` in
   `AndroidManifest.xml` for `https://ratatoskr.po4yka.com/s/*`.
   `MainActivity.handleIntent(intent)` calls
   `RatatoskrDeepLinkParser.parse(intent.dataString.orEmpty())` and routes
   based on the sealed result.
2. **iOS**: `com.apple.developer.associated-domains` entitlement entry
   (`applinks:ratatoskr.po4yka.com`) in `Ratatoskr.entitlements`. Extend
   `iOSApp.swift`'s `application(_:continue:restorationHandler:)` to read
   `userActivity.webpageURL?.absoluteString` and pass it through the
   parser via the KMP bridge.
3. **Backend**: host `.well-known/assetlinks.json` (Android) and
   `apple-app-site-association` (iOS) at the canonical https host. Both
   must be served as `application/json` over HTTPS with no redirects.
   Files are out-of-repo (backend-owned) but coordinated.
4. **Widget**: existing `RecentSummariesWidget` `PendingIntent`s already
   build `ratatoskr://summary/{id}` URIs — the parser's custom-scheme
   branch covers them; no widget code change needed.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `androidApp/src/main/AndroidManifest.xml` — new intent-filter with
  `autoVerify="true"`.
- `androidApp/.../MainActivity.kt` — `handleIntent` routes through the
  parser.
- `iosApp/iosApp/Ratatoskr.entitlements` — associated-domains entry.
- `iosApp/iosApp/iOSApp.swift` — `NSUserActivity` continuation handler
  forwarding the URL through the KMP bridge.
- Backend hosts the two well-known files.

## Constraints

- Both platforms must verify successfully —
  `adb shell pm verify-app-links com.po4yka.ratatoskr` on Android,
  `swcutil dl` on iOS.
- Backend files served as `application/json` over HTTPS with no redirects.
- The custom `ratatoskr://` scheme stays registered as the widget fallback
  (no breaking change for existing notification PendingIntents).

## Definition of done

- Tapping `https://ratatoskr.po4yka.com/s/abc123` opens the app directly
  without chooser on both platforms.
- iOS share-sheet previews render the proper app icon + title.
- Existing `ratatoskr://` deep links continue working unchanged.
