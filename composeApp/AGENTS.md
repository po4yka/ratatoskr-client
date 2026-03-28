# composeApp/AGENTS.md

Guidance for UI work in `composeApp/`.

## Module Role

- `composeApp` contains shared Compose UI, the shell navigation layer, iOS CocoaPods export, and the desktop dev target.
- Android app entrypoints, widgets, and workers now live in `androidApp/`.
- UI code is split into `ui/screens`, `ui/components`, `ui/theme`, `ui/icons`, `ui/auth`, and a small `di/` layer.

## Screen Pattern

- Prefer `*Screen(component: *Component)` over wiring navigation directly inside composables.
- Read screen state from `component.viewModel.state.collectAsState()`.
- Read routed-screen dependencies from the component or app-level providers, not `koinInject` inside the screen.
- Keep navigation callbacks in the component layer; screens should call component methods or ViewModel intents, not mutate navigation stacks.
- Complex UI should be decomposed into local components under `ui/components/`.

## Design System

- The app theme is `BiteSizeReaderTheme` in `ui/theme/Theme.kt`.
- Carbon Compose is the primary component system.
- Use `Carbon.theme.*` for surfaces, icons, and semantic colors.
- Use `Carbon.typography.*` for text styles.
- Use Material 3 `Text` and `Icon`, not Carbon's internal text/icon APIs.
- Reuse `Spacing`, `Dimensions`, and `IconSizes` from `ui/theme/`.

Widgets are the main exception: Glance UI is platform-specific and does not follow Carbon patterns.

## Resources And Accessibility

- Add user-facing strings to both:
  - `composeApp/src/commonMain/composeResources/values/strings.xml`
  - `composeApp/src/commonMain/composeResources/values-ru/strings.xml`
- Use `stringResource(Res.string...)` instead of hardcoded strings in shared UI.
- Keep accessibility semantics when adding headers, dynamic status, or loading banners.

## Project Components

Prefer extending existing components before creating new abstractions. Useful anchors:

- list/detail cards: `SummaryCard`, `SummaryGridCard`, `SwipeableSummaryCard`
- state views: `ErrorView`, `ContextualEmptyState`, `SummaryCardSkeleton`
- search/filter: `SummarySearchBar`, `FilterChipsRow`, `SortOptionsMenu`
- settings/detail flows: `ScreenHeader`, `ReadingSettingsPanel`, dialogs in `ui/components/`
- engagement widgets: `ReadingGoalCard`, `RecommendationsSection`, `RecentSearchesSection`, `TrendingTopicsSection`

## DI Exception

`composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/di/ImageLoaderModule.kt` uses Koin DSL on purpose. Do not treat it as an annotations bug.

The app-level `App.kt` provider for image URL transformation is also intentional. Reusable composables should consume that provider instead of resolving Koin directly.

## Icons

- Carbon icons live in `ui/icons/CarbonIcons.kt`.
- Keep icon names aligned with IBM Carbon names where practical.
- New icons should stay 32x32 and use existing theme colors in UI call sites.
