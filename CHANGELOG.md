# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
- IBM Carbon Design System UI (`carbon-compose` library) used throughout all screens,
  providing consistent theming, typography, and accessible components.
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

[Unreleased]: https://github.com/po4yka/bite-size-reader-client/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/po4yka/bite-size-reader-client/releases/tag/v0.1.0
