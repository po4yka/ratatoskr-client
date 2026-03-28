# shared/AGENTS.md

Guidance for work inside the shared KMP module.

## Structure

`shared/src/commonMain/kotlin/com/po4yka/bitesizereader/` is organized into:

- `data/local`: SQLDelight, secure storage contracts, platform-facing persistence helpers
- `data/remote`: Ktor APIs, DTOs, gRPC client code, API client setup
- `data/mappers`: DTO/domain conversions
- `data/repository`: repository implementations
- `domain/model`: domain entities and enums
- `domain/repository`: repository contracts
- `domain/usecase`: application use cases
- `presentation/state`: screen state data classes and nested sub-states
- `presentation/viewmodel`: `BaseViewModel` subclasses and delegate collaborators
- `presentation/navigation`: Decompose components
- `di`: Koin module scanners and initialization
- `util`: config, error mapping, network, sharing, platform helpers

## DI Rules

Default shared-module rule set:

- API implementations: `@Single`
- repositories: `@Single`
- use cases: `@Factory`
- ViewModels and delegates: `@Factory`
- scanners/providers: `@Module` classes with `@ComponentScan` or provider methods

Use `binds = [...]` whenever an implementation backs a domain interface.

### Valid Exceptions

- `shared/src/iosMain/.../di/IosModule.kt` uses DSL because generated `.module` extensions are not visible from `iosMain`.
- tests may use DSL modules for verification and overrides.

Do not document the shared layer as "annotations only"; the real rule is "annotations by default, DSL where source-set or test constraints require it".

## ViewModel Pattern

- Extend `BaseViewModel`.
- Keep mutable state private and expose `asStateFlow()`.
- Launch work on `viewModelScope`.
- Let components own retention with `retainedInstance { get() }`.
- For large screens, prefer delegates plus nested sub-states instead of one monolithic ViewModel.

Current reference points:

- `SettingsViewModel` + `TelegramLinkingDelegate` / `SyncSettingsDelegate` / `AccountSettingsDelegate`
- `SummaryDetailViewModel` + `ReadingSessionDelegate` / `AudioDelegate` / `HighlightDelegate` / `FeedbackDelegate` / `CollectionDelegate`

## Sync And Auth

- Auth refresh is implemented in `data/remote/ApiClient.kt`.
- Sync orchestration lives in `data/repository/SyncRepositoryImpl.kt`.
- UI should talk through use cases and repositories, not manually call transport helpers.

## Platform Bindings

- Android secure storage: Tink AEAD + DataStore
- iOS secure storage: `KeychainSettings`
- Android engine: OkHttp
- iOS engine: Darwin
- platform DI bootstrapping happens through `KoinInitializer.kt` plus expect/actual `commonModules()` and `platformModules()`

## Source-Set Notes

- `commonMain` is the default home for business logic.
- `androidMain`, `iosMain`, and `desktopMain` only hold true platform bindings.
- SKIE is currently disabled in Gradle; do not add new code that depends on SKIE-only generation being active.
