# Carbon Design System Components

## Buttons

```kotlin
Button(
    label = "Submit",
    onClick = { /* ... */ },
    buttonType = ButtonType.Primary, // Primary, Secondary, Tertiary, Ghost, PrimaryDanger, TertiaryDanger, GhostDanger
)
```

## Text Input

```kotlin
TextInput(
    label = "Email",
    value = state.email,
    onValueChange = { /* ... */ },
    state = TextInputState.Enabled, // Enabled, Warning, Error, Disabled, ReadOnly
)
```

## Progress

```kotlin
ProgressBar(progressState = ProgressBarState.Active) // Active, Success, Error
IndeterminateProgressBar(progressState = ProgressBarState.Active)
Loading() // Full-size spinner
SmallLoading() // Inline spinner
```

## Tags

```kotlin
ReadOnlyTag(text = "Category")
```

## Theme Colors

Access via `Carbon.theme.*`:
- Text: `textPrimary`, `textSecondary`, `textOnColor`
- Background: `background`, `layer01`, `layer02`
- Icons: `iconPrimary`, `iconSecondary`
- Status: `supportError`, `supportSuccess`, `supportWarning`
- Other: `linkPrimary`, `borderSubtle00`

## Typography

Access via `Carbon.typography.*`:
- Headings: `heading03`, `heading04`, `heading05`, `headingCompact01`
- Body: `body01`, `bodyCompact01`
- Labels: `label01`

## Text Usage

Use Material3 `Text` with Carbon styles:

```kotlin
import androidx.compose.material3.Text

Text(
    text = "Hello",
    style = Carbon.typography.heading03,
    color = Carbon.theme.textPrimary,
)
```

## Icons

```kotlin
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import androidx.compose.material3.Icon

Icon(
    imageVector = CarbonIcons.Bookmark,
    contentDescription = "Bookmark",
    tint = Carbon.theme.iconPrimary,
)
```

Available: Bookmark, Folder, Settings, ArrowLeft, Home, Renew, Share, Close,
Checkmark, CheckmarkFilled, CircleOutline, WarningAlt, Document, Email, Map,
TrashCan, ColorPalette, Idea, Restaurant, GameWireless, RainDrop, Gem

## Adding New Icons

1. Get SVG path from: https://carbondesignsystem.com/elements/icons/library/
2. Add to `composeApp/.../ui/icons/CarbonIcons.kt`
3. Use `ImageVector.Builder` pattern, 32x32dp
