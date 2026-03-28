# Architecture

Last updated: 2026-03-28

## Overview

Bite-Size Reader is a Kotlin Multiplatform app with:

- `core/` for cross-feature infrastructure, transport, persistence, config, and shared domain models
- `feature/auth`, `feature/collections`, `feature/digest`, `feature/settings`, `feature/summary`, `feature/sync` for feature-owned repositories, use cases, state, ViewModels, and Decompose components
- `shared/` for Koin bootstrap, root/main navigation shells, and CocoaPods export glue
- `composeApp/` for Compose UI, Android entrypoints, the desktop dev target, and app-level UI providers
- `iosApp/` for the SwiftUI host app plus native share/widget source

## Dependency Direction

Allowed direction:

`composeApp` -> `shared` -> `feature/*` -> `core`

Additional rules:

- Feature modules may depend on `core` and, where justified, on another feature's public API.
- `shared/` should not regain business logic or repository implementations.
- `core/` must not depend on feature modules.

## Boundaries

These rules are mandatory:

- Domain contracts must not import `data.remote` APIs or DTOs.
- UI code must not import transport DTOs.
- Routed screen dependencies come from Decompose components, not `koinInject` inside the screen.
- Reusable composables consume app-level providers or explicit parameters, not Koin directly.
- Data/transport types stay in `core/.../data/remote`; feature data layers map them to domain models before returning them.

## Dependency Injection

Default rule set:

- `data/remote/` and `data/repository/`: `@Single`
- `domain/usecase/`: `@Factory`
- `presentation/viewmodel/` and delegates: `@Factory`
- module scanners: `@Module` + `@ComponentScan`

Valid DSL exceptions:

- `core/src/iosMain/.../di/IosModule.kt`
- `composeApp/src/commonMain/.../di/ImageLoaderModule.kt`
- tests and verification modules

`shared/.../di/KoinInitializer.kt` aggregates `commonModules()` and `platformModules()`.

## Navigation And UI

- Navigation is Decompose-based.
- Feature components own retained ViewModel instances.
- `composeApp` screens render feature state and call component methods or ViewModel intents.
- Cross-cutting UI glue, such as image URL transformation for proxied images, is provided once at the app layer and consumed by reusable composables.

## Sync

- `feature/sync` owns sync orchestration.
- `SyncRepositoryImpl` remains the external implementation entry point for `SyncRepository`.
- Sync internals should stay split by responsibility: public progress/cancel coordination, full sync, delta sync, pending-operation flushing, and per-entity apply logic.

## Platform Notes

Android:

- Secure storage uses Tink AEAD + DataStore.
- Networking uses OkHttp.
- Background work uses WorkManager.

iOS:

- Secure storage uses `KeychainSettings`.
- Networking uses the Darwin Ktor engine.
- App startup and background sync live in `iosApp/iosApp/iOSApp.swift`.
- Share/widget source must share the same app-group and deep-link contract as the main app.

Swift interop:

- SKIE is configured in Gradle but currently disabled because the active Kotlin version is ahead of the supported SKIE version.
