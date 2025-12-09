# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Bite-Size Reader is a **Kotlin Multiplatform + Compose Multiplatform** mobile client for an AI-powered article/video summarization service. It targets Android, iOS, and Desktop (desktop is for development hot reload only).

- **Shared code**: ~80-90% in `shared/` module (business logic, networking, database)
- **Shared UI**: Compose Multiplatform in `composeApp/` rendered on all platforms
- **iOS shell**: SwiftUI host in `iosApp/` that embeds the Compose UI
- **Architecture**: Clean Architecture with MVI pattern, offline-first with SQLite + background sync

## Build Commands

```bash
# Build all
./gradlew build

# Android
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug

# Desktop (Hot Reload for UI development)
./gradlew :composeApp:runDesktop

# iOS - open in Xcode
open iosApp/iosApp.xcodeproj
```

## Testing

```bash
# All shared tests
./gradlew :shared:allTests

# Android unit tests
./gradlew :composeApp:testDebugUnitTest

# Run specific test class
./gradlew :shared:testDebugUnitTest --tests SummaryListViewModelTest

# iOS tests (macOS only)
./gradlew :shared:iosSimulatorArm64Test

# Coverage report
./gradlew koverHtmlReportDebug
```

## Code Quality

```bash
# Lint check
./gradlew ktlintCheck

# Auto-fix lint issues
./gradlew ktlintFormat

# Static analysis
./gradlew detekt

# All quality checks
./gradlew ktlintCheck detekt
```

## Architecture

### Module Structure

```
shared/src/commonMain/kotlin/
  data/
    local/          # SQLDelight database, SecureStorage
    remote/         # Ktor API clients, DTOs
    repository/     # Repository implementations
    mappers/        # DTO <-> Domain mappers
  domain/
    model/          # Domain entities
    repository/     # Repository interfaces
    usecase/        # Business logic use cases
  presentation/
    navigation/     # Decompose navigation
    viewmodel/      # Shared ViewModels (MVI pattern)
  di/               # Koin dependency injection modules

composeApp/src/commonMain/kotlin/
  ui/
    screens/        # Full screens (Compose Multiplatform)
    components/     # Reusable UI components
    theme/          # Material 3 theme
```

### Key Patterns

- **MVI**: ViewModels expose `StateFlow<State>` and accept events/intents
- **Repository Pattern**: Repositories coordinate between remote API and local database
- **Offline-First**: Always read from local DB first, sync with server in background
- **Decompose Navigation**: Cross-platform navigation with lifecycle awareness

### Technology Stack

| Component | Technology |
|-----------|------------|
| Networking | Ktor Client 3.x |
| Database | SQLDelight 2.x |
| DI | Koin 4.x |
| Navigation | Decompose 3.x |
| Serialization | kotlinx.serialization |
| Async | kotlinx.coroutines + Flow |
| Image Loading | Coil 3.x |
| Design System | Carbon Compose (IBM Carbon) |
| Icons | Compose Icons |

## Design System

This project uses **Carbon Compose** (IBM's Carbon Design System for Compose Multiplatform).

### Usage Rules

1. **Always use Carbon components** for new UI:
   - `Button` with `ButtonType` (Primary, Secondary, Tertiary, Ghost, PrimaryDanger, TertiaryDanger, GhostDanger)
   - `TextInput` with `TextInputState` (Enabled, Warning, Error, Disabled, ReadOnly)
   - `ProgressBar` / `IndeterminateProgressBar` with `ProgressBarState` (Active, Success, Error)
   - `Loading` / `SmallLoading` for spinners
   - `ReadOnlyTag` for tag chips

2. **Use Carbon theme colors** via `Carbon.theme.*`:
   - `textPrimary`, `textSecondary`, `textOnColor`
   - `background`, `layer01`, `layer02`
   - `iconPrimary`, `iconSecondary`
   - `supportError`, `supportSuccess`, `supportWarning`
   - `linkPrimary`, `borderSubtle00`

3. **Use Carbon typography** via `Carbon.typography.*`:
   - Headings: `heading03`, `heading04`, `heading05`, `headingCompact01`
   - Body: `body01`, `bodyCompact01`
   - Labels: `label01`

4. **Use Material3 Text** with Carbon styles (Carbon's Text is internal):
   ```kotlin
   import androidx.compose.material3.Text
   Text(
       text = "Hello",
       style = Carbon.typography.heading03,
       color = Carbon.theme.textPrimary,
   )
   ```

5. **Theme setup** in `Theme.kt` wraps `CarbonDesignSystem` around `MaterialTheme`:
   - Light mode: `WhiteTheme`
   - Dark mode: `Gray100Theme`

### Carbon Documentation

- GitHub: https://github.com/gabrieldrn/carbon-compose
- Docs: https://gabrieldrn.github.io/carbon-compose/

## Icons

This project uses **Compose Icons** (by DevSrSouza) - a multiplatform icon library providing access to popular icon packs including Feather Icons, Tabler Icons, Font Awesome, and more.

### Available Icon Packs (in this project)

- **Feather Icons**: Clean, minimalist icons - `compose.icons.FeatherIcons`
- **Tabler Icons**: Consistent stroke icons - `compose.icons.TablerIcons`

Additional packs available via gradle:
- Font Awesome: `br.com.devsrsouza.compose.icons:font-awesome`
- Simple Icons: `br.com.devsrsouza.compose.icons:simple-icons`
- Eva Icons: `br.com.devsrsouza.compose.icons:eva-icons`
- Octicons: `br.com.devsrsouza.compose.icons:octicons`

### Usage Rules

1. **Import icons from the appropriate pack**:
   ```kotlin
   import compose.icons.FeatherIcons
   import compose.icons.feathericons.Home
   import compose.icons.feathericons.Settings

   import compose.icons.TablerIcons
   import compose.icons.tablericons.Bulb
   ```

2. **Use icons with Material3 Icon component**:
   ```kotlin
   import androidx.compose.material3.Icon
   import compose.icons.FeatherIcons
   import compose.icons.feathericons.Bookmark

   Icon(
       imageVector = FeatherIcons.Bookmark,
       contentDescription = "Bookmark",
       tint = Carbon.theme.iconPrimary
   )
   ```

3. **Always apply Carbon theme colors** for consistency:
   - `Carbon.theme.iconPrimary` - Primary icons
   - `Carbon.theme.iconSecondary` - Secondary/less important icons
   - `Carbon.theme.iconOnColor` - Icons on colored backgrounds
   - `Carbon.theme.supportError` - Error state icons
   - `Carbon.theme.supportSuccess` - Success state icons

4. **Common icons used in this project**:
   - Navigation: `FeatherIcons.Bookmark`, `FeatherIcons.Folder`, `FeatherIcons.Settings`
   - Actions: `FeatherIcons.RefreshCw`, `FeatherIcons.Share2`, `FeatherIcons.ArrowLeft`
   - Status: `FeatherIcons.CheckCircle`, `FeatherIcons.AlertCircle`, `FeatherIcons.X`
   - Content: `FeatherIcons.FileText`, `FeatherIcons.Inbox`, `FeatherIcons.Trash2`

### Compose Icons Documentation

- GitHub: https://github.com/DevSrSouza/compose-icons
- Icon Browser: https://composeicons.com/
- Maven: `br.com.devsrsouza.compose.icons:{pack}:1.1.1`

## Configuration

Create `local.properties` in project root (not committed):

```properties
api.base.url=http://localhost:8000
client.id=android-app-v1.0
```

For Android emulator, use `http://10.0.2.2:8000` instead of `localhost`.

## Code Style

- ktlint configured for Compose (uppercase function names allowed for @Composable)
- Max line length: 120 characters
- Generated code in `build/` and `generated/` is excluded from checks
- Detekt enabled with relaxed rules for Compose/KMP patterns

## Platform-Specific Notes

### Android
- `EncryptedSharedPreferences` for secure token storage
- `WorkManager` for background sync
- `Glance` for home screen widgets

### iOS
- Keychain for secure token storage
- `BackgroundTasks` for background sync
- `WidgetKit` for home screen widgets
- SKIE for improved Kotlin/Swift interop (Flow -> AsyncSequence, suspend -> async)

### Desktop
- Development only - used for Compose Hot Reload during UI work
- Not a production target
