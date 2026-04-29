# UI Component Library

Catalog of reusable Compose UI components in this project.

## Component Inventory

| Component | Purpose | Location |
|-----------|---------|----------|
| `SummaryCard` | Display summary in list | `ui/components/SummaryCard.kt` |
| `SummaryGridCard` | Summary in grid layout | `ui/components/SummaryGridCard.kt` |
| `SummaryCardSkeleton` | Loading placeholder | `ui/components/SummaryCardSkeleton.kt` |
| `SwipeableSummaryCard` | Card with swipe actions | `ui/components/SwipeableSummaryCard.kt` |
| `CollectionItem` | Collection list item | `ui/components/CollectionItem.kt` |
| `TagChip` | Topic tag display | `ui/components/TagChip.kt` |
| `FilterChipsRow` | Filter chips row | `ui/components/FilterChipsRow.kt` |
| `SearchBar` | Search input field | `ui/components/SearchBar.kt` |
| `SortOptionsMenu` | Sort dropdown menu | `ui/components/SortOptionsMenu.kt` |
| `ErrorView` | Error state display | `ui/components/ErrorView.kt` |
| `EmptyStateView` | Empty state display | `ui/components/EmptyStateView.kt` |
| `ProgressIndicatorWithStages` | Multi-stage progress | `ui/components/ProgressIndicatorWithStages.kt` |
| `PullToRefreshContainer` | Pull-to-refresh wrapper | `ui/components/PullToRefreshContainer.kt` |

## Usage Examples

### SummaryCard

```kotlin
SummaryCard(
    summary = summary,
    onClick = { onSummaryClick(summary.id) },
    modifier = Modifier.fillMaxWidth()
)
```

### ErrorView

```kotlin
ErrorView(
    message = "Failed to load summaries",
    onRetry = { viewModel.refresh() }
)
```

### EmptyStateView

```kotlin
EmptyStateView(
    title = "No summaries yet",
    message = "Submit a URL to get started"
)
```

### TagChip

```kotlin
TagChip(
    tag = "kotlin",
    onClick = { onTagClick(tag) }
)
```

## Design System Integration

All components use Material 3 as the substrate with project-owned `AppTheme` tokens.

### Colors

```kotlin
AppTheme.colors.layer01      // Card backgrounds
AppTheme.colors.textPrimary  // Primary text
AppTheme.colors.textSecondary // Secondary text
AppTheme.colors.iconPrimary  // Icons
AppTheme.colors.supportError // Error states
// TODO: confirm exact AppTheme field list — see core/ui/.../theme/AppColors.kt
```

### Typography

```kotlin
AppTheme.type.heading03      // Titles
AppTheme.type.body01         // Body text
AppTheme.type.bodyCompact01  // Compact body
AppTheme.type.label01        // Labels
// TODO: confirm exact AppTheme.type field list — see core/ui/.../theme/AppType.kt
```

### Icons

Use `AppIcons` object:

```kotlin
import com.po4yka.ratatoskr.core.ui.icons.AppIcons

Icon(
    imageVector = AppIcons.Bookmark,
    contentDescription = "Bookmark",
    tint = AppTheme.colors.iconPrimary
)
```

### Project Wrapper Components

Prefer these wrappers in `core/ui/.../components/` over raw Material 3 equivalents:

| Wrapper | Purpose |
|---------|---------|
| `AppCheckbox` | Themed checkbox |
| `AppDialog` | Themed dialog |
| `AppIconButton` | Themed icon button |
| `LayerCard` | Themed card surface |
| `AppMenu` / `AppMenuItem` / `AppOverflowMenuButton` | Themed menus |
| `SelectableChip` | Filter / selectable chip |
| `AppSlider` | Themed slider |
| `TextArea` | Multi-line text input |
| `AppTextButton` | Text-style button |
| `AppSpinner` / `AppSmallSpinner` | Loading indicators (wraps `CircularProgressIndicator`) |

## Component Pattern

```kotlin
@Composable
fun MyComponent(
    data: MyData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(AppTheme.colors.layer01)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        // Content
        Text(
            text = data.title,
            style = AppTheme.type.heading03,
            color = AppTheme.colors.textPrimary,
        )
    }
}
```

## File Location

Shared components: `core/ui/src/commonMain/kotlin/.../core/ui/components/`

Feature-specific UI should live with the owning feature module, not under `composeApp`.

---

**Related**: [VIEWMODEL_GUIDE.md](VIEWMODEL_GUIDE.md)
