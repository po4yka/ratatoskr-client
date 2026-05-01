# AGENTS.md

Project guidance for Codex when working in this repository.

## Project Snapshot

- Kotlin Multiplatform app with shared infrastructure in `core/`, feature modules under `feature/*`, a Compose shell in `composeApp/`, an Android application host in `androidApp/`, and a SwiftUI host app in `iosApp/`.
- Active Kotlin modules are `core/common`, `core/data`, `core/navigation`, `core/ui`, `feature/auth`, `feature/collections`, `feature/digest`, `feature/settings`, `feature/summary`, and `feature/sync`.
- Desktop exists as a development target for Compose work and hot reload, not as a production app.

## Build And Run

```bash
# Full build
./gradlew build

# Android app
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug

# Desktop Compose development
./gradlew :composeApp:hotRunDesktop

# Module tests
./gradlew :core:common:allTests :core:data:allTests
./gradlew :feature:summary:allTests :feature:settings:allTests

# composeApp tests (KMP — covers Android, iOS sim, JVM/desktop)
./gradlew :composeApp:allTests

# Code quality
./gradlew ktlintCheck detekt
./gradlew ktlintFormat

# Coverage
./gradlew :core:common:koverHtmlReportDebug :core:data:koverHtmlReportDebug

# iOS workspace
open iosApp/iosApp.xcworkspace
```

Use the workspace, not just `iosApp.xcodeproj`, when CocoaPods integration matters.

## Configuration

`local.properties` is gitignored. Common overrides:

```properties
api.base.url=https://api.ratatoskr.po4yka.com
# Optional Android release override. Must use HTTPS when set.
api.release.base.url=https://api.ratatoskr.po4yka.com
api.logging.enabled=false
telegram.bot.username=ratatoskr_client_bot
telegram.bot.id=
client.id=ratatoskr-android-v1.0
api.timeout.seconds=30
```

Shared runtime config is centralized in `core/common/src/commonMain/kotlin/com/po4yka/ratatoskr/util/config/AppConfig.kt`.

## Design System

Frost is the project-owned design system and is now live across all screens,
components, and platform widgets. Editorial monospace minimalism: two-color rule
(ink `#1C242C` / `#E8ECF0` dark + page `#F0F2F5` / `#12161C` dark), single
critical accent (spark `#DC3545`, never flips), 0 corner radius, no shadows,
no Material elevation. Canonical spec lives in `DESIGN.md` (DESIGN.md format,
https://github.com/google-labs-code/design.md) at the repo root — read it
before adding tokens, components, colors, shapes, or motion.

Frost primitives live in:
- `core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/components/frost/`
- `core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/components/foundation/`

## Architecture

- Modules are split by responsibility:
  - `core/common` for cross-feature domain models, config, errors, base ViewModel primitives, and platform abstractions
  - `core/data` for shared networking/bootstrap, persistence, SQLDelight, secure storage, generic API wrappers, and platform data bindings
  - `core/navigation` for route contracts and navigation-facing interfaces
  - `core/ui` for shared non-feature UI primitives
  - `feature/*` for feature-owned repositories, use cases, route factories, transport APIs/DTOs/mappers, state, ViewModels, and Decompose components
  - `composeApp/` for shared Compose UI, navigation shell composition, CocoaPods export, and the desktop dev target
  - `androidApp/` for Android activity/app/widget/worker entrypoints
- Navigation is Decompose-based. Feature components own routed-screen dependencies and retained ViewModel creation.
- Compose UI lives in `composeApp/src/commonMain/kotlin/.../ui`, with screens consuming a `*Component` or app-level provider instead of resolving Koin directly.
- Domain contracts and UI code must not import `data.remote` APIs or DTOs.
- One feature may depend on another feature's public contracts only. Do not import another feature's `data` or `presentation` packages.
- Current public feature edges include `summary -> auth/collections/sync`, `collections -> sync`, `digest -> summary`, and `settings -> auth/summary/sync`.

See `composeApp/AGENTS.md` for UI rules. See `docs/ARCHITECTURE.md` for the current dependency rules.

## Dependency Injection

Default rule in `core/` and `feature/*` code:

- `data/remote/` and `data/repository/`: `@Single`
- `domain/usecase/`: `@Factory`
- `presentation/viewmodel/` and delegates: `@Factory`
- module scanners: `@Module` + `@ComponentScan`

Important exceptions already exist and are valid:

- `core/data/src/iosMain/.../di/IosModule.kt` uses Koin DSL because the generated `.module` extensions are not visible from `iosMain`.
- `composeApp/src/commonMain/.../di/ImageLoaderModule.kt` uses DSL for UI-only wiring.
- tests may use DSL to provide fakes and overrides.

Do not "fix" those exceptions by force-converting them to annotations without understanding the source-set limitations.

`composeApp/.../di/KoinInitializer.kt` is the active bootstrap entry point. Platform actuals expose `appModules()` plus `platformModules()`.

## Platform Notes

### Android

- `androidApp/` is the Android application host module.
- Secure storage uses Tink AEAD + DataStore.
- Networking uses OkHttp.
- Background work uses WorkManager.
- Widgets use Glance with hardcoded Frost INK/PAGE color constants (exempt from AppTheme, but must follow Frost visual rules).

### iOS

- `iosApp/` is the SwiftUI host around the `ComposeApp` framework exported from `composeApp/`.
- Secure storage uses `KeychainSettings`.
- Networking uses the Darwin Ktor engine.
- App startup and background sync live in `iosApp/iosApp/iOSApp.swift`.
- Share and widget source lives under `iosApp/ShareExtension` and `iosApp/RecentSummariesWidget`; keep their app-group and deep-link contracts aligned with the main app.

### Swift Interop

The SKIE plugin is configured in Gradle but currently disabled in `composeApp/build.gradle.kts` because the active Kotlin version is ahead of supported SKIE versions. Do not assume new SKIE-generated APIs are available until that flag is re-enabled.

## Auth And Sync

- HTTP auth is handled by Ktor `Auth` bearer refresh in `core/data/.../data/remote/ApiClient.kt`.
- Token refresh calls `POST v1/auth/refresh` and updates `SecureStorage`.
- Sync is session-based and implemented in `feature/sync/.../data/repository/SyncRepositoryImpl.kt`.
- `feature/sync` owns orchestration only; feature-owned sync item appliers and pending-operation handlers are injected into it from the owning modules.

## Common Change Patterns

### Add A Screen

1. Add or extend state in the owning `feature/.../presentation/state/` package, or `core/common` only if the state is intentionally shared across features.
2. Add a ViewModel in the owning `feature/.../presentation/viewmodel/` module extending `BaseViewModel`.
3. Add a Decompose component in the owning `feature/.../presentation/navigation/` module.
4. Register the route entry or binding in the owning feature module, then connect it through the `composeApp` shell if needed.
5. Add the Compose screen in the owning feature module under `feature/.../feature/<name>/ui/screens/`.
6. Add shared strings and assets under `core/ui/src/commonMain/composeResources/`.

### Add A Repository Flow

1. Define the contract in `domain/repository/`.
2. Add DTOs under the owning feature module `data/remote/dto/` if the backend shape changes.
3. Add feature-owned mappers in the owning feature `data/mappers/`.
4. Add the implementation in the owning feature `data/repository/` with `@Single(binds = [...])`.
5. Keep API details in the owning feature `data/remote/`; keep DTOs and API types out of domain and UI layers.

### Add Shared UI Behavior

- Prefer Frost atoms (`BrutalistCard`, `BracketButton`, `BracketField`, `BracketSwitch`, `MultiSelectChip`, `StatusBadge`, `RowDigest`, `SectionHeading`, `Toast`, `IngestLine`, `PullQuote`, `AtomMark`, `FrostText`, `FrostIcon`, `FrostSpinner`, `FrostDialog`, `FrostScaffold`, `FrostSurface`, `FrostDivider`, `FrostCheckbox`, `FrostRadio`) in `core/ui/.../components/frost/` or `core/ui/.../components/foundation/` before inventing new patterns.
- Use Compose Resources instead of hardcoded UI text.
- Keep accessibility semantics in mind; the repo already uses headings and live regions in screens such as `SummaryListScreen`.
