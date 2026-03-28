# shared/AGENTS.md

Guidance for work inside the shared bootstrap/navigation module.

## Structure

`shared/` is intentionally thin:

- `presentation/navigation`: root and main Decompose shells that stitch feature components together
- `di`: Koin bootstrap and expect/actual module aggregation
- platform source sets: per-platform `commonModules()` or `platformModules()` wiring for the exported framework

Business logic, repositories, use cases, and feature ViewModels now live in `core/` and `feature/*`, not in `shared/`.

## DI Rules

Default rule set for code reached from `shared/`:

- API implementations: `@Single`
- repositories: `@Single`
- use cases: `@Factory`
- ViewModels and delegates: `@Factory`
- scanners/providers: `@Module` classes with `@ComponentScan` or provider methods

Use `binds = [...]` whenever an implementation backs a domain interface. Do not reintroduce feature logic into `shared/`.

### Valid Exceptions

- `core/src/iosMain/.../di/IosModule.kt` uses DSL because generated `.module` extensions are not visible from `iosMain`.
- tests may use DSL modules for verification and overrides.

Do not document the shared layer as "annotations only"; the real rule is "annotations by default, DSL where source-set or test constraints require it".

## Boundary Rules

- `shared/` should depend on feature/public interfaces only; it should not own transport, DTO, or repository implementation code.
- Routed screens get dependencies from Decompose components. Keep `koinInject` out of routed screen composables.
- Domain contracts must not import `data.remote` APIs or DTOs.

## Sync And Auth

- Auth refresh is implemented in `core/.../data/remote/ApiClient.kt`.
- Sync orchestration lives in `feature/sync/.../data/repository/SyncRepositoryImpl.kt`.
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
