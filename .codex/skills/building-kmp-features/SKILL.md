---
name: building-kmp-features
description:
  Guides feature work in this Ratatoskr Kotlin Multiplatform project.
  Use when adding or refactoring screens, ViewModels, Decompose components,
  repositories, use cases, DTOs, mappers, or Material 3 / AppTheme Compose UI.
  Covers the current core/* + feature/* DI rules, component retention pattern,
  Compose Resources, and the repo's valid Koin DSL exceptions.
user-invocable: false
---

# Building KMP Features

Use this skill for day-to-day feature work in the KMP + Compose stack.

## Module Layout

Production code is split across two layers:

- **`core/{common,data,navigation,ui}`** — cross-feature infrastructure:
  domain primitives, networking, persistence, secure storage, navigation
  contracts, shared UI components.
- **`feature/{auth,collections,digest,settings,summary,sync}`** — each
  feature owns its own domain models, repositories, use cases, DTOs,
  mappers, presentation state, ViewModels, Decompose components, and UI
  screens.

The `composeApp/` module is the shell host: it composes the navigation
graph, exports the iOS framework via CocoaPods, and serves as the
desktop dev target. Feature UI lives in feature modules, not composeApp.

The `androidApp/` module contains the Android `Application`, `Activity`,
widgets, and WorkManager workers — entrypoints only.

There is no `shared/` Gradle module. References to `shared/` in older
docs or git history describe the pre-migration layout.

## Default Flow For A New Feature Slice

1. **Domain contract** in `feature/<name>/src/commonMain/kotlin/com/po4yka/ratatoskr/<name>/domain/`
   (model + repository interface + use cases). Types that genuinely
   belong cross-feature go in `core/common` instead.
2. **Data implementation** in `feature/<name>/src/commonMain/kotlin/com/po4yka/ratatoskr/<name>/data/`
   (DTOs under `data/remote/dto/`, mappers under `data/mappers/`, API
   classes under `data/remote/`, repository impl under `data/repository/`).
3. **Presentation state + ViewModel** in `feature/<name>/src/commonMain/kotlin/com/po4yka/ratatoskr/<name>/presentation/`
   (state under `presentation/state/`, ViewModels under
   `presentation/viewmodel/`, Decompose components under
   `presentation/navigation/`).
4. **Compose screens** in `feature/<name>/src/commonMain/kotlin/com/po4yka/ratatoskr/feature/<name>/ui/screens/`.
5. **Localised strings + assets** in `core/ui/src/commonMain/composeResources/values/strings.xml`
   (and `values-ru/strings.xml`). Do not put feature strings in
   `androidApp/`; those resources are not shared with iOS or Desktop.
6. **Route registration**: expose the feature's route entry from the
   feature module, then wire it into the shell via the navigation graph
   in `composeApp/.../presentation/navigation/`.

## Add Or Refactor A Screen

1. Add or update the state class in `feature/<name>/.../presentation/state/`.
2. Add or update the ViewModel in `feature/<name>/.../presentation/viewmodel/`.
3. Keep complex state nested when the screen has multiple concerns;
   use delegate collaborators rather than one bloated ViewModel.
4. Add a Decompose component interface plus `Default<Name>Component`
   in `feature/<name>/.../presentation/navigation/`.
5. Create the screen in `feature/<name>/.../ui/screens/`.
6. Wire the route through the shell in `composeApp/`.
7. Add strings in `core/ui/src/commonMain/composeResources/values/strings.xml`
   and `values-ru/strings.xml`.

### ViewModel Rules

- Extend `BaseViewModel` (in `core/common`).
- Keep `_state` private and expose `asStateFlow()`.
- Use `viewModelScope` for async work.
- Components retain ViewModels with `retainedInstance { get() }`.
- For large screens, prefer delegate collaborators over a single
  bloated ViewModel.

Reference implementations:

- `feature/settings/.../SettingsViewModel`
- `feature/summary/.../SummaryDetailViewModel`

## Add A Repository Or API Flow

1. Define the repository contract in
   `feature/<name>/.../domain/repository/`.
2. Add DTOs under `feature/<name>/.../data/remote/dto/` if the backend
   shape changes.
3. Add mappers under `feature/<name>/.../data/mappers/`.
4. Add API interfaces/implementations under `feature/<name>/.../data/remote/`.
5. Add the repository implementation under
   `feature/<name>/.../data/repository/`.
6. Bind implementations with `@Single(binds = [...])`.
7. Add use cases in `feature/<name>/.../domain/usecase/`.

Keep transport details in `data/remote/`; do not leak Ktor or DTO types
into ViewModels or Compose UI. Domain code in any feature must not
import another feature's `data/` or `presentation/` packages — only
its public domain contracts.

## DI Rules

Default rule set:

- `data/remote/`: `@Single`
- `data/repository/`: `@Single`
- `domain/usecase/`: `@Factory`
- `presentation/viewmodel/`: `@Factory`
- scanning/provider modules: `@Module` + `@ComponentScan` or provider
  methods

Valid exceptions already in the repo:

- `core/data/src/iosMain/kotlin/com/po4yka/ratatoskr/di/IosModule.kt`
  uses Koin DSL because generated `.module` extensions are not visible
  from `iosMain`.
- `composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/di/ImageLoaderModule.kt`
  uses DSL for UI-only wiring.
- Tests may use DSL for fake bindings and verification.

Do not "normalize" those files into annotations unless the source-set
constraint is removed.

`composeApp/.../di/KoinInitializer.kt` is the active bootstrap entry
point. Platform actuals expose `appModules()` plus `platformModules()`.

## UI Rules

For component usage and project-local UI anchors, read
[references/app-components.md](references/app-components.md).

Core rules:

- Use `RatatoskrTheme` (in `core/ui/.../theme/Theme.kt`).
- Use `AppTheme.colors.*` for surfaces, icons, and semantic colors.
- Use `AppTheme.type.*` for typography.
- Use Material 3 `Text` and `Icon` for primitives.
- Reuse existing components from `core/ui/.../components/` before
  introducing new ones.
- Use Compose Resources for text. The generated `Res` accessor lives in
  `ratatoskr.core.ui.generated.resources.*`.
- Keep accessibility semantics for headings, progress, and live updates.
- Routed Composables must not call `koinInject()` directly — read
  routed-screen dependencies from the Decompose component or an
  app-level provider.

## File Map

```text
core/common/src/commonMain/kotlin/com/po4yka/ratatoskr/
  domain/             ← cross-feature domain primitives
  presentation/       ← BaseViewModel and shared presentation utilities
  util/               ← config (AppConfig.kt), errors, platform helpers

core/data/src/commonMain/kotlin/com/po4yka/ratatoskr/
  data/local/         ← SecureStorage contract + DB driver
  data/remote/        ← Ktor ApiClient + bearer auth refresh
  database/           ← SQLDelight generated bindings
core/data/src/commonMain/sqldelight/com/po4yka/ratatoskr/database/
  Database.sq         ← SQLDelight schema (do not edit lightly)

core/navigation/src/commonMain/kotlin/com/po4yka/ratatoskr/
  navigation/         ← route contracts (RootNavigation, MainNavigation)

core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/
  components/         ← shared Compose components (SummaryCard, etc.)
  icons/              ← AppIcons.kt
  theme/              ← RatatoskrTheme + design tokens
core/ui/src/commonMain/composeResources/
  values/strings.xml
  values-ru/strings.xml

feature/<name>/src/commonMain/kotlin/com/po4yka/ratatoskr/
  <name>/data/remote/{,dto,mappers}
  <name>/data/repository/
  <name>/domain/{model,repository,usecase}
  feature/<name>/ui/screens/        ← Compose screens for this feature
  presentation/{state,viewmodel,navigation}
  di/                                ← @ComponentScan module

composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/
  App.kt              ← root composable
  app/                ← AppCompositionRoot, launch action handling
  di/                 ← KoinInitializer + ImageLoaderModule (DSL)
  presentation/navigation/  ← MainComponent, RootComponent, shell
  ui/screens/         ← MainScreen only (allowed shell host)

androidApp/src/main/kotlin/com/po4yka/ratatoskr/
  RatatoskrApp.kt     ← Application class
  MainActivity.kt
  widget/             ← Glance widgets
  worker/             ← WorkManager workers

iosApp/iosApp/        ← SwiftUI host around the ComposeApp framework
iosApp/ShareExtension/
iosApp/RecentSummariesWidget/
```
