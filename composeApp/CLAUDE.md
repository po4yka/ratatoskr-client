# composeApp/CLAUDE.md

Guidance for UI work in `composeApp/`.

## Module Role

- `composeApp` contains the shell navigation layer, iOS CocoaPods export, and the desktop dev target.
- Android app entrypoints, widgets, and workers now live in `androidApp/`.
- Shared design system code lives in `core/ui/`.
- Route screens and feature-specific UI live in the owning `feature/*` module.

## Screen Pattern

- Prefer `*Screen(component: *Component)` over wiring navigation directly inside composables.
- Keep navigation callbacks in the component layer; screens should call component methods or ViewModel intents, not mutate navigation stacks.
- Do not import feature route screens into shell hosts. Shell rendering should go through render descriptors from `core/navigation`.

## Design System

- The app theme is `BiteSizeReaderTheme` in `core/ui/.../theme/Theme.kt`.
- Carbon Compose is the primary component system.
- Use `Carbon.theme.*` for surfaces, icons, and semantic colors.
- Use `Carbon.typography.*` for text styles.
- Use Material 3 `Text` and `Icon`, not Carbon's internal text/icon APIs.
- Reuse `Spacing`, `Dimensions`, and `IconSizes` from `core/ui/.../theme/`.

Widgets are the main exception: Glance UI is platform-specific and does not follow Carbon patterns.

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

`composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/di/ImageLoaderModule.kt` uses Koin DSL on purpose. Do not treat it as an annotations bug.

## Icons

- Carbon icons live in `core/ui/.../icons/CarbonIcons.kt`.
- Keep icon names aligned with IBM Carbon names where practical.
- New icons should stay 32x32 and use existing theme colors in UI call sites.
