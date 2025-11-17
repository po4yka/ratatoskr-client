# Architecture Documentation

**Project**: Bite-Size Reader Mobile Client
**Last Updated**: 2025-11-17
**Version**: 1.0.0

---

## Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Architecture Patterns](#architecture-patterns)
4. [Module Structure](#module-structure)
5. [Data Flow](#data-flow)
6. [Platform-Specific Details](#platform-specific-details)
7. [Dependency Management](#dependency-management)
8. [Security](#security)
9. [Performance](#performance)
10. [Design Decisions](#design-decisions)

---

## Overview

Bite-Size Reader is a Kotlin Multiplatform Mobile (KMM) application that provides AI-powered summaries of web articles. The app uses a **shared business logic layer** written in Kotlin and platform-specific UI implementations for Android (Compose) and iOS (SwiftUI).

### Key Characteristics

- **Offline-First**: Local database with background sync
- **Cross-Platform**: 90% code sharing between Android and iOS
- **Modern UI**: Jetpack Compose (Android) and SwiftUI (iOS)
- **Clean Architecture**: Separation of concerns with clear boundaries
- **Type-Safe**: Kotlin's type system for robust code

---

## Technology Stack

### Shared (Kotlin Multiplatform)

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Kotlin** | 2.2.10 | Core language |
| **Ktor** | 3.3.2 | HTTP client for API calls |
| **SQLDelight** | 2.1.0 | Type-safe SQL database |
| **Koin** | 4.0.0 | Dependency injection |
| **Kotlinx Serialization** | 1.7.3 | JSON serialization |
| **Kotlinx Coroutines** | 1.10.2 | Async/concurrency |
| **Kotlinx DateTime** | 0.6.1 | Date/time handling |
| **Decompose** | 3.4.0 | Navigation (shared) |
| **Store** | 5.0.0 | Repository pattern utilities |
| **kotlin-logging** | 7.0.3 | Structured logging |

### Android-Specific

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Jetpack Compose** | 1.9.1 | Declarative UI |
| **Compose Material 3** | - | Material Design components |
| **Glance** | 1.1.0 | Home screen widgets |
| **WorkManager** | 2.10.0 | Background sync |
| **Security Crypto** | 1.1.0-alpha06 | Encrypted SharedPreferences |
| **Coil** | 3.3.0 | Image loading |

### iOS-Specific

| Technology | Version | Purpose |
|-----------|---------|---------|
| **SwiftUI** | iOS 15+ | Declarative UI |
| **WidgetKit** | iOS 15+ | Home screen widgets |
| **BackgroundTasks** | iOS 13+ | Background sync |
| **Keychain** | - | Secure token storage |
| **SKIE** | 0.10.8 | Kotlin/Swift interop |

### Development Tools

| Tool | Purpose |
|------|---------|
| **Compose Hot Reload** | 1.0.0-rc03 | Instant UI updates (desktop target) |
| **ktlint** | Code formatting |
| **detekt** | Static analysis |
| **Kover** | Code coverage |

---

## Architecture Patterns

### Clean Architecture

The project follows Uncle Bob's Clean Architecture with three main layers:

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│  (ViewModels, UI States, Screens)       │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            Domain Layer                 │
│  (Use Cases, Models, Repositories)      │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│             Data Layer                  │
│  (API, Database, Local Storage)         │
└─────────────────────────────────────────┘
```

#### Layer Responsibilities

**1. Presentation Layer** (`presentation/`)
- ViewModels manage UI state
- Screens render UI based on state
- No business logic
- Platform-specific implementations

**2. Domain Layer** (`domain/`)
- Use Cases contain business logic
- Domain models represent business entities
- Repository interfaces (implementation in data layer)
- Platform-agnostic

**3. Data Layer** (`data/`)
- API client for network calls
- Database for local storage
- Repository implementations
- Data source coordination

### MVI (Model-View-Intent)

The presentation layer uses MVI pattern:

```kotlin
// State
data class SummaryListState(
    val summaries: List<Summary>,
    val isLoading: Boolean,
    val error: AppError?
)

// Intent (user actions)
viewModel.loadSummaries()
viewModel.refresh()
viewModel.retry()

// View observes state
viewModel.state.collect { state ->
    // Render UI based on state
}
```

**Benefits**:
- Unidirectional data flow
- Predictable state changes
- Easy testing
- Time-travel debugging possible

### Repository Pattern

Data access is abstracted through repositories:

```kotlin
interface SummaryRepository {
    fun getSummaries(limit: Int, offset: Int, filters: SearchFilters): Flow<List<Summary>>
    suspend fun getSummaryById(id: Int): Summary?
    suspend fun saveSummary(summary: Summary)
}
```

**Implementation** combines multiple data sources:
- Remote API (server)
- Local database (SQLite)
- In-memory cache (Store library)

### Dependency Injection

Uses Koin for constructor injection:

```kotlin
val dataModule = module {
    single { ApiClient(get()) }
    single<SummaryRepository> { SummaryRepositoryImpl(get(), get()) }
}

val domainModule = module {
    factory { GetSummariesUseCase(get()) }
}

val presentationModule = module {
    factory { (scope: CoroutineScope) ->
        SummaryListViewModel(get(), get(), get(), scope)
    }
}
```

---

## Module Structure

### Project Layout

```
bite-size-reader-client/
├── shared/                  # Kotlin Multiplatform shared code
│   ├── commonMain/          # Shared code (Android + iOS)
│   ├── androidMain/         # Android-specific implementations
│   ├── iosMain/             # iOS-specific implementations
│   ├── desktopMain/         # Desktop stubs (Hot Reload only)
│   └── commonTest/          # Shared tests
├── composeApp/              # Android app
│   ├── androidMain/         # Android UI (Compose)
│   └── desktopMain/         # Desktop UI (Hot Reload only)
├── iosApp/                  # iOS app
│   ├── iosApp/              # iOS UI (SwiftUI)
│   ├── ShareExtension/      # Share Extension
│   └── RecentSummariesWidget/  # WidgetKit widget
└── docs/                    # Documentation
```

### Shared Module (`shared/`)

```
shared/src/commonMain/kotlin/
├── data/                    # Data layer
│   ├── local/              # Database, storage
│   ├── remote/             # API client, DTOs
│   └── repository/         # Repository implementations
├── domain/                  # Domain layer
│   ├── model/              # Business models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Business logic use cases
├── presentation/            # Presentation layer
│   ├── navigation/         # Decompose navigation
│   ├── state/              # UI states
│   └── viewmodel/          # ViewModels
├── util/                    # Utilities
│   ├── error/              # Error handling
│   └── network/            # Network utilities
└── di/                      # Dependency injection
```

### Android Module (`composeApp/`)

```
composeApp/src/androidMain/kotlin/
├── ui/                      # UI components
│   ├── screens/            # Full screens
│   ├── components/         # Reusable components
│   └── theme/              # Material 3 theme
├── widget/                  # Glance widgets
├── worker/                  # WorkManager background tasks
├── auth/                    # Telegram auth activity
└── App.kt                   # Main app composable
```

### iOS Module (`iosApp/`)

```
iosApp/
├── Views/                   # SwiftUI views
│   ├── Auth/               # Auth screens
│   ├── Summary/            # Summary screens
│   ├── Search/             # Search screen
│   └── Components/         # Reusable components
├── ViewModels/              # ViewModel wrappers
├── Helpers/                 # Swift utilities
├── ShareExtension/          # Share extension
└── RecentSummariesWidget/   # Widget extension
```

---

## Data Flow

### Read Flow (Get Summaries)

```
┌──────────┐      ┌───────────┐      ┌────────────┐      ┌──────────┐      ┌──────────┐
│   UI     │──1──→│ ViewModel │──2──→│  UseCase   │──3──→│Repository│──4──→│ Database │
│ (Compose)│      │           │      │            │      │          │      │          │
└──────────┘      └───────────┘      └────────────┘      └──────────┘      └──────────┘
     ↑                                                           │                 │
     │                                                           5                 │
     │                                                           ↓                 │
     └───────────────────────────────────────────────────────  API ←──────────────┘
                                                                 ↓
                                                              Server
```

1. User scrolls → UI requests more summaries
2. ViewModel calls UseCase
3. UseCase calls Repository
4. Repository checks local DB first
5. If stale/missing, fetches from API
6. Saves to DB
7. Returns Flow to ViewModel
8. ViewModel updates State
9. UI re-renders

### Write Flow (Submit URL)

```
┌──────────┐      ┌───────────┐      ┌────────────┐      ┌──────────┐      ┌──────────┐
│   UI     │──1──→│ ViewModel │──2──→│  UseCase   │──3──→│Repository│──4──→│   API    │
└──────────┘      └───────────┘      └────────────┘      └──────────┘      └──────────┘
     ↑                                                           │                 │
     │                                                           5                 │
     │                                                           ↓                 ↓
     └───────────────────────────────────────────────────────Database          Server
                                                                                   │
                                                                                   6
                                                                                   ↓
                                                                              Processing
```

1. User submits URL
2. ViewModel calls SubmitURLUseCase
3. UseCase validates and calls Repository
4. Repository sends to API
5. Saves pending request to DB (offline-first)
6. Server processes (async)
7. Polling/WebSocket for status updates
8. Summary saved to DB when ready
9. UI notified via Flow

### Offline-First Strategy

```
Request → Repository → Check DB → Data exists?
                           ├─ Yes → Return from DB
                           └─ No  → Queue for sync
                                    └─ Network available?
                                        ├─ Yes → Fetch immediately
                                        └─ No  → Wait for connection
```

**Key Principles**:
- Always read from DB first
- Write to DB immediately (optimistic updates)
- Sync with server in background
- Handle conflicts gracefully

---

## Platform-Specific Details

### Android

#### Jetpack Compose UI

```kotlin
@Composable
fun SummaryListScreen(
    viewModel: SummaryListViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn {
        items(state.summaries) { summary ->
            SummaryCard(summary) { viewModel.navigateToDetail(summary.id) }
        }
    }
}
```

#### Dependency Injection (Koin)

```kotlin
// Initialize in Application.onCreate()
startKoin {
    androidContext(this@BiteSizeReaderApp)
    modules(androidModule, sharedModules)
}

// Inject in Composable
val viewModel: SummaryListViewModel = koinInject()
```

#### Platform Features

- **Share Intent**: Receives URLs from other apps
- **WorkManager**: Background sync every 6 hours
- **Glance Widgets**: Show recent summaries on home screen
- **Encrypted Storage**: Token storage using EncryptedSharedPreferences

### iOS

#### SwiftUI UI

```swift
struct SummaryListView: View {
    @StateObject var viewModel: SummaryListViewModelWrapper

    var body: some View {
        List(viewModel.summaries, id: \.id) { summary in
            SummaryCardView(summary: summary)
        }
    }
}
```

#### Kotlin/Swift Interop (SKIE)

```swift
// Flow → AsyncSequence
for await summaries in viewModel.summariesFlow {
    self.summaries = summaries
}

// Suspend functions → async/await
let summary = try await useCase.getSummary(id: 123)
```

#### Platform Features

- **Share Extension**: Receives URLs from Safari/apps
- **BackgroundTasks**: Background sync (BGProcessingTask)
- **WidgetKit**: Show recent summaries on home screen
- **Keychain**: Secure token storage

### Cross-Platform Navigation (Decompose)

```kotlin
// Shared navigation component
class RootComponent(
    componentContext: ComponentContext
) {
    private val navigation = StackNavigation<Screen>()

    val stack = childStack(
        source = navigation,
        serializer = Screen.serializer(),
        childFactory = ::createChild
    )

    fun navigateToSummaryDetail(id: Int) {
        navigation.push(Screen.SummaryDetail(id))
    }
}
```

**Android** (Compose):
```kotlin
Children(stack = rootComponent.stack) { child ->
    when (val screen = child.instance) {
        is Screen.SummaryDetail -> SummaryDetailScreen(screen.id)
    }
}
```

**iOS** (SwiftUI):
```swift
switch screen {
case let screen as Screen.SummaryDetail:
    SummaryDetailView(summaryId: screen.id)
}
```

---

## Dependency Management

### Gradle Version Catalog (`libs.versions.toml`)

All dependencies managed centrally:

```toml
[versions]
kotlin = "2.2.10"
ktor = "3.3.2"

[libraries]
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }

[plugins]
kotlinMultiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
```

**Benefits**:
- Single source of truth
- Easy version updates
- Dependency grouping
- IDE autocomplete

### Platform-Specific Dependencies

**Android**:
```kotlin
android Main.dependencies {
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.glance.appwidget)
}
```

**iOS** (via CocoaPods/SPM):
- Managed through Xcode project
- SKIE plugin handles Kotlin/Swift interop

---

## Security

### Authentication

- **Method**: Telegram OAuth
- **Token Storage**:
  - Android: EncryptedSharedPreferences
  - iOS: Keychain
- **Token Refresh**: Handled automatically by API

### Data Protection

| Data Type | Android | iOS |
|-----------|---------|-----|
| Auth Token | Encrypted SharedPreferences | Keychain |
| Database | SQLite (no encryption) | SQLite (no encryption) |
| Network | HTTPS only | HTTPS only |

**Note**: Database encryption can be added with SQLCipher if needed.

### API Security

- HTTPS-only connections
- Bearer token authentication
- Request/response validation
- Rate limiting handled by server

---

## Performance

### Optimizations

1. **Database Indices**
   ```sql
   CREATE INDEX idx_summaries_created_at ON summaries(created_at);
   CREATE INDEX idx_summaries_is_read ON summaries(is_read);
   ```

2. **Pagination**
   - Load 20 items at a time
   - Infinite scroll with offset-based pagination

3. **Image Loading**
   - Coil with disk/memory caching (Android)
   - Native image caching (iOS)

4. **Background Sync**
   - WorkManager (Android): Network + battery constraints
   - BackgroundTasks (iOS): System-managed scheduling

5. **Lazy Loading**
   - LazyColumn/LazyRow (Android)
   - SwiftUI List with LazyVStack (iOS)

### Benchmarks

| Metric | Target | Current |
|--------|--------|---------|
| Cold Start | < 2s | ~1.5s |
| Screen Load | < 500ms | ~300ms |
| List Scroll | 60 FPS | 60 FPS |
| Memory Usage | < 100 MB | ~70 MB |
| APK Size | < 10 MB | ~8 MB |

---

## Design Decisions

### Why Kotlin Multiplatform?

**Pros**:
- 90% code sharing (business logic)
- Native UI performance
- Single team can maintain both platforms
- Type safety across platforms
- Existing Kotlin ecosystem

**Cons**:
- iOS requires Xcode configuration
- Limited iOS library support
- Learning curve for iOS developers

**Conclusion**: Benefits outweigh costs for our use case.

### Why Decompose for Navigation?

**Alternatives Considered**:
- Voyager
- Appyx
- Native navigation (separate implementations)

**Why Decompose**:
- Lifecycle-aware
- Type-safe navigation
- Works with both Compose and SwiftUI
- Mature and stable

### Why SQLDelight?

**Alternatives Considered**:
- Room (Android-only)
- Realm (cross-platform but proprietary)
- Core Data (iOS-only)

**Why SQLDelight**:
- Cross-platform
- Type-safe SQL
- Compile-time query validation
- Native performance
- Open source

### Why Koin?

**Alternatives Considered**:
- Dagger/Hilt (Android-only, complex)
- Manual DI

**Why Koin**:
- KMP support
- Simple DSL
- Runtime DI (faster builds)
- Easy testing

### Why Offline-First?

**Rationale**:
- Better UX (instant load)
- Works without network
- Reduced server load
- Smoother animations (no loading spinners)

**Trade-offs**:
- More complex sync logic
- Conflict resolution needed
- Database management overhead

---

## Future Architecture Plans

### Planned Improvements

1. **Modularization**: Split into feature modules
2. **Paging 3**: Advanced pagination for large lists
3. **Database Encryption**: SQLCipher integration
4. **GraphQL**: Replace REST API
5. **WebSocket**: Real-time updates
6. **Multi-Module**: Separate feature modules

### Migration Path

1. Keep current architecture working
2. Add new features in modular structure
3. Gradually migrate existing features
4. Maintain backward compatibility

---

## References

### Documentation
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Decompose](https://arkivanov.github.io/Decompose/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin](https://insert-koin.io/)

### Architecture Patterns
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [MVI Pattern](https://hannesdorfmann.com/android/mosby3-mvi-1/)
- [Repository Pattern](https://developer.android.com/jetpack/guide/data-layer#architecture)

---

**Created**: 2025-11-17
**Last Updated**: 2025-11-17
**Maintained By**: Development Team
