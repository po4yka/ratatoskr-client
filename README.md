# Ratatoskr Client

[![PR Validation](https://github.com/po4yka/ratatoskr-client/actions/workflows/pr-validation.yml/badge.svg)](https://github.com/po4yka/ratatoskr-client/actions/workflows/pr-validation.yml)
[![CI](https://github.com/po4yka/ratatoskr-client/actions/workflows/ci.yml/badge.svg)](https://github.com/po4yka/ratatoskr-client/actions/workflows/ci.yml)
[![Code Quality](https://github.com/po4yka/ratatoskr-client/actions/workflows/code-quality.yml/badge.svg)](https://github.com/po4yka/ratatoskr-client/actions/workflows/code-quality.yml)
[![License: BSD 3-Clause](https://img.shields.io/badge/License-BSD_3--Clause-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.20-7F52FF.svg?logo=kotlin)](gradle/libs.versions.toml)
[![JDK](https://img.shields.io/badge/JDK-17-orange.svg?logo=openjdk)](https://openjdk.org/projects/jdk/17/)

Compose Multiplatform client for [Ratatoskr](https://github.com/po4yka/ratatoskr) — a service that summarizes web articles and YouTube videos using LLM. Sharing ~80–90% of business logic and UI code between Android and iOS, with a Desktop hot-reload target for UI iteration.

<!-- Screenshots: TODO populate after marketing capture.
     Replace this comment with three side-by-side images:
       docs/img/screenshot-android.png   (1170×2532 portrait, Android home)
       docs/img/screenshot-ios.png       (1170×2532 portrait, iOS home)
       docs/img/screenshot-desktop.png   (1440×900 landscape, Desktop preview)
-->

## Overview

Browse and read AI-generated summaries of articles and YouTube videos:

- Submit URLs for AI-powered summarization
- Search by topic, content, or tags
- Work offline with automatic delta sync
- Track reading progress, organize into collections
- Telegram-based authentication (no separate account needed)

### Architecture philosophy

- **Shared (~80–90%)**: infrastructure in `core/*`, feature logic in
  `feature/*`, navigation contracts in `core/navigation`, shell
  composition in `composeApp/`.
- **Shared UI**: Compose Multiplatform screens render on Android, iOS,
  and Desktop with native host hooks where the platform demands it.
- **Offline-first**: SQLite (SQLDelight) cache fronted by session-based
  delta sync against the FastAPI backend.
- **Boundary-driven**: domain/UI code stays free of transport DTOs;
  routed screens receive dependencies from Decompose components, not
  `koinInject()`.

For the rules in detail, see [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

## CI/CD

GitHub Actions runs Android builds (Ubuntu) and iOS builds (macOS) in
parallel on every PR; main-branch CI runs the same plus Detekt,
ktlint, Kover coverage, and dependency security scans. Releases are
tag-driven (`git tag v1.0.0 && git push --tags`) and produce APK +
IPA artefacts. Add the `skip-ios` label to a PR to skip the macOS
runner for Android-only changes.

See [`docs/CICD.md`](docs/CICD.md) for the full setup, secret
configuration, and troubleshooting.

## Tech Stack

### Shared (commonMain)

| Category | Technology | Purpose |
|---|---|---|
| Navigation | [Decompose](https://github.com/arkivanov/Decompose) | Lifecycle-aware navigation, retained ViewModels |
| Networking | [Ktor Client](https://ktor.io/docs/client.html) | HTTP with bearer-token auto-refresh |
| Persistence | [SQLDelight](https://cashapp.github.io/sqldelight/) | Type-safe SQL with coroutines |
| DI | [Koin](https://insert-koin.io/) (annotations + DSL) | Compile-time DI graph |
| Serialization | [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | JSON wire format |
| Concurrency | [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) | `Flow`, `StateFlow`, structured concurrency |
| Date/Time | [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) | ISO 8601, timezone-correct |
| Logging | [kotlin-logging](https://github.com/oshai/kotlin-logging) | Structured logging facade |
| UI Kit | Material 3 + project `AppTheme` | Custom design tokens in `core/ui/.../theme/` |
| Hot Reload | [Compose Hot Reload](https://github.com/JetBrains/compose-hot-reload) | Desktop dev loop |

### Android (androidApp + Android actuals)

- **Jetpack Compose** UI (Material 3 + `AppTheme`)
- **Tink AEAD + DataStore** secure JWT storage (see [`docs/SECURITY.md`](docs/SECURITY.md))
- **OkHttp** Ktor engine
- **WorkManager** background sync
- **Glance** home-screen widget

### iOS (iosApp + iOS actuals)

- **SwiftUI** shell hosts the `ComposeApp` framework
- **KeychainSettings** secure JWT storage
- **Darwin** Ktor engine
- **Background Tasks** for sync
- **WidgetKit** home-screen widget
- **Share Extension** for URL submission from any app
- **SKIE** configured but disabled for the active Kotlin version

### Desktop

Development-only target for Compose hot reload. Not shipped as a
production app.

## Prerequisites

| Tool | Minimum version | Notes |
|---|---|---|
| JDK | **17** | LTS, Temurin recommended |
| Gradle | **9.4.1** (via wrapper) | `./gradlew` resolves automatically |
| Android Gradle Plugin | **9.0.1** | Wired in `build-logic/` |
| Kotlin | **2.3.20** | KMP + KSP |
| Compose Multiplatform | **1.10.3** | UI framework |
| Android `compileSdk` / `minSdk` | **36 / 24** | Modern devices, API 24+ |
| Android Studio | Ladybug+ (2024.2.1) | For IDE workflow |
| Xcode | **15+** | For iOS builds (macOS only) |
| CocoaPods | recent stable | iOS framework integration |
| `xcodegen` (optional) | recent stable | If regenerating `iosApp.xcodeproj` from `project.yml` |

The mobile client also requires a running [`po4yka/ratatoskr`](https://github.com/po4yka/ratatoskr)
backend instance — see [`docs/DEVELOPMENT.md#running-the-backend-locally`](docs/DEVELOPMENT.md#running-the-backend-locally).

## Platform feature matrix

| Feature | Android | iOS | Desktop |
|---|:---:|:---:|:---:|
| Summary list, detail, search | ✅ | ✅ | ✅ (preview) |
| Submit URL → poll status → display summary | ✅ | ✅ | ✅ |
| Telegram auth (login widget + OAuth) | ✅ | ✅ | ❌ |
| Offline cache + delta sync | ✅ | ✅ | ⚠️ in-memory only |
| Background sync | ✅ WorkManager | ✅ Background Tasks | ❌ |
| Home-screen widget | ✅ Glance | ✅ WidgetKit | ❌ |
| Share-sheet URL submission | ✅ Share Intent | ✅ Share Extension | ❌ |
| Secure token storage | ✅ Tink + DataStore | ✅ Keychain | ⚠️ in-memory dev only |
| Ratatoskr launcher icon | ✅ | ✅ | n/a |

Desktop is a development convenience for UI iteration, not a
production target. See [`docs/COMPOSE_HOT_RELOAD.md`](docs/COMPOSE_HOT_RELOAD.md).

## Project Structure

```
ratatoskr-client/
├── androidApp/              # Android Application class, MainActivity, Glance widgets, WorkManager workers
├── composeApp/              # Compose shell, navigation graph, CocoaPods export, Desktop dev target
├── core/
│   ├── common/              # Cross-feature domain primitives, AppConfig, BaseViewModel, error types
│   ├── data/                # Ktor ApiClient, SQLDelight, SecureStorage actuals, generic API wrappers
│   ├── navigation/          # Route contracts (RootNavigation, MainNavigation)
│   └── ui/                  # Shared Compose components, RatatoskrTheme, AppIcons, Compose Resources
├── feature/
│   ├── auth/                # Telegram login screen, JWT exchange, login session
│   ├── collections/         # Folders, tags, RSS, OPML import/export
│   ├── digest/              # Channel digest subscriptions, custom digests
│   ├── settings/            # Settings, stats, reading goals, account
│   ├── summary/             # List/detail, search, submit URL, recommendations
│   └── sync/                # Sync orchestration + per-entity appliers contract
├── iosApp/                  # SwiftUI host, ShareExtension, RecentSummariesWidget, project.yml
├── build-logic/             # Convention plugins (ratatoskr.kmp.library, ratatoskr.android.application, ...)
├── gradle/libs.versions.toml
└── docs/                    # Reference documentation
```

A feature module never imports another feature's `data/` or
`presentation/` packages — only the public domain contracts. See
[`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the full
dependency-direction rules.

## Backend API

The client talks to the FastAPI backend at [`po4yka/ratatoskr`](https://github.com/po4yka/ratatoskr).
Default base URL: `https://api.ratatoskr.po4yka.com` (override via
`api.base.url` in `local.properties`). See [`docs/API.md`](docs/API.md)
for endpoint contracts, error codes, and Kotlin transport-layer wiring.

## Quickstart

After cloning the repo, drop a `local.properties` in the root:

```properties
api.base.url=http://localhost:8000
client.id=ratatoskr-android-v1.0
telegram.bot.username=ratatoskr_client_bot
telegram.bot.id=
```

(Use `http://10.0.2.2:8000` instead of `localhost` for the Android
emulator. `local.properties` is gitignored.)

### Android

```bash
./gradlew :androidApp:installDebug
```

### iOS

```bash
cd iosApp && pod install && cd ..
xcodebuild -workspace iosApp/iosApp.xcworkspace -scheme iosApp \
  -configuration Debug -sdk iphonesimulator
# Or open iosApp/iosApp.xcworkspace in Xcode
```

### Desktop (hot reload)

```bash
./gradlew :composeApp:hotRunDesktop
```

### Tests

```bash
./gradlew :core:common:allTests :core:data:allTests
./gradlew :feature:summary:allTests :feature:settings:allTests
./gradlew :composeApp:testDebugUnitTest
./gradlew detekt ktlintCheck
```

For the full development guide (IDE setup, debugging, code-quality
workflow), see [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md).

## Documentation

See [`docs/INDEX.md`](docs/INDEX.md) for the complete documentation
index. Most-used entry points:

| Topic | Document |
|---|---|
| System architecture and module dependency rules | [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) |
| Backend API contracts and Ktor wiring | [`docs/API.md`](docs/API.md) |
| Telegram auth setup (BotFather, deep links, WebView actuals) | [`docs/AUTHENTICATION.md`](docs/AUTHENTICATION.md) |
| Offline sync strategy | [`docs/SYNC_STRATEGY.md`](docs/SYNC_STRATEGY.md) |
| Secure storage / threat model | [`docs/SECURITY.md`](docs/SECURITY.md) |
| Build, runtime, and API troubleshooting | [`docs/TROUBLESHOOTING.md`](docs/TROUBLESHOOTING.md) |
| ViewModel pattern, use cases, repository pattern | [`docs/VIEWMODEL_GUIDE.md`](docs/VIEWMODEL_GUIDE.md), [`docs/USE_CASE_GUIDE.md`](docs/USE_CASE_GUIDE.md), [`docs/REPOSITORY_PATTERNS.md`](docs/REPOSITORY_PATTERNS.md) |
| Compose component catalog | [`docs/COMPONENT_LIBRARY.md`](docs/COMPONENT_LIBRARY.md) |
| CI/CD workflows | [`docs/CICD.md`](docs/CICD.md) |

For AI-assisted development, the project ships skill files at
`.claude/skills/building-kmp-features/` and `.codex/skills/building-kmp-features/`
(content-equivalent). They cover the file map, DI rules, and screen
patterns in a form agents can follow directly.

## Contributing

Contributions are welcome. See [`CONTRIBUTING.md`](CONTRIBUTING.md)
for the dev-environment quickstart, commit-message style, PR
checklist, and architecture rules. The
[`.github/pull_request_template.md`](.github/pull_request_template.md)
is auto-filled when you open a PR.

By participating you agree to abide by the project's
[Code of Conduct](CODE_OF_CONDUCT.md).

## Security

Found a vulnerability? Please report it privately per
[`SECURITY.md`](SECURITY.md) — do **not** open a public issue.

For the engineering-side threat model, secure-storage choices, and
the security checklist, see [`docs/SECURITY.md`](docs/SECURITY.md).

## License

BSD 3-Clause License — see [LICENSE](LICENSE).

Copyright (c) 2025, Nikita Pochaev

## Related Projects

- **Backend service**: [`po4yka/ratatoskr`](https://github.com/po4yka/ratatoskr) — FastAPI backend with Telegram bot
