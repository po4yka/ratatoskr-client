# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Project Snapshot

- Kotlin Multiplatform app with shared business logic in `shared/`, shared Compose UI in `composeApp/`, and a SwiftUI host app in `iosApp/`.
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
# Optional Android release override. Must use HTTPS when set.
api.release.base.url=https://bitsizereaderapi.po4yka.com
api.logging.enabled=false
telegram.bot.username=bitesizereader_bot
telegram.bot.id=
client.id=android-app-v1.0
api.timeout.seconds=30
```

Shared runtime config is centralized in `core/common/src/commonMain/kotlin/com/po4yka/bitesizereader/util/config/AppConfig.kt`.

## Architecture

- Shared code follows `data/`, `domain/`, `presentation/`, `di/`, and `util/` layering.
- Navigation is Decompose-based. Components live in `shared/.../presentation/navigation` and own ViewModel creation.
- ViewModels extend `BaseViewModel` and are normally retained from components via `retainedInstance { get() }`.
- Complex screens use nested state plus delegate collaborators rather than one oversized ViewModel. Current examples: `SettingsViewModel` and `SummaryDetailViewModel`.
- Compose UI lives in `composeApp/src/commonMain/kotlin/.../ui`, with screens usually consuming a `*Component` instead of resolving navigation directly.

See `shared/CLAUDE.md` for DI and shared-layer rules. See `composeApp/CLAUDE.md` for UI rules.

## Dependency Injection

Default rule in shared code:

- `data/remote/` and `data/repository/`: `@Single`
- `domain/usecase/`: `@Factory`
- `presentation/viewmodel/` and delegates: `@Factory`
- module scanners: `@Module` + `@ComponentScan`

Important exceptions already exist and are valid:

- `shared/src/iosMain/.../di/IosModule.kt` uses Koin DSL because the generated `.module` extensions are not visible from `iosMain`.
- `composeApp/src/commonMain/.../di/ImageLoaderModule.kt` uses DSL for UI-only wiring.
- tests may use DSL to provide fakes and overrides.

Do not "fix" those exceptions by force-converting them to annotations without understanding the source-set limitations.

`shared/.../di/KoinInitializer.kt` is the entry point that stitches `commonModules()` and `platformModules()` together.

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
- Share Extension and WidgetKit integration are native Swift targets around the shared app.

### Swift Interop

The SKIE plugin is configured in Gradle but currently disabled in `shared/build.gradle.kts` because the active Kotlin version is ahead of supported SKIE versions. Do not assume new SKIE-generated APIs are available until that flag is re-enabled.

## Auth And Sync

- HTTP auth is handled by Ktor `Auth` bearer refresh in `shared/.../data/remote/ApiClient.kt`.
- Token refresh calls `POST v1/auth/refresh` and updates `SecureStorage`.
- Sync is session-based and implemented in `SyncRepositoryImpl`; use `SyncRepository`/use cases instead of ad hoc sync logic in UI layers.

## Common Change Patterns

### Add A Screen

1. Add or extend state in `shared/.../presentation/state/`.
2. Add a ViewModel in `shared/.../presentation/viewmodel/` extending `BaseViewModel`.
3. Add a Decompose component in `shared/.../presentation/navigation/`.
4. Register the child in `MainComponent` or `DefaultRootComponent`.
5. Add the Compose screen in `composeApp/.../ui/screens/`.
6. Add strings in `composeApp/src/commonMain/composeResources/values/strings.xml` and `values-ru/strings.xml`.

### Add A Repository Flow

1. Define the contract in `domain/repository/`.
2. Add DTOs under `data/remote/dto/` if needed.
3. Add mappers in `data/mappers/`.
4. Add the implementation in `data/repository/` with `@Single(binds = [...])`.
5. Keep API details in `data/remote/`; keep ViewModels free of transport concerns.

### Add Shared UI Behavior

- Prefer project components in `composeApp/.../ui/components/` before inventing new patterns.
- Use Compose Resources instead of hardcoded UI text.
- Keep accessibility semantics in mind; the repo already uses headings and live regions in screens such as `SummaryListScreen`.
