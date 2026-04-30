# Architecture

Last updated: 2026-03-28

## Overview

Ratatoskr is a Kotlin Multiplatform app with:

- `core/common` for cross-feature domain models, config, errors, base presentation primitives, and platform abstractions
- `core/data` for shared networking/bootstrap, persistence, SQLDelight, secure storage, and generic API wrappers
- `core/navigation` for route contracts and navigation interfaces
- `core/ui` for shared non-feature UI primitives
- `feature/auth`, `feature/collections`, `feature/digest`, `feature/settings`, `feature/summary`, `feature/sync` for feature-owned repositories, use cases, state, ViewModels, and Decompose components
- `composeApp/` for shared Compose UI, shell navigation composition, CocoaPods export, and the desktop dev target
- `androidApp/` for Android application entrypoints, widgets, and workers
- `iosApp/` for the SwiftUI host app plus native share/widget source

Legacy migration residue remains in the repo but is off-graph.

## Dependency Direction

Allowed direction:

- `androidApp` -> `composeApp`
- `iosApp` -> `ComposeApp.framework`
- `composeApp` -> `feature/*` -> `core/*`

Additional rules:

- Feature modules may depend on `core/*` and only these cross-feature public contracts:
  - `feature/summary` -> `feature/auth`, `feature/collections`, `feature/sync`
  - `feature/collections` -> `feature/sync`
  - `feature/digest` -> `feature/summary`
  - `feature/settings` -> `feature/auth`, `feature/summary`, `feature/sync`
- Feature modules must not import another feature's `data` or `presentation` packages.
- `composeApp` shell code must not import feature implementation types from `data` or `presentation`.
- `core/*` must not depend on feature modules.

## Boundaries

These rules are mandatory:

- Domain contracts must not import `data.remote` APIs or DTOs.
- UI code must not import transport DTOs.
- Routed screen dependencies come from Decompose components, not `koinInject` inside the screen.
- Reusable composables consume app-level providers or explicit parameters, not Koin directly.
- Transport types stay in the owning feature `data/remote` packages; feature data layers map them to domain models before returning them.

## Dependency Injection

Default rule set:

- `data/remote/` and `data/repository/`: `@Single`
- `domain/usecase/`: `@Factory`
- `presentation/viewmodel/` and delegates: `@Factory`
- module scanners: `@Module` + `@ComponentScan`

Valid DSL exceptions:

- `core/data/src/iosMain/.../di/IosModule.kt`
- `composeApp/src/commonMain/.../di/ImageLoaderModule.kt`
- tests and verification modules

Active Koin bootstrap uses `composeApp/.../di/KoinInitializer.kt` plus platform `appModules()` and `platformModules()` actuals.

## Navigation And UI

- Navigation is Decompose-based.
- `core/navigation` owns route contracts and navigator-facing interfaces.
- Feature components own retained ViewModel instances and expose screen dependencies through component APIs.
- `composeApp` screens render feature state and call component methods or ViewModel intents.
- Cross-cutting UI glue, such as image URL transformation for proxied images, is provided once at the app layer and consumed by reusable composables.
- UI is built on the Frost design system. Material 3 substrate has been removed from commonMain. Foundation primitives in `core/ui/.../components/foundation/`; brand atoms in `core/ui/.../components/frost/`.

## Sync

- `feature/sync` owns sync orchestration, progress, cancellation, and session lifecycle.
- Entity-specific sync behavior is injected through feature-owned `SyncItemApplier` and `PendingOperationHandler` implementations.
- Adding a new syncable entity should not require hard-coding transport logic into `feature/sync`.

## Platform Notes

Android:

- `androidApp` is the Android host module.
- Secure storage uses Tink AEAD + DataStore.
- Networking uses OkHttp.
- Background work uses WorkManager.

iOS:

- `iosApp` consumes `ComposeApp` as the only exported Apple framework.
- Secure storage uses `KeychainSettings`.
- Networking uses the Darwin Ktor engine.
- App startup and background sync live in `iosApp/iosApp/iOSApp.swift`.
- Share/widget source must share the same app-group and deep-link contract as the main app.

Swift interop:

- SKIE is configured in Gradle but currently disabled because the active Kotlin version is ahead of the supported SKIE version.

## Implementation Patterns

The structural rules above describe *what depends on what*. The
patterns below show *how* a typical feature slice is implemented.

### MVI (Model-View-Intent)

```kotlin
// State
data class SummaryListState(
    val summaries: List<Summary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

// Intent / Event
sealed class SummaryListEvent {
    data object LoadSummaries : SummaryListEvent()
    data class MarkAsRead(val id: Int) : SummaryListEvent()
}

// ViewModel (extends BaseViewModel from core/common)
class SummaryListViewModel(
    private val getSummariesUseCase: GetSummariesUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SummaryListState())
    val state: StateFlow<SummaryListState> = _state.asStateFlow()

    fun onEvent(event: SummaryListEvent) {
        when (event) {
            is SummaryListEvent.LoadSummaries -> loadSummaries()
            is SummaryListEvent.MarkAsRead -> markAsRead(event.id)
        }
    }
}
```

- `_state` is private; the public surface is the read-only `StateFlow`.
- Async work is launched on `viewModelScope`.
- Decompose components retain ViewModels via `retainedInstance { get() }`.
- For complex screens, prefer delegate collaborators
  (`SettingsViewModel + TelegramLinkingDelegate / SyncSettingsDelegate / ...`)
  over a single bloated ViewModel.

See [`docs/VIEWMODEL_GUIDE.md`](VIEWMODEL_GUIDE.md) for the full pattern.

### Repository pattern

```kotlin
class SummaryRepositoryImpl(
    private val api: SummariesApi,
    private val database: Database,
) : SummaryRepository {

    override fun observeSummaries(): Flow<List<Summary>> =
        database.summaryQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map(SummaryEntity::toDomain) }

    override suspend fun refresh() {
        val response = api.getSummaries()
        database.summaryQueries.transaction {
            response.data?.summaries?.forEach { dto ->
                database.summaryQueries.upsert(dto.toEntity())
            }
        }
    }
}
```

- Reads are database-first; the UI subscribes to `Flow<List<Summary>>`
  and never blocks on the network.
- Writes go to the API, then optimistically update the DB before
  awaiting confirmation when the operation supports it.
- See [`docs/REPOSITORY_PATTERNS.md`](REPOSITORY_PATTERNS.md) for the
  offline-first contract details.

### Decompose navigation

```kotlin
interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class SummaryList(val component: SummaryListComponent) : Child()
        class SummaryDetail(val component: SummaryDetailComponent) : Child()
    }
}
```

- Each routed screen has a corresponding `*Component` interface plus
  a `Default<Name>Component` implementation in the feature's
  `presentation/navigation/` package.
- Components own retained ViewModel instances and expose screen
  dependencies through method APIs — Compose screens never call
  `koinInject()` directly.
- Cross-cutting UI providers (e.g. image URL transformation) are
  installed at the app layer in `composeApp/.../App.kt` and consumed
  by reusable composables via `LocalXxx` providers.
