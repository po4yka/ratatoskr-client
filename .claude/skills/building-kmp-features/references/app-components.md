# App UI Reference

Use this reference when touching `composeApp` UI or any feature screen.

## Theme Rules

- Wrap app UI with `RatatoskrTheme`.
- Use `AppTheme.colors.*` for colors and surfaces.
- Use `AppTheme.type.*` for typography.
- Use Material 3 `Text` and `Icon` for primitives.
- Reuse `Spacing`, `Dimensions`, and `IconSizes` from `ui/theme/`.

## Project Wrapper Components

Prefer these over raw Material 3 equivalents; all live in `core/ui/.../components/`:

- `AppCheckbox` — themed checkbox
- `AppDialog` — themed dialog
- `AppIconButton` — themed icon button
- `LayerCard` — themed card surface
- `AppMenu` / `AppMenuItem` / `AppOverflowMenuButton` — themed menus
- `SelectableChip` — filter / selectable chip
- `AppSlider` — themed slider
- `TextArea` — multi-line text input
- `AppTextButton` — text-style button
- `AppSpinner` / `AppSmallSpinner` — loading indicators (shim around `CircularProgressIndicator`)

## Button

```kotlin
// Primary action
Button(onClick = onSave) { Text("Save") }

// Secondary / outlined action
OutlinedButton(onClick = onCancel) { Text("Cancel") }

// Ghost / text action
AppTextButton(onClick = onDismiss) { Text("Dismiss") }
```

## Text Input

```kotlin
OutlinedTextField(
    value = name,
    onValueChange = onNameChange,
    label = { Text("Collection name") },
    placeholder = { Text("Work reads") },
    isError = hasError,
    enabled = isEnabled,
    supportingText = { if (hasError) Text("Required") },
)
```

## Loading And Progress

```kotlin
AppSpinner()       // full-size loading indicator
AppSmallSpinner()  // compact loading indicator

// Determinate progress
LinearProgressIndicator(progress = { fraction }, modifier = Modifier.fillMaxWidth())

// Indeterminate progress
LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
```

## Tag / Chip

Tags are hand-rolled in `TagChip.kt` (no wrapper component):

```kotlin
Box(
    modifier = Modifier
        .background(AppTheme.colors.layer02, RoundedCornerShape(4.dp))
        .padding(horizontal = 8.dp, vertical = 4.dp),
) {
    Text(text = tag, style = AppTheme.type.label01, color = AppTheme.colors.textSecondary)
}
```

## Text And Icon Usage

```kotlin
Text(
    text = title,
    style = AppTheme.type.heading03,
    color = AppTheme.colors.textPrimary,
)

Icon(
    imageVector = AppIcons.Bookmark,
    contentDescription = null,
    tint = AppTheme.colors.iconPrimary,
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

`AppIcons.kt` is the project-local icon set (renamed from `CarbonIcons.kt`).
Keep vector definitions at 32x32 and use `AppTheme.colors.*` at call sites.
