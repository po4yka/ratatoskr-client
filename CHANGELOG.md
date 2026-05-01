# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

- Replaced the Material 3 / IBM Carbon residual styling with the
  Frost design system across all screens, components, and
  platform widgets. INK + PAGE two-color rule with a single
  SPARK accent for critical signals; 0 corner radius; no shadows
  or elevation.
- Reader mode: SummaryDetailScreen body now renders in Source
  Serif 4 italic via the Frost markdown theme; pull-quotes
  detected from blockquotes and rendered as `PullQuote`.
- Dark mode driven by ink/page inversion (spark constant).
- JetBrains Mono and Source Serif 4 fonts bundled as Compose
  Resources.
- Removed `markdown-renderer-m3` dependency in favor of a
  project-owned Frost markdown theme.

## [Unreleased] — Renamed to Ratatoskr Client

### Breaking

- Project renamed from `bite-size-reader-client` to `ratatoskr-client`. Gradle root
  project name is now `Ratatoskr`; build-logic plugin IDs renamed from `bitesize.*`
  to `ratatoskr.*`.
- Android `applicationId` changed from `com.po4yka.bitesizereader` to
  `com.po4yka.ratatoskr`. Existing installs MUST be uninstalled and reinstalled —
  there is no in-place upgrade path. Local cached summaries, JWT tokens, and the
  on-device SQLite database will be cleared.
- iOS bundle identifiers changed: main app `com.po4yka.bitesizereader` →
  `com.po4yka.ratatoskr`; Share Extension `.shareextension` and Widget `.widget`
  variants tracked. App-group renamed from `group.com.po4yka.bitesizereader` to
  `group.com.po4yka.ratatoskr`. Background task identifier renamed to
  `com.po4yka.ratatoskr.sync`. iOS `PRODUCT_NAME` is now `Ratatoskr`. Same
  fresh-install requirement on iOS.
- Kotlin package root renamed: `com.po4yka.bitesizereader.*` → `com.po4yka.ratatoskr.*`.
  External integrators reading shared types must update imports.
- Deep-link URL scheme renamed: `bitesizereader://` → `ratatoskr://` (affects
  share-sheet, widget, and Telegram auth callback flows on both platforms).
- Default `client.id` sent to backend renamed from `android-app-v1.0` to
  `ratatoskr-android-v1.0` (and `ratatoskr-ios-v1.0` on iOS). Backend
  `ALLOWED_CLIENT_IDS` must be updated in lockstep.
- Default backend URL changed from `https://bitsizereaderapi.po4yka.com` to
  `https://api.ratatoskr.po4yka.com`.
- Telegram bot username default renamed from `bitesizereader_bot` to
  `ratatoskr_client_bot`. Coordinate with the BotFather bot rename or new bot
  registration before users can authenticate.
- Local SQLite database file renamed from `bite_size_reader.db` to `ratatoskr.db`.
  Combined with the bundle ID change above, this means existing local data is not
  carried over.
- App theme symbol renamed: `BiteSizeReaderTheme` → `RatatoskrTheme`. Android
  `Application` class renamed: `BiteSizeReaderApp` → `RatatoskrApp`.
- iOS entitlements file renamed: `iosApp/iosApp/BiteSizeReader.entitlements` →
  `iosApp/iosApp/Ratatoskr.entitlements`.

### Migration steps for existing users

1. Uninstall the previous Bite-Size Reader app from device.
2. Install Ratatoskr Client.
3. Re-authenticate with Telegram. Local summaries will sync down on first session.
4. (Self-hosting backend) Update `ALLOWED_CLIENT_IDS` server-side to include the
   new `client.id` value(s).
5. (Self-hosting backend) Provision the new Telegram bot or transfer the username
   so `ratatoskr_client_bot` resolves.

## [0.1.0] - 2026-03-17

### Added

- Multi-platform support for Android and iOS using Kotlin Multiplatform and Compose
  Multiplatform, sharing ~80-90% of business logic and UI code across platforms.
- Article and YouTube video summarization via AI: submit any URL and receive a
  structured summary (TL;DR, key ideas, key stats, topic tags, readability score).
- Offline-first architecture backed by a local SQLite database (SQLDelight) with
  automatic delta sync against the FastAPI backend using WorkManager (Android) and
  Background Tasks (iOS).
- Telegram authentication via the Telegram Login Widget, with JWT tokens stored
  securely in the platform keychain (iOS Keychain / Android EncryptedSharedPreferences).
- Apple Sign-In and Google Sign-In support for iOS and Android respectively.
- Summary library with read/unread status, favorites, and multi-axis filtering
  (date range, language, read status, sort order).
- Full-text search powered by SQLite FTS5 for offline queries, combined with
  server-side semantic search for whole-corpus results; results are merged and
  deduplicated.
- Reading statistics dashboard: total summaries read, total reading time, top
  topics and sources, language breakdown, and chart visualisations.
- Daily reading goals with configurable per-day targets (5–60 minutes) and streak
  tracking (current streak, longest streak).
- Highlight sync: select text in any summary to create persistent highlights that
  sync across devices.
- Voice narration (TTS) via ElevenLabs: listen to any summary with an in-app audio
  player supporting play, pause, and stop.
- Collections (folders) for organising summaries; supports named collections with
  optional descriptions, item management, collaborator invites, and OPML import.
- Channel digest subscriptions: subscribe to Telegram channels and receive scheduled
  AI-generated digest summaries via the bot.
- IBM Carbon Design System UI used throughout all screens for initial theming,
  typography, and accessible components (replaced by Frost in a subsequent release).
- English and Russian (EN/RU) localisation for all user-facing strings.
- Home screen widgets for recent summaries (Android Glance, iOS WidgetKit).
- Share-sheet extension (iOS) and Share Intent (Android) for submitting URLs
  directly from the system browser or any other app.
- Adaptive navigation layouts for tablets and foldable devices (Material 3
  NavigationRail on Android, multi-column layout on iPad).
- Batch URL submission mode for queuing multiple URLs in a single session.
- Request history with per-request status tracking and one-tap retry for failed
  requests.
- Decompose-based lifecycle-aware navigation shared across Android, iOS, and the
  Desktop preview target.
- MVI architecture with shared ViewModels exposing `StateFlow<State>` consumed by
  Compose UI on all platforms.
- Comprehensive CI/CD pipeline via GitHub Actions: parallel Android (Ubuntu) and
  iOS (macOS) builds, automated APK/IPA release artefacts, ktlint, Detekt, and
  dependency security scanning.

[Unreleased]: https://github.com/po4yka/ratatoskr-client/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/po4yka/ratatoskr-client/releases/tag/v0.1.0
