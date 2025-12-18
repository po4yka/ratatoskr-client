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

## Carbon Design System Integration

All components use Carbon theme values:

### Colors

```kotlin
Carbon.theme.layer01      // Card backgrounds
Carbon.theme.textPrimary  // Primary text
Carbon.theme.textSecondary // Secondary text
Carbon.theme.iconPrimary  // Icons
Carbon.theme.supportError // Error states
```

### Typography

```kotlin
Carbon.typography.heading03      // Titles
Carbon.typography.body01         // Body text
Carbon.typography.bodyCompact01  // Compact body
Carbon.typography.label01        // Labels
```

### Icons

Use `CarbonIcons` object:

```kotlin
import com.po4yka.bitesizereader.ui.icons.CarbonIcons

Icon(
    imageVector = CarbonIcons.Bookmark,
    contentDescription = "Bookmark",
    tint = Carbon.theme.iconPrimary
)
```

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
            .background(Carbon.theme.layer01)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        // Content
        Text(
            text = data.title,
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )
    }
}
```

## File Location

All components: `composeApp/src/commonMain/kotlin/.../ui/components/`

---

**Related**: [VIEWMODEL_GUIDE.md](VIEWMODEL_GUIDE.md) | Carbon Docs: https://gabrieldrn.github.io/carbon-compose/
