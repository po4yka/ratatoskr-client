# App UI Reference

Use this reference when touching `composeApp` UI or any feature screen.

## Theme Rules

- Wrap app UI with `RatatoskrTheme` (`core/ui/.../theme/Theme.kt`).
- Frost is the live design system. Material 3 was removed from
  `commonMain`. The canonical spec lives in `DESIGN.md` at the repo
  root (DESIGN.md format).
- Two-color rule only: ink (`#1C242C` / `#E8ECF0` dark) and page
  (`#F0F2F5` / `#12161C` dark). The single critical accent is `spark`
  (`#DC3545`) and never flips.
- 0 corner radius, no shadows, no Material elevation.
- Reuse `Spacing`, `Dimensions`, `IconSizes`, and `FrostSpacing` from
  `core/ui/.../theme/`.

## Frost Atoms

Prefer these atoms over hand-rolling new components. All live in
`core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/components/frost/`:

- Surface / card: `BrutalistCard`
- Buttons: `BracketButton`, `BracketIconButton`
- Inputs: `BracketField`, `BracketSwitch`, `BracketSelector`,
  `BracketSlider`, `FrostCheckbox`, `FrostRadio`, `MultiSelectChip`
- Display: `StatusBadge`, `RowDigest`, `SectionHeading`, `IngestLine`,
  `PullQuote`, `AtomMark`, `InlineLink`, `Toast`
- Feedback: `FrostSpinner`

Foundation primitives live in
`core/ui/.../components/foundation/`:

- `FrostText`, `FrostIcon`, `FrostDialog`, `FrostScaffold`,
  `FrostSurface`, `FrostDivider`

## Typical Usage

```kotlin
// Primary action
BracketButton(
    label = "Save",
    onClick = onSave,
)

// Text
FrostText(
    text = title,
    style = FrostTextStyle.HeadingL,
)

// Icon
FrostIcon(
    imageVector = AppIcons.Bookmark,
    contentDescription = null,
)

// Text field
BracketField(
    value = name,
    onValueChange = onNameChange,
    label = "Collection name",
    placeholder = "Work reads",
    isError = hasError,
    supportingText = if (hasError) "Required" else null,
)

// Loading
FrostSpinner()
```

Glance widgets are the documented exception: they read hardcoded Frost
INK/PAGE constants directly because Glance does not see `RatatoskrTheme`.

## Higher-Level Project Components

Reuse these before inventing new abstractions. They wrap Frost atoms
and live under `core/ui/.../components/`:

- list / detail cards: `SummaryCard`, `SummaryGridCard`, `SwipeableSummaryCard`
- state views: `ErrorView`, `ContextualEmptyState`, `SummaryCardSkeleton`,
  `EmptyStateView`
- search / filter: `SummarySearchBar`, `FilterChipsRow`, `SortOptionsMenu`
- chrome: `ScreenHeader`, `PullToRefreshContainer`,
  `ReadingSettingsPanel`
- engagement: `ReadingGoalCard`, `RecommendationsSection`,
  `RecentSearchesSection`, `TrendingTopicsSection`
- dialogs: `FeedbackDialog`, `DeleteAccountDialog`, `AddToCollectionDialog`

## Localization

- Add strings to `core/ui/src/commonMain/composeResources/values/strings.xml`
  and `values-ru/strings.xml`.
- Use `stringResource(Res.string...)` from
  `ratatoskr.core.ui.generated.resources.*` in shared UI code.

## Accessibility

- Preserve heading semantics for section titles.
- Add content descriptions for meaningful actions.
- Use live-region semantics for dynamic sync / loading / error text
  when appropriate.

## Icons

- Project icons live in `core/ui/.../icons/AppIcons.kt`
  (renamed from `CarbonIcons.kt`).
- Keep new vectors at 32×32 and tint them with Frost ink / page /
  spark tokens at call sites.
