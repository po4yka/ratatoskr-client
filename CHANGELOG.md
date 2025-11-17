# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned
- Offline reading mode with downloaded content
- Reading statistics and analytics
- Custom topic collections/folders
- Export summaries (PDF, Markdown)
- Browser extension for quick saves

## [0.1.0] - 2025-11-16

### Added

#### Project Foundation
- Kotlin Multiplatform Mobile project structure
- Gradle build configuration with version catalogs
- iOS Xcode project setup
- Android and iOS platform modules

#### Data Layer
- Domain models (Summary, Request, User, Auth Tokens)
- API DTOs with kotlinx.serialization
- SQLDelight database schema with FTS5 search
- Ktor HTTP client with JWT authentication
- Data mappers (DTO ↔ Domain)
- Platform-specific secure storage (Keychain/EncryptedSharedPreferences)
- Store-based repository pattern for offline-first architecture

#### Business Logic
- Repository interfaces and implementations
- Use cases for all core features:
  * GetSummariesUseCase with pagination
  * GetSummaryByIdUseCase
  * MarkSummaryAsReadUseCase
  * SubmitURLUseCase with status polling
  * SearchSummariesUseCase
  * LoginWithTelegramUseCase
  * SyncDataUseCase
- Koin dependency injection modules

#### Presentation Layer
- MVI state models for all screens
- ViewModels for Summary List, Summary Detail, Submit URL, Search, Auth
- Decompose navigation components
- Root navigation with Splash → Auth → Main flow

#### Android UI (Jetpack Compose)
- Material 3 theme with dynamic colors and dark mode
- Summary list screen with pagination and pull-to-refresh
- Summary detail screen with scrollable content
- Submit URL screen with multi-stage progress
- Search screen with trending topics
- Auth screen with Telegram Login Widget (Custom Tabs)
- Reusable components (SummaryCard, TagChip, ProgressIndicator)

#### iOS UI (SwiftUI)
- SwiftUI views for all screens
- SKIE integration for Flow → AsyncSequence conversion
- Swift ViewModel wrappers with ObservableObject
- Decompose navigation integration
- Telegram Login Widget (WKWebView)
- Platform-specific components

#### Authentication
- Telegram Login Widget integration (Android & iOS)
- JWT token management with auto-refresh
- Secure token storage (Keychain/EncryptedSharedPreferences)
- Deep linking for auth callbacks (bitesizereader://telegram-auth)
- Logout flow

#### Testing Infrastructure
- Test utilities (TestDispatchers, MockDataFactory, CoroutineTestBase)
- Unit tests for domain models (Summary, Request)
- Unit tests for use cases (MarkAsRead, GetById, Login)
- Unit tests for data mappers
- Unit tests for ViewModels (Login, SummaryList)
- Android Compose UI tests (AuthScreen)
- iOS XCTest UI tests (AuthView)
- Kover code coverage configuration (80% target)
- Testing documentation (TESTING.md)

#### Performance & Optimization
- Image caching (50MB memory, 250MB disk)
- Optimized Coil 3 configuration for Android
- Exponential backoff retry mechanism
- Network connectivity monitoring (Android & iOS)
- Lazy loading and pagination
- Performance documentation (PERFORMANCE.md)

#### Accessibility
- Android TalkBack support with semantic descriptions
- iOS VoiceOver support with accessibility labels
- Proper heading hierarchy
- Content descriptions for all interactive elements

#### Animations
- Material Design animation constants (Android)
- Spring animations with proper damping
- Fade and slide transitions
- SwiftUI animation utilities (iOS)
- List item staggered animations

#### Localization
- English translations (110+ strings for Android, 90+ for iOS)
- Russian translations (110+ strings for Android, 90+ for iOS)
- Full app coverage (auth, summaries, search, errors, etc.)
- Accessibility labels in both languages

#### Error Handling
- Typed error hierarchy (AppError sealed class)
- User-friendly error messages
- Network, Server, Unauthorized, NotFound, Validation errors
- Smart retry logic for retryable errors
- Offline mode indicators

#### CI/CD
- GitHub Actions workflows for CI
- Automated testing (shared, Android, iOS)
- Code quality checks (ktlint)
- Coverage reporting with Kover
- Android and iOS build automation
- Release workflow with artifact generation
- Security scanning

#### Documentation
- README.md with project overview
- AUTHENTICATION.md - Telegram bot setup guide
- TESTING.md - Comprehensive testing guide
- PERFORMANCE.md - Performance optimization guide
- TODO.md - Implementation checklist
- ROADMAP.md - Long-term development plan
- CHANGELOG.md - Version history

### Technical Stack
- **Kotlin**: 2.2.21
- **Compose Multiplatform**: 1.9.1
- **Android Gradle Plugin**: 8.13.0
- **Ktor**: 3.3.2
- **SQLDelight**: 2.1.0
- **Decompose**: 3.4.0
- **Koin**: 4.0.0
- **Kotlinx Coroutines**: 1.10.2
- **Kotlinx Serialization**: 1.7.3
- **Kotlinx DateTime**: 0.7.1
- **SKIE**: 0.10.6
- **Coil**: 3.3.0
- **Store**: 5.1.0

### Platforms
- Android: API 24+ (Android 7.0+)
- iOS: 15.0+

---

## Version Format

- **MAJOR.MINOR.PATCH**
  - **MAJOR**: Incompatible API changes
  - **MINOR**: New functionality (backward compatible)
  - **PATCH**: Bug fixes (backward compatible)

## Categories

- **Added**: New features
- **Changed**: Changes in existing functionality
- **Deprecated**: Soon-to-be removed features
- **Removed**: Removed features
- **Fixed**: Bug fixes
- **Security**: Security vulnerability fixes

[Unreleased]: https://github.com/po4yka/bite-size-reader-client/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/po4yka/bite-size-reader-client/releases/tag/v0.1.0
