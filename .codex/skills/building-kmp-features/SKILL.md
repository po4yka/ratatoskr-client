---
name: building-kmp-features
description:
  Guides feature work in this Ratatoskr Kotlin Multiplatform project.
  Use when adding or refactoring screens, ViewModels, Decompose components,
  repositories, use cases, DTOs, mappers, or Carbon-based Compose UI. Covers
  the current shared-module DI rules, component retention pattern, Compose
  Resources, and the repo's valid Koin DSL exceptions.
metadata:
  short-description: Build KMP features in this repo
---

# Building KMP Features

Use this skill for day-to-day feature work in the shared KMP + Compose stack.

## Shared Architecture

Default flow:

1. domain contract in `shared/.../domain/`
2. data implementation in `shared/.../data/`
3. state + ViewModel in `shared/.../presentation/`
4. Decompose component in `shared/.../presentation/navigation/`
5. Compose screen/components in `composeApp/.../ui/`
6. localized strings in `composeApp/src/commonMain/composeResources/`

## Add Or Refactor A Screen

1. Add or update state in `presentation/state/`.
2. Add or update the ViewModel in `presentation/viewmodel/`.
3. Keep complex state nested when the screen has multiple concerns.
4. Add a Decompose component interface plus `Default...Component`.
5. Create the screen in `composeApp/ui/screens/`.
6. Register the route in `MainComponent` or `DefaultRootComponent`.
7. Add strings in both `values/strings.xml` and `values-ru/strings.xml`.

### ViewModel Rules

- Extend `BaseViewModel`.
- Keep `_state` private and expose `asStateFlow()`.
- Use `viewModelScope` for async work.
- Components should retain ViewModels with `retainedInstance { get() }`.
- For large screens, prefer delegate collaborators over a single bloated ViewModel.

Reference implementations:

- `SettingsViewModel`
- `SummaryDetailViewModel`

## Add A Repository Or API Flow

1. Define the repository contract in `domain/repository/`.
2. Add DTOs under `data/remote/dto/` if the backend shape changes.
3. Add mappers under `data/mappers/`.
4. Add API interfaces/implementations under `data/remote/`.
5. Add the repository implementation under `data/repository/`.
6. Bind implementations with `@Single(binds = [...])`.
7. Add use cases in `domain/usecase/`.

Keep transport details in `data/remote/`; do not leak Ktor or DTO types into ViewModels or Compose UI.

## DI Rules

Default rule set:

- `data/remote/`: `@Single`
- `data/repository/`: `@Single`
- `domain/usecase/`: `@Factory`
- `presentation/viewmodel/`: `@Factory`
- scanning/provider modules: `@Module` + `@ComponentScan` or provider methods

Valid exceptions already in the repo:

- `shared/src/iosMain/.../di/IosModule.kt` uses Koin DSL because generated `.module` extensions are not visible there.
- `composeApp/.../di/ImageLoaderModule.kt` uses DSL for UI-only wiring.
- tests may use DSL for fake bindings and verification.

Do not "normalize" those files into annotations unless the source-set constraint is removed.

## UI Rules

For Carbon usage and project-local UI anchors, read [references/carbon-components.md](references/carbon-components.md).

Core rules:

- Use `RatatoskrTheme`.
- Prefer Carbon components and theme tokens.
- Use Material 3 `Text` and `Icon` with Carbon styling.
- Reuse existing components from `ui/components/` before introducing new ones.
- Use Compose Resources for text.
- Keep accessibility semantics for headings, progress, and live updates.

## File Map

```text
shared/src/commonMain/kotlin/com/po4yka/ratatoskr/
  data/local
  data/remote
  data/mappers
  data/repository
  domain/model
  domain/repository
  domain/usecase
  presentation/state
  presentation/viewmodel
  presentation/navigation
  di
  util

composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/
  di
  ui/auth
  ui/components
  ui/icons
  ui/screens
  ui/theme

composeApp/src/commonMain/composeResources/
  values/strings.xml
  values-ru/strings.xml
```
