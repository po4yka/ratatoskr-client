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
| DI | Koin 4.x with Annotations + KSP |
| Navigation | Decompose 3.x |
| Serialization | kotlinx.serialization |
| Async | kotlinx.coroutines + Flow |
| Image Loading | Coil 3.x |
| Design System | Carbon Compose (IBM Carbon) |
| Icons | Custom Carbon Icons (IBM Carbon Design) |

## Dependency Injection (Koin)

This project uses **Koin 4.x with Koin Annotations** and KSP for compile-time safety.

### Usage Rules

1. **Use annotations for new classes** - never use DSL modules for new code:
   ```kotlin
   import org.koin.core.annotation.Single
   import org.koin.core.annotation.Factory

   // Singleton (one instance for entire app lifecycle)
   @Single
   class MyRepository(private val api: MyApi) : MyRepositoryInterface

   // Factory (new instance each time)
   @Factory
   class MyUseCase(private val repository: MyRepository)
   ```

2. **Annotation placement by layer**:
   - **API implementations** (`data/remote/`): Use `@Single`
   - **Repository implementations** (`data/repository/`): Use `@Single`
   - **Use cases** (`domain/usecase/`): Use `@Factory`
   - **ViewModels** (`presentation/viewmodel/`): Use `@Factory` (or `@Single` for shared state like AuthViewModel)

3. **Module classes** use `@Module` and `@ComponentScan`:
   ```kotlin
   import org.koin.core.annotation.Module
   import org.koin.core.annotation.ComponentScan

   @Module
   @ComponentScan("com.po4yka.bitesizereader.data.repository")
   class RepositoryModule
   ```

4. **Provider functions** for complex initialization:
   ```kotlin
   @Module
   class DatabaseModule {
       @Single
       fun provideDatabase(driverFactory: DatabaseDriverFactory): Database {
           // Complex initialization logic
           return Database(driverFactory.createDriver(), ...)
       }
   }
   ```

5. **Platform modules** are in platform-specific source sets:
   - `shared/src/androidMain/.../di/AndroidModule.kt`
   - `shared/src/iosMain/.../di/IosModule.kt`
   - `shared/src/desktopMain/.../di/DesktopModule.kt`

6. **Importing generated modules** - KSP generates `.module` extension:
   ```kotlin
   import org.koin.ksp.generated.module

   fun commonModules(): List<Module> = listOf(
       NetworkModule().module,
       DatabaseModule().module,
       RepositoryModule().module,
   )
   ```

7. **Binding to interfaces** - use `binds` parameter:
   ```kotlin
   @Single(binds = [MyInterface::class])
   class MyImplementation : MyInterface
   ```

### DI Module Structure

```
shared/src/commonMain/kotlin/.../di/
  NetworkModule.kt      # HttpClient, API bindings
  DatabaseModule.kt     # SQLDelight Database
  RepositoryModule.kt   # @ComponentScan for repositories
  UseCaseModule.kt      # @ComponentScan for use cases
  AppModule.kt          # ViewModelModule, CoroutineScopeModule
  KoinInitializer.kt    # initKoin() entry point

shared/src/androidMain/kotlin/.../di/
  AndroidModule.kt      # Platform-specific: Context, SecureStorage, HttpEngine

shared/src/iosMain/kotlin/.../di/
  IosModule.kt          # Platform-specific: Keychain, Darwin engine

shared/src/desktopMain/kotlin/.../di/
  DesktopModule.kt      # Platform-specific: Desktop implementations
```

### Common Mistakes to Avoid

- Do NOT use `module { }` DSL for new code - use annotations
- Do NOT forget to add `@Single` or `@Factory` to new injectable classes
- Do NOT use `get()` in annotated classes - constructor injection is automatic
- Do NOT create circular dependencies - KSP will fail at compile time

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

This project uses custom **IBM Carbon Design System icons** implemented as Compose ImageVectors. The icons are defined in `composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/ui/icons/CarbonIcons.kt`.

### Available Icons

All icons are accessed via the `CarbonIcons` object:

- **Navigation**: `Bookmark`, `Folder`, `Settings`, `ArrowLeft`, `Home`
- **Actions**: `Renew`, `Share`, `Close`, `Checkmark`, `CheckmarkFilled`
- **Status**: `CircleOutline`, `WarningAlt`
- **Content**: `Document`, `Email`, `Map`, `TrashCan`
- **Categories**: `ColorPalette`, `Idea`, `Restaurant`, `GameWireless`, `RainDrop`, `Gem`

### Usage Rules

1. **Import from CarbonIcons**:
   ```kotlin
   import com.po4yka.bitesizereader.ui.icons.CarbonIcons
   ```

2. **Use icons with Material3 Icon component**:
   ```kotlin
   import androidx.compose.material3.Icon
   import com.po4yka.bitesizereader.ui.icons.CarbonIcons

   Icon(
       imageVector = CarbonIcons.Bookmark,
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

4. **Adding new icons**:
   - Get SVG path data from Carbon icons library: https://carbondesignsystem.com/elements/icons/library/
   - Add to `CarbonIcons.kt` using the `ImageVector.Builder` pattern
   - All icons should be 32x32dp (Carbon's standard size)

### Carbon Icons Documentation

- Icons Library: https://carbondesignsystem.com/elements/icons/library/
- GitHub: https://github.com/carbon-design-system/carbon

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
