# AGENTS.md

Project guidance for Codex when working in this repository.

## Project Snapshot

- Kotlin Multiplatform app with infrastructure in `core/`, feature modules under `feature/*`, a thin shared bootstrap/navigation shell in `shared/`, shared Compose UI in `composeApp/`, and a SwiftUI host app in `iosApp/`.
- Active product areas include summaries, search, collections, reading goals, recommendations, stats, settings, digests, RSS, tags, rules, backup, and import/export flows.
- Desktop exists as a development target for Compose work and hot reload, not as a production app.

## Build And Run

```bash
# Full build
./gradlew build

# Android
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug

# Desktop Compose development
./gradlew :composeApp:hotRunDesktop

# Shared tests
./gradlew :shared:allTests

# Android unit tests
./gradlew :composeApp:testDebugUnitTest

# iOS shared tests
./gradlew :shared:iosSimulatorArm64Test

# Code quality
./gradlew ktlintCheck detekt
./gradlew ktlintFormat

# Coverage
./gradlew koverHtmlReportDebug

# iOS workspace
open iosApp/iosApp.xcworkspace
```

Use the workspace, not just `iosApp.xcodeproj`, when CocoaPods integration matters.

## Configuration

`local.properties` is gitignored. Common overrides:

```properties
api.base.url=https://bitsizereaderapi.po4yka.com
api.logging.enabled=false
telegram.bot.username=bitesizereader_bot
telegram.bot.id=
client.id=android-app-v1.0
api.timeout.seconds=30
```

Shared runtime config is centralized in `core/src/commonMain/kotlin/com/po4yka/bitesizereader/util/config/AppConfig.kt`.

## Architecture

- Modules are split by responsibility:
  - `core/` for cross-feature infrastructure, transport, persistence, shared domain models, config, and platform bindings
  - `feature/auth`, `feature/collections`, `feature/digest`, `feature/settings`, `feature/summary`, `feature/sync` for feature-owned repositories, use cases, state, ViewModels, and Decompose components
  - `shared/` for Koin bootstrap, root/main navigation shells, and CocoaPods export glue
  - `composeApp/` for Compose UI, Android entrypoints, desktop dev target, and the app-level image loading/provider glue
- Navigation is Decompose-based. Components own routed-screen dependencies and ViewModel creation.
- ViewModels extend `BaseViewModel` and are normally retained from components via `retainedInstance { get() }`.
- Complex screens use nested state plus delegate collaborators rather than one oversized ViewModel. Current examples: `SettingsViewModel` and `SummaryDetailViewModel`.
- Compose UI lives in `composeApp/src/commonMain/kotlin/.../ui`, with screens consuming a `*Component` instead of resolving dependencies or navigation directly.
- Domain contracts and UI code must not import `data.remote` APIs or DTOs. Transport types stay in data layers and are mapped to domain models before crossing a boundary.

See `shared/AGENTS.md` for DI and shared-layer rules. See `composeApp/AGENTS.md` for UI rules.

## Dependency Injection

Default rule in `core/` and `feature/*` code:

- `data/remote/` and `data/repository/`: `@Single`
- `domain/usecase/`: `@Factory`
- `presentation/viewmodel/` and delegates: `@Factory`
- module scanners: `@Module` + `@ComponentScan`

Important exceptions already exist and are valid:

- `core/src/iosMain/.../di/IosModule.kt` uses Koin DSL because the generated `.module` extensions are not visible from `iosMain`.
- `composeApp/src/commonMain/.../di/ImageLoaderModule.kt` uses DSL for UI-only wiring.
- tests may use DSL to provide fakes and overrides.

Do not "fix" those exceptions by force-converting them to annotations without understanding the source-set limitations.

`shared/.../di/KoinInitializer.kt` is the bootstrap entry point that stitches `commonModules()` and `platformModules()` together.

## Platform Notes

### Android

- Secure storage uses Tink AEAD + DataStore, not `EncryptedSharedPreferences`.
- Networking uses OkHttp.
- Background work uses WorkManager.
- Widgets use Glance and are exempt from Carbon UI rules.

### iOS

- Secure storage uses `KeychainSettings`.
- Networking uses the Darwin Ktor engine.
- App startup and background sync live in `iosApp/iosApp/iOSApp.swift`.
- Share and widget source lives under `iosApp/ShareExtension` and `iosApp/RecentSummariesWidget`; keep their app-group and deep-link contracts aligned with the main app.

### Swift Interop

The SKIE plugin is configured in Gradle but currently disabled in `shared/build.gradle.kts` because the active Kotlin version is ahead of supported SKIE versions. Do not assume new SKIE-generated APIs are available until that flag is re-enabled.

## Auth And Sync

- HTTP auth is handled by Ktor `Auth` bearer refresh in `shared/.../data/remote/ApiClient.kt`.
- HTTP auth is handled by Ktor `Auth` bearer refresh in `core/.../data/remote/ApiClient.kt`.
- Token refresh calls `POST v1/auth/refresh` and updates `SecureStorage`.
- Sync is session-based and implemented in `SyncRepositoryImpl`; use `SyncRepository`/use cases instead of ad hoc sync logic in UI layers.

## Common Change Patterns

### Add A Screen

1. Add or extend state in `shared/.../presentation/state/`.
2. Add a ViewModel in the owning `feature/.../presentation/viewmodel/` module extending `BaseViewModel`.
3. Add a Decompose component in the owning `feature/.../presentation/navigation/` module.
4. Register the child in `MainComponent` or `DefaultRootComponent`.
5. Add the Compose screen in `composeApp/.../ui/screens/`.
6. Add strings in `composeApp/src/commonMain/composeResources/values/strings.xml` and `values-ru/strings.xml`.

### Add A Repository Flow

1. Define the contract in `domain/repository/`.
2. Add DTOs under `core/.../data/remote/dto/` if the backend shape changes.
3. Add feature-owned mappers in `data/mappers/`.
4. Add the implementation in the owning feature `data/repository/` with `@Single(binds = [...])`.
5. Keep API details in `core/.../data/remote/`; keep DTOs and API types out of domain and UI layers.

### Add Shared UI Behavior

- Prefer project components in `composeApp/.../ui/components/` before inventing new patterns.
- Use Compose Resources instead of hardcoded UI text.
- Keep accessibility semantics in mind; the repo already uses headings and live regions in screens such as `SummaryListScreen`.
