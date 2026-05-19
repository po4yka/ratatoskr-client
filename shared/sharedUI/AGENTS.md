# shared/sharedUI/AGENTS.md

Guidance for UI work in `shared/sharedUI/`. Applies to Claude Code, Codex, and
any other AGENTS.md-aware agent.

## Module Role

- `shared/sharedUI` contains the shared Compose Multiplatform UI:
  shell navigation composables (`App.kt`, `MainScreen.kt`), Compose UI
  bridges for iOS (`MainViewController`,
  `ComposeRootViewControllerFactory`), and the CocoaPods framework export
  consumed by `iosApp`.
- The pure-logic side of the shared library — DI, navigation contracts,
  app composition, iOS Swift-facing bridges — lives in
  `shared/sharedLogic/`. `sharedUI` depends on `sharedLogic` (and
  re-exports it through the CocoaPods framework so Swift sees both
  through `import ComposeApp`).
- Android app entrypoints, widgets, and workers live in `androidApp/`.
- Desktop entry point (`main.kt`, `application { Window { ... } }`)
  lives in `desktopApp/`.
- Shared design-system code lives in `core/ui/`.
- Route screens and feature-specific UI live in the owning `feature/*`
  module.
- `shared/sharedUI` UI should stay limited to the shell host and
  adaptive navigation chrome.

## Screen Pattern

- Prefer `*Screen(component: *Component)` over wiring navigation directly
  inside composables.
- Read routed-screen dependencies from the component or app-level
  providers, not `koinInject` inside the screen.
- Keep navigation callbacks in the component layer; screens should call
  component methods or ViewModel intents, not mutate navigation stacks.
- Do not import feature route screens into shell hosts. Shell rendering
  should go through `MainChildDescriptor.render()` and
  `RootChildDescriptor.render()`.

## Design System

- Frost is the design system. Material 3 is removed from `commonMain`.
  Use Frost atoms in
  `core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/components/frost/`
  and foundation primitives in
  `core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/components/foundation/`.
- The app theme entry point is `RatatoskrTheme` in
  `core/ui/.../theme/Theme.kt`.
- Use `FrostText` for text and `FrostIcon` for icons.
- Reuse `Spacing`, `Dimensions`, `IconSizes`, and `FrostSpacing` tokens
  from `core/ui/.../theme/`.
- Prefer Frost atoms directly: `BrutalistCard`, `BracketButton`,
  `BracketIconButton`, `BracketField`, `BracketSwitch`,
  `BracketSelector`, `BracketSlider`, `MultiSelectChip`, `StatusBadge`,
  `RowDigest`, `SectionHeading`, `IngestLine`, `PullQuote`, `AtomMark`,
  `InlineLink`, `Toast`, `FrostText`, `FrostIcon`, `FrostSpinner`,
  `FrostDialog`, `FrostScaffold`, `FrostSurface`, `FrostDivider`,
  `FrostCheckbox`, `FrostRadio`.
- Honor Frost's two-color rule (ink/page), single `spark` accent,
  0 corner radius, no shadows, no Material elevation. Canonical spec
  lives in `DESIGN.md` at the repo root.

Glance widgets are the documented exception: Glance UI is platform-specific
and uses hardcoded Frost INK/PAGE constants directly because it does not
see `RatatoskrTheme`.

## Resources And Accessibility

- Add shared user-facing strings to:
  - `core/ui/src/commonMain/composeResources/values/strings.xml`
  - `core/ui/src/commonMain/composeResources/values-ru/strings.xml`
- Use `stringResource(Res.string...)` instead of hardcoded strings in
  shared UI.
- Keep accessibility semantics when adding headers, dynamic status, or
  loading banners.

## Project Components

Prefer extending existing components before creating new abstractions.
Useful anchors:

- Frost atoms: `BrutalistCard`, `BracketButton`, `BracketIconButton`,
  `BracketField`, `BracketSwitch`, `BracketSelector`, `BracketSlider`,
  `MultiSelectChip`, `StatusBadge`, `RowDigest`, `SectionHeading`,
  `IngestLine`, `PullQuote`, `AtomMark`, `InlineLink`, `Toast`,
  `FrostText`, `FrostIcon`, `FrostSpinner`, `FrostDialog`,
  `FrostScaffold`, `FrostSurface`, `FrostDivider`, `FrostCheckbox`,
  `FrostRadio`
- list/detail cards: `SummaryCard`, `SummaryGridCard`, `SwipeableSummaryCard`
- state views: `ErrorView`, `ContextualEmptyState`, `SummaryCardSkeleton`
- search/filter: `SummarySearchBar`, `FilterChipsRow`, `SortOptionsMenu`
- settings/detail flows: `ScreenHeader`, `ReadingSettingsPanel`,
  dialogs in `core/ui/.../components/`
- engagement widgets: `ReadingGoalCard`, `RecommendationsSection`,
  `RecentSearchesSection`, `TrendingTopicsSection`

## DI Exception

`shared/sharedUI/src/commonMain/kotlin/com/po4yka/ratatoskr/di/ImageLoaderModule.kt`
uses Koin DSL on purpose. Do not treat it as an annotations bug.

The app-level `App.kt` provider for image URL transformation is also
intentional. Reusable composables should consume that provider instead
of resolving Koin directly.

## Icons

- Project icons live in `core/ui/.../icons/AppIcons.kt`.
- New icons should stay 32×32 and use Frost ink / page / spark tokens
  at call sites.
