# shared/AGENTS.md

`shared/` is legacy migration residue and is not part of the active Gradle build.

## Current Status

- Do not add new production code here.
- Active bootstrapping and navigation composition live in `composeApp/`.
- Active infrastructure lives in `core/*`.
- Active feature logic lives in `feature/*`.

## If You Must Touch This Directory

- Limit changes to migration cleanup or documentation.
- Do not reintroduce DI bootstrap, navigation shells, repositories, or feature ViewModels here.
- Keep any notes aligned with `docs/ARCHITECTURE.md` and root `AGENTS.md`.

## Active Replacements

- Koin bootstrap: `composeApp/.../di/KoinInitializer.kt`
- Route contracts: `core/navigation`
- Compose shell and root navigation: `composeApp/.../presentation/navigation`
- CocoaPods export: `composeApp/build.gradle.kts`
- Auth transport: `core/data/.../data/remote/ApiClient.kt`
- Sync orchestration: `feature/sync/.../data/repository/SyncRepositoryImpl.kt`
