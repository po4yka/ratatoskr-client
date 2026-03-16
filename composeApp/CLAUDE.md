# composeApp/ Module

## Design System (Carbon Compose)

This project uses **Carbon Compose** — IBM's Carbon Design System for Compose Multiplatform.

### Component Usage

Use Carbon components for all new UI: `Button` (with `ButtonType`), `TextInput` (with `TextInputState`), `ProgressBar`/`IndeterminateProgressBar`, `Loading`/`SmallLoading`, `ReadOnlyTag`.

### Theme Colors

Access via `Carbon.theme.*`:
- Text: `textPrimary`, `textSecondary`, `textOnColor`
- Surfaces: `background`, `layer01`, `layer02`
- Icons: `iconPrimary`, `iconSecondary`
- Status: `supportError`, `supportSuccess`, `supportWarning`
- Other: `linkPrimary`, `borderSubtle00`

### Typography

Access via `Carbon.typography.*`:
- Headings: `heading03`, `heading04`, `heading05`, `headingCompact01`
- Body: `body01`, `bodyCompact01`
- Labels: `label01`

### Important: Use Material3 Text

Carbon's `Text` composable is internal. Always use Material3 `Text` with Carbon styles:
```kotlin
import androidx.compose.material3.Text
Text(
    text = "Hello",
    style = Carbon.typography.heading03,
    color = Carbon.theme.textPrimary,
)
```

### Theme Setup

`Theme.kt` wraps `CarbonDesignSystem` around `MaterialTheme`. Light = `WhiteTheme`, Dark = `Gray100Theme`.

### Documentation

- GitHub: https://github.com/gabrieldrn/carbon-compose
- Docs: https://gabrieldrn.github.io/carbon-compose/

## Icons (Carbon Design)

Custom IBM Carbon icons as Compose `ImageVector`s in `CarbonIcons.kt`.

### Usage

Use `CarbonIcons.*` with Material3 `Icon` and Carbon theme colors:
```kotlin
Icon(
    imageVector = CarbonIcons.Bookmark,
    contentDescription = "Bookmark",
    tint = Carbon.theme.iconPrimary,
)
```

Browse `CarbonIcons` object for all available icons. Apply `Carbon.theme.icon*` colors for consistency.

### Adding New Icons

1. Get SVG path data from [Carbon Icons Library](https://carbondesignsystem.com/elements/icons/library/)
2. Add to `CarbonIcons.kt` using the `ImageVector.Builder` pattern
3. All icons should be 32x32dp (Carbon's standard size)

### Documentation

- Icons Library: https://carbondesignsystem.com/elements/icons/library/
- GitHub: https://github.com/carbon-design-system/carbon
