# composeApp/AGENTS.md

Guidance for UI work in `composeApp/`.

## Module Role

- `composeApp` contains the shell navigation layer, iOS CocoaPods export, and the desktop dev target.
- Android app entrypoints, widgets, and workers now live in `androidApp/`.
- Shared design system code lives in `core/ui/`.
- Route screens and feature-specific UI live in the owning `feature/*` module.
- `composeApp` UI should stay limited to the shell host and adaptive navigation chrome.

## Screen Pattern

- Prefer `*Screen(component: *Component)` over wiring navigation directly inside composables.
- Read routed-screen dependencies from the component or app-level providers, not `koinInject` inside the screen.
- Keep navigation callbacks in the component layer; screens should call component methods or ViewModel intents, not mutate navigation stacks.
- Do not import feature route screens into shell hosts. Shell rendering should go through `MainChildDescriptor.render()` and `RootChildDescriptor.render()`.

## Design System

- The app theme is `RatatoskrTheme` in `core/ui/.../theme/Theme.kt`.
- Material 3 is the component substrate; project-owned `AppTheme` tokens are the design system.
- Use `AppTheme.colors.*` for surfaces, icons, and semantic colors.
- Use `AppTheme.type.*` for text styles.
- Use Material 3 `Text` and `Icon` for primitives.
- Reuse `Spacing`, `Dimensions`, and `IconSizes` from `core/ui/.../theme/`.
- Prefer the project wrapper components in `core/ui/.../components/` (`AppCheckbox`, `AppDialog`, `AppIconButton`, `LayerCard`, `AppMenu`/`AppMenuItem`/`AppOverflowMenuButton`, `SelectableChip`, `AppSlider`, `TextArea`, `AppTextButton`) over raw Material 3 equivalents.

Widgets are the main exception: Glance UI is platform-specific and does not follow AppTheme patterns.

## Resources And Accessibility

- Add shared user-facing strings to:
  - `core/ui/src/commonMain/composeResources/values/strings.xml`
  - `core/ui/src/commonMain/composeResources/values-ru/strings.xml`
- Use `stringResource(Res.string...)` instead of hardcoded strings in shared UI.
- Keep accessibility semantics when adding headers, dynamic status, or loading banners.

## Project Components

Prefer extending existing components before creating new abstractions. Useful anchors:

- list/detail cards: `SummaryCard`, `SummaryGridCard`, `SwipeableSummaryCard`
- state views: `ErrorView`, `ContextualEmptyState`, `SummaryCardSkeleton`
- search/filter: `SummarySearchBar`, `FilterChipsRow`, `SortOptionsMenu`
- settings/detail flows: `ScreenHeader`, `ReadingSettingsPanel`, dialogs in `core/ui/.../components/`
- engagement widgets: `ReadingGoalCard`, `RecommendationsSection`, `RecentSearchesSection`, `TrendingTopicsSection`

## DI Exception

`composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/di/ImageLoaderModule.kt` uses Koin DSL on purpose. Do not treat it as an annotations bug.

The app-level `App.kt` provider for image URL transformation is also intentional. Reusable composables should consume that provider instead of resolving Koin directly.

## Icons

- Project icons live in `core/ui/.../icons/AppIcons.kt`.
- New icons should stay 32x32 and use `AppTheme.colors.*` at call sites.
