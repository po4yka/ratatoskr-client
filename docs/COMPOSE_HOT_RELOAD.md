# Compose Hot Reload

This project is configured to use [Compose Hot Reload](https://github.com/JetBrains/compose-hot-reload) for rapid UI development with instant feedback.

## What is Compose Hot Reload?

Compose Hot Reload allows you to make changes to your Compose UI code and see the results in real time without restarting your application. This significantly speeds up the development workflow.

## Requirements

- **Kotlin**: 2.1.20 or higher ✅ (project uses 2.2.10)
- **Compose Multiplatform**: 1.8.2 or higher ✅ (project uses 1.9.1)
- **JVM Target**: Desktop or JVM target in your multiplatform project ✅
- **JetBrains Runtime**: Downloaded automatically via Foojay resolver

## Configuration

The project is already configured with:

1. **Compose Hot Reload Plugin** (v1.0.0-rc03) added to `gradle/libs.versions.toml`
2. **Desktop Target** added to both `composeApp` and `shared` modules
3. **Foojay Resolver** enabled in `settings.gradle.kts` for automatic JetBrains Runtime downloads
4. **Platform-specific implementations** for desktop (stubs for development)

## Running with Hot Reload

### Using IntelliJ IDEA or Android Studio

1. **Open the project** in IntelliJ IDEA or Android Studio

2. **Find the run configuration** for the desktop target:
   - Look for "desktopRun" or similar in the run configurations dropdown
   - Or create a new Gradle run configuration with task: `composeApp:runDesktop`

3. **Run the application**:
   ```bash
   ./gradlew :composeApp:runDesktop
   ```

4. **Make changes** to any Compose UI code in:
   - `composeApp/src/commonMain/kotlin/` (shared UI code)
   - `composeApp/src/androidMain/kotlin/` (Android UI code)
   - `composeApp/src/desktopMain/kotlin/` (Desktop UI code)

5. **See instant updates** without restarting the app!

### Terminal/Command Line

```bash
# Run the desktop app with hot reload
./gradlew :composeApp:runDesktop

# The app will launch in a desktop window
# Edit any Compose UI file and save
# Changes appear instantly in the running application
```

## What Can Be Hot Reloaded?

✅ **Supported:**
- Compose UI functions (`@Composable` functions)
- UI layout changes
- Color, text, and style modifications
- Conditional logic in UI code
- State modifications in composables

❌ **Not Supported (requires restart):**
- Changes to ViewModel logic
- Changes to repository/data layer
- New dependencies
- Configuration changes
- Changes to non-Compose code

## Desktop Development Setup

For hot reload development, the project includes a **desktop target** with stub implementations:

### Desktop-Specific Files

**Shared Module** (`shared/src/desktopMain/`):
- `Platform.desktop.kt` - Platform information
- `data/local/DatabaseDriverFactory.kt` - In-memory SQLite database
- `data/local/SecureStorageImpl.kt` - In-memory storage (not secure, dev only)
- `util/share/DesktopShareManager.kt` - Console-based sharing
- `util/network/DesktopNetworkMonitor.kt` - Always-connected stub

**ComposeApp Module** (`composeApp/src/desktopMain/`):
- `main.kt` - Desktop entry point with Koin initialization
- `di/DesktopModule.kt` - Desktop-specific dependency injection

### Development Notes

⚠️ **Important**: The desktop target is primarily for **UI development with hot reload**. Desktop implementations are stubs that:
- Store data in memory (lost on restart)
- Don't provide real secure storage
- Simulate network connectivity
- Print share actions to console

For production features, always test on the actual target platforms (Android/iOS).

## Tips for Effective Hot Reload

1. **Focus on UI changes**: Hot reload works best for Compose UI code
2. **Keep the app running**: Leave the desktop app running while you develop
3. **Save files**: Changes take effect when you save the file
4. **Check the console**: Any reload errors will appear in the console
5. **Restart when needed**: If hot reload isn't working, restart the app

## Troubleshooting

### Hot Reload Not Working

1. **Check the console** for error messages
2. **Verify JetBrains Runtime** is installed (downloaded automatically)
3. **Restart the application** if hot reload stops working
4. **Clean and rebuild**:
   ```bash
   ./gradlew clean
   ./gradlew :composeApp:runDesktop
   ```

### Build Errors

If you encounter build errors related to the desktop target:

1. **Sync Gradle**: In your IDE, click "Sync Now" or run:
   ```bash
   ./gradlew --refresh-dependencies
   ```

2. **Check Java version**: Ensure you're using Java 11 or higher
   ```bash
   java -version
   ```

3. **Invalidate caches**: In IntelliJ IDEA: File → Invalidate Caches → Invalidate and Restart

## Production Builds

The desktop target is **only for development**. For production builds:

**Android:**
```bash
./gradlew :composeApp:assembleRelease
```

**iOS:**
Build through Xcode or using Kotlin Multiplatform Mobile plugin

## Resources

- [Compose Hot Reload GitHub](https://github.com/JetBrains/compose-hot-reload)
- [Kotlin Multiplatform Documentation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)

## Version Information

- **Compose Hot Reload**: 1.0.0-rc03
- **Kotlin**: 2.2.10
- **Compose Multiplatform**: 1.9.1
- **Status**: Release Candidate (RC) - Stable for development use
