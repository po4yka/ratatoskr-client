# Carbon And Compose UI Reference

Use this reference when you are touching `composeApp` UI.

## Theme Rules

- Wrap app UI with `BiteSizeReaderTheme`.
- Use `Carbon.theme.*` for colors and surfaces.
- Use `Carbon.typography.*` for typography.
- Use Material 3 `Text` and `Icon` with Carbon styles.
- Reuse `Spacing`, `Dimensions`, and `IconSizes` from `ui/theme/`.

## Common Carbon Components

### Button

```kotlin
Button(
    label = "Save",
    onClick = onSave,
    buttonType = ButtonType.Primary,
)
```

### TextInput

```kotlin
TextInput(
    label = "Collection name",
    value = name,
    onValueChange = onNameChange,
    placeholderText = "Work reads",
    state = TextInputState.Enabled,
)
```

### Loading And Progress

```kotlin
SmallLoading()
Loading()
ProgressBar(progressState = ProgressBarState.Active)
IndeterminateProgressBar(progressState = ProgressBarState.Active)
```

### Tag

```kotlin
ReadOnlyTag(text = "AI")
```

## Text And Icon Usage

```kotlin
Text(
    text = title,
    style = Carbon.typography.heading03,
    color = Carbon.theme.textPrimary,
)

Icon(
    imageVector = CarbonIcons.Bookmark,
    contentDescription = null,
    tint = Carbon.theme.iconPrimary,
)
```

## Project Components To Reuse First

- `ScreenHeader`
- `SummarySearchBar`
- `FilterChipsRow`
- `SortOptionsMenu`
- `PullToRefreshContainer`
- `SummaryCard`
- `SummaryGridCard`
- `SwipeableSummaryCard`
- `SummaryCardSkeleton`
- `ContextualEmptyState`
- `ErrorView`
- `ReadingGoalCard`
- `RecommendationsSection`
- `RecentSearchesSection`
- `TrendingTopicsSection`

## Localization

- Add strings to `composeResources/values/strings.xml` and `values-ru/strings.xml`.
- Use `stringResource(Res.string...)` in shared UI code.

## Accessibility

- Preserve heading semantics for section titles.
- Add content descriptions for meaningful actions.
- Use live-region semantics for dynamic sync/loading/error text when appropriate.

## Icons

`CarbonIcons.kt` is the project-local icon set. Keep new icons aligned with IBM Carbon names where practical, and keep vector definitions at 32x32.
