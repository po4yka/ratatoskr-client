# Ratatoskr Client

[![PR Validation](https://github.com/po4yka/ratatoskr-client/actions/workflows/pr-validation.yml/badge.svg)](https://github.com/po4yka/ratatoskr-client/actions/workflows/pr-validation.yml)
[![CI](https://github.com/po4yka/ratatoskr-client/actions/workflows/ci.yml/badge.svg)](https://github.com/po4yka/ratatoskr-client/actions/workflows/ci.yml)
[![Code Quality](https://github.com/po4yka/ratatoskr-client/actions/workflows/code-quality.yml/badge.svg)](https://github.com/po4yka/ratatoskr-client/actions/workflows/code-quality.yml)

Compose Multiplatform client for [Ratatoskr](https://github.com/po4yka/ratatoskr) - a service that summarizes web articles and YouTube videos using LLM.

## Overview

This is a **Kotlin Multiplatform + Compose Multiplatform** app that provides a shared UI stack across Android, iOS, and Desktop while sharing ~80-90% of business logic code. The app allows users to:

- Browse and read saved article/video summaries
- Submit new URLs for AI-powered summarization
- Search summaries by topic, content, or tags
- Work offline with automatic sync
- Track reading progress and organize content

### Architecture Philosophy

**KMP + Compose Multiplatform UI:**
- **Shared Code (80-90%)**: Infrastructure in `core/*`, feature logic in `feature/*`, navigation contracts in `core/navigation`, and shell composition in `composeApp/`
- **Shared UI**: Compose Multiplatform screens rendered on Android, iOS, and Desktop (with native host hooks where needed)
- **Offline-First**: Local SQLite database with session-based sync to the backend API
- **Boundary-Driven**: Domain/UI code stays free of transport DTOs and routed screens receive dependencies from components

## CI/CD

This project features **comprehensive CI/CD automation** using GitHub Actions:

-  **Automated Testing**: All PRs run Android, iOS, and modular KMP test suites
-  **Multi-Platform Builds**: Parallel builds on Ubuntu (Android) and macOS (iOS)
-  **Automated Releases**: Tag-based releases with automatic APK/IPA generation
-  **Code Quality**: Linting, security scanning, and dependency checks
-  **Dependabot**: Automatic dependency updates with grouped PRs
-  **Cost Optimized**: Conditional builds and smart caching reduce CI minutes by ~60%

**Quick Start:**
- PRs automatically validate on both platforms
- Add `skip-ios` label to skip expensive macOS builds for Android-only changes
- Create releases: `git tag v1.0.0 && git push --tags`

See **[docs/CICD.md](docs/CICD.md)** for complete documentation including setup, secrets configuration, and troubleshooting.

## Tech Stack

### Shared Kotlin Multiplatform (commonMain)

| Category | Technology | Purpose |
|----------|-----------|---------|
| **Navigation** | [Decompose](https://github.com/arkivanov/Decompose) | Lifecycle-aware navigation and state preservation |
| **Networking** | [Ktor Client 3.0](https://ktor.io/docs/client.html) | HTTP client with async/await support |
| **Data Layer** | Feature repositories + Ktor APIs | Repository pattern with local persistence and sync |
| **Database** | [SQLDelight 2.0](https://cashapp.github.io/sqldelight/) | Type-safe SQL with coroutines support |
| **Serialization** | [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | JSON parsing and data classes |
| **DI** | [Koin 4.1+](https://insert-koin.io/) | Dependency injection |
| **Coroutines** | [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) | Async/await and Flow streams |
| **Date/Time** | [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) | ISO 8601 parsing and timezone handling |
| **Logging** | [kotlin-logging](https://github.com/oshai/kotlin-logging) | Structured multiplatform logging |
| **Icons** | Custom Carbon Icons | IBM Carbon Design System icons as ImageVectors |
| **Dev Tools** | [Compose Hot Reload](https://github.com/JetBrains/compose-hot-reload) | Instant UI updates without restarts |

### iOS (Compose Multiplatform host)

- **Compose Multiplatform** - Shared UI rendered via `MainViewController`
- **SwiftUI shell** - Hosts Compose UI and bridges Telegram login via native WebView
- **SKIE** - Configured in Gradle but currently disabled for the active Kotlin version
- **Keychain** - Secure JWT token storage
- **Share Extension** - Submit URLs from Safari/other apps
- **WidgetKit** - Home screen widget for recent summaries

### Android (Jetpack Compose)

- **Jetpack Compose** - Modern declarative UI (100% Compose)
- **Material 3** - Material Design components
- **Koin Android** - Activity/Composable injection
- **Tink AEAD + DataStore** - Secure JWT storage
- **WorkManager** - Background sync jobs
- **App Widgets** - Home screen widget

## Project Structure

```
ratatoskr-client/
 androidApp/                      # Android app host, widgets, workers, manifest/resources
 composeApp/                      # Compose Multiplatform UI + navigation shell + CocoaPods export
    src/iosMain/kotlin/          # Compose UIViewController for iOS host
    src/desktopMain/kotlin/      # Desktop preview entrypoint
    src/commonMain/kotlin/       # Shared Compose UI/theme/navigation
 core/
    common/                       # Shared domain, config, base presentation primitives
    data/                         # Shared networking/bootstrap, SQLDelight, persistence, secure storage
    navigation/                   # Route contracts and navigator interfaces
    ui/                           # Shared non-feature UI primitives
 feature/
    auth/                         # Auth/session contracts, APIs, and flows
    collections/                  # Collections, tags, RSS, import/export
    digest/                       # Digest and custom digest flows
    settings/                     # Settings, stats, reading goals, account
    summary/                      # Summary list/detail, search, submit URL, recommendations
    sync/                         # Sync orchestration and public sync contracts
 iosApp/                          # iOS app shell (SwiftUI hosting Compose)
    iosApp/
       Auth/                   # Native Telegram login sheet
       Config/                 # Platform config
       iOSApp.swift            # Entry point hosting Compose UI
    ShareExtension/             # Native share-extension source
    RecentSummariesWidget/      # Native widget source
    Info.plist                  # Main app config
    Podfile                     # CocoaPods dependencies
 gradle/
    libs.versions.toml          # Version catalog
 README.md                        # This file
 docs/                            # Reference documentation
```

## Backend API

The client talks to the FastAPI backend at [`po4yka/ratatoskr`](https://github.com/po4yka/ratatoskr). Default base URL: `https://api.ratatoskr.po4yka.com` (override via `api.base.url` in `local.properties`). See [`docs/API.md`](docs/API.md) for endpoint contracts, error codes, and Kotlin transport-layer wiring.

## Getting Started

### Prerequisites

#### Development Tools

- **Xcode 15+** (for iOS development, macOS only)
- **Android Studio Ladybug+** (2024.2.1 or later)
- **JDK 17+** (for Gradle)
- **CocoaPods** (for iOS dependencies)

#### Backend Service

The mobile client requires the [`po4yka/ratatoskr`](https://github.com/po4yka/ratatoskr) FastAPI backend. See [`docs/DEVELOPMENT.md#running-the-backend-locally`](docs/DEVELOPMENT.md#running-the-backend-locally) for the Docker setup, environment variables, and verification steps.

### Clone Repository

```bash
git clone https://github.com/po4yka/ratatoskr-client.git
cd ratatoskr-client
```

### Configuration

Create `local.properties` in project root:

```properties
# Backend API base URL
api.base.url=http://localhost:8000

# Telegram Bot Token (for auth verification)
telegram.bot.token=YOUR_BOT_TOKEN_HERE

# Client ID (identifies this app to backend)
client.id=ratatoskr-android-v1.0
```

**Note**: Do NOT commit `local.properties` - it's in `.gitignore`.

### Build & Run

#### Desktop (Hot Reload for UI Development)

```bash
# Run with Compose Hot Reload for rapid UI development
./gradlew :composeApp:runDesktop

# Edit any Compose UI file and see changes instantly!
```

**Note**: Desktop target is for development only. See [docs/COMPOSE_HOT_RELOAD.md](docs/COMPOSE_HOT_RELOAD.md) for details.

#### Android

```bash
# Open in Android Studio
open -a "Android Studio" .

# Or build from command line
./gradlew :androidApp:assembleDebug

# Install on connected device/emulator
./gradlew :androidApp:installDebug
```

#### iOS

```bash
# Install CocoaPods dependencies
cd iosApp
pod install
cd ..

# Open Xcode workspace
open iosApp/iosApp.xcworkspace

# Or build from command line
xcodebuild -workspace iosApp/iosApp.xcworkspace \
           -scheme iosApp \
           -configuration Debug \
           -sdk iphonesimulator
```

### Running Tests

```bash
# Run the module tests you changed
./gradlew :core:common:allTests :core:data:allTests
./gradlew :feature:summary:allTests :feature:settings:allTests

# Android tests
./gradlew :composeApp:testDebugUnitTest
```

## Development

### Code Style

- **Kotlin**: [Official Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html)
- **Swift**: [Swift API Design Guidelines](https://swift.org/documentation/api-design-guidelines/)
- **Formatting**: Use IDE auto-formatting (Cmd+Opt+L / Ctrl+Alt+L)

### Dependency Management

All versions are managed in `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "2.2.20"
ktor = "3.0.2"
sqldelight = "2.0.2"
decompose = "3.2.0"
store = "5.1.0"
koin = "3.5.6"

[libraries]
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
# ... more dependencies
```

### Adding Dependencies

1. Add version to `[versions]` section in `libs.versions.toml`
2. Add library to `[libraries]` section
3. Reference in `build.gradle.kts`: `implementation(libs.ktor.client.core)`

### Architecture Patterns

The MVI / Repository / Decompose patterns the project uses are documented in [`docs/ARCHITECTURE.md#implementation-patterns`](docs/ARCHITECTURE.md#implementation-patterns), with annotated snippets straight from the codebase.

## Key Features

### Offline-First Architecture

- **Local SQLite Database**: All summaries cached locally
- **Background Sync**: Automatic delta sync on app launch
- **Optimistic Updates**: Instant UI updates with background sync
- **Conflict Resolution**: Server wins for reads, local changes uploaded

### Telegram Authentication

1. User taps "Login with Telegram"
2. Opens Telegram Login Widget (WebView on iOS, Custom Tab on Android)
3. User authorizes in Telegram app
4. Callback receives auth data
5. App exchanges auth data for JWT tokens
6. Tokens stored securely (Keychain/Tink AEAD + DataStore)

### URL Submission Flow

1. User pastes URL or shares from another app
2. Client validates URL format
3. POST to `/v1/requests` with URL
4. Receive `request_id`
5. Poll `/v1/requests/{id}/status` every 2 seconds
6. Show progress: content_extraction → llm_summarization → validation → done
7. Fetch final summary and display

### Search

- **Local FTS**: SQLite FTS5 for offline search
- **Remote API**: Full-corpus search on backend
- **Merged Results**: Combine local + remote with deduplication
- **Topic Tags**: Filter by hashtags (#technology, #ai, etc.)

## Performance

### Optimizations

- **Lazy Loading**: Pagination (20 items per page)
- **Image Caching**: Coil (Android) / Kingfisher (iOS) for thumbnails
- **Database Indexing**: Indexes on `is_read`, `created_at`, FTS
- **Memory Management**: Weak references, proper lifecycle handling
- **Background Sync**: WorkManager (Android) / Background Tasks (iOS)

### Benchmarks

- **App Launch**: <2 seconds cold start
- **Summary List**: 60 FPS scrolling with 1000+ items
- **Search**: <200ms for local FTS, <500ms for remote
- **Sync**: <5 seconds for 100 summaries delta sync

## Troubleshooting

See [`docs/TROUBLESHOOTING.md`](docs/TROUBLESHOOTING.md) for build, runtime, API, and logging troubleshooting (with platform-specific notes).

## Documentation

See [docs/INDEX.md](docs/INDEX.md) for the complete documentation index.

### Quick Links

| Topic | Document |
|-------|----------|
| Architecture | [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) |
| Sync Strategy | [docs/SYNC_STRATEGY.md](docs/SYNC_STRATEGY.md) |
| API Reference | [docs/API.md](docs/API.md) |
| Authentication | [docs/AUTHENTICATION.md](docs/AUTHENTICATION.md) |
| ViewModel Guide | [docs/VIEWMODEL_GUIDE.md](docs/VIEWMODEL_GUIDE.md) |
| Use Case Guide | [docs/USE_CASE_GUIDE.md](docs/USE_CASE_GUIDE.md) |
| Component Library | [docs/COMPONENT_LIBRARY.md](docs/COMPONENT_LIBRARY.md) |
| CI/CD | [docs/CICD.md](docs/CICD.md) |

For AI-assisted development guidance, see [CLAUDE.md](CLAUDE.md).

## License

BSD 3-Clause License - see [LICENSE](./LICENSE) file.

Copyright (c) 2025, Nikita Pochaev

## Related Projects

- **Backend Service**: [ratatoskr](https://github.com/po4yka/ratatoskr) - FastAPI backend with Telegram bot
