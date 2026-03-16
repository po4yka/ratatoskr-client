# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Bite-Size Reader is a **Kotlin Multiplatform + Compose Multiplatform** mobile client for an AI-powered article/video summarization service. It targets Android, iOS, and Desktop (desktop is for development hot reload only).

- **Shared code**: ~80-90% in `shared/` module (business logic, networking, database)
- **Shared UI**: Compose Multiplatform in `composeApp/` rendered on all platforms
- **iOS shell**: SwiftUI host in `iosApp/` that embeds the Compose UI
- **Architecture**: Clean Architecture with MVI pattern, offline-first with SQLite + background sync

## Build Commands

```bash
# Build all
./gradlew build

# Android
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug

# Desktop (Hot Reload for UI development)
./gradlew :composeApp:hotRunDesktop

# iOS - open in Xcode
open iosApp/iosApp.xcodeproj
```

## Testing

```bash
# All shared tests
./gradlew :shared:allTests

# Android unit tests
./gradlew :composeApp:testDebugUnitTest

# Run specific test class
./gradlew :shared:testDebugUnitTest --tests SummaryListViewModelTest

# iOS tests (macOS only)
./gradlew :shared:iosSimulatorArm64Test

# Coverage report
./gradlew koverHtmlReportDebug
```

## Code Quality

```bash
# Lint check
./gradlew ktlintCheck

# Auto-fix lint issues
./gradlew ktlintFormat

# Static analysis
./gradlew detekt

# All quality checks
./gradlew ktlintCheck detekt
```

## Architecture

### Key Patterns

- **MVI**: ViewModels expose `StateFlow<State>` and accept events/intents
- **Repository Pattern**: Repositories coordinate between remote API and local database
- **Offline-First**: Always read from local DB first, sync with server in background
- **Decompose Navigation**: Cross-platform navigation with lifecycle awareness

### Sync & Auth

Session-based sync with progress tracking. Bearer token auth via Ktor `Auth` plugin with automatic refresh. See `shared/CLAUDE.md` for details.

### Technology Stack

| Component | Technology |
|-----------|------------|
| Networking | Ktor Client 3.x |
| Database | SQLDelight 2.x |
| DI | Koin 4.x with Annotations + KSP |
| Navigation | Decompose 3.x |
| Serialization | kotlinx.serialization |
| Async | kotlinx.coroutines + Flow |
| Image Loading | Coil 3.x |
| Design System | Carbon Compose (IBM Carbon) |
| Icons | Custom Carbon Icons (IBM Carbon Design) |

## Dependency Injection

Uses **Koin 4.x with Koin Annotations** + KSP. Use `@Single` for singletons, `@Factory` for per-use instances. Constructor injection only (no `get()` calls). Use annotations, not `module { }` DSL, for all new code. See `shared/CLAUDE.md` for detailed DI rules and annotation-by-layer guide.

## Design System & Icons

UI uses **Carbon Compose** (IBM Carbon Design System). Access colors via `Carbon.theme.*`, typography via `Carbon.typography.*`, icons via `CarbonIcons.*`. Use Material3 `Text` and `Icon` composables (Carbon's are internal). See `composeApp/CLAUDE.md` for detailed component, theming, and icon rules.

### Exemptions

- **Android Glance widgets** (`widget/` package) are exempt from Carbon theming requirements. Glance uses its own theming API (`GlanceTheme`, `ColorProviders`) which is incompatible with Compose Carbon components.

## Configuration

Create `local.properties` in project root (not committed):

```properties
api.base.url=http://localhost:8000
client.id=android-app-v1.0
```

For Android emulator, use `http://10.0.2.2:8000` instead of `localhost`.

## Code Style

- ktlint configured for Compose (uppercase function names allowed for @Composable)
- Max line length: 120 characters
- Generated code in `build/` and `generated/` is excluded from checks
- Detekt enabled with relaxed rules for Compose/KMP patterns

## Common Development Tasks

### Add a New Screen

1. Create state class in `shared/.../presentation/state/MyState.kt`
2. Create ViewModel in `shared/.../presentation/viewmodel/MyViewModel.kt` with `@Factory`
3. Add navigation component in `shared/.../presentation/navigation/MyComponent.kt`
4. Create screen composable in `composeApp/.../ui/screens/MyScreen.kt`
5. Register in `RootComponent.kt` navigation graph

### Add a New Use Case

1. Create use case in `shared/.../domain/usecase/MyUseCase.kt`
2. Add `@Factory` annotation
3. Inject repository via constructor
4. Return `Result<T>` or `Flow<T>`

### Add a New Repository

1. Define interface in `shared/.../domain/repository/MyRepository.kt`
2. Create implementation in `shared/.../data/repository/MyRepositoryImpl.kt`
3. Add `@Single(binds = [MyRepository::class])` annotation
4. Create DTOs in `data/remote/dto/` if needed
5. Create mappers in `data/mappers/` if needed

## Platform-Specific Notes

### Android
- `EncryptedSharedPreferences` for secure token storage
- `WorkManager` for background sync
- `Glance` for home screen widgets

### iOS
- Keychain for secure token storage
- `BackgroundTasks` for background sync
- `WidgetKit` for home screen widgets
- SKIE for improved Kotlin/Swift interop (Flow -> AsyncSequence, suspend -> async)

### Desktop
- Development only - used for Compose Hot Reload during UI work
- Not a production target
