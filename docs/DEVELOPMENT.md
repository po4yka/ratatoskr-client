# Development Guide

**Last Updated**: 2025-11-17

---

## Quick Start

### Prerequisites

- **macOS**: For iOS development
- **JDK 17+**: For Kotlin/Android
- **Android Studio**: Latest stable
- **Xcode 15+**: For iOS
- **Git**: Version control

### Initial Setup

```bash
# Clone repository
git clone https://github.com/po4yka/bite-size-reader-client.git
cd bite-size-reader-client

# Create local.properties
cat > local.properties << EOL
api.base.url=http://10.0.2.2:8000
client.id=android-app-v1.0
api.timeout.seconds=30
api.logging.enabled=true
EOL

# Build project
./gradlew build

# Run Android
./gradlew :composeApp:installDebug

# Run iOS (macOS only)
open iosApp/iosApp.xcodeproj
# Select iosApp scheme → Run (⌘R)
```

---

## Project Structure

```
bite-size-reader-client/
├── shared/              # Kotlin Multiplatform (90% code sharing)
├── composeApp/          # Android app (Jetpack Compose)
├── iosApp/              # iOS app (SwiftUI)
├── gradle/              # Gradle configuration
└── docs/                # Documentation
```

---

## Development Workflows

### Android Development

```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Install on device/emulator
./gradlew :composeApp:installDebug

# Run with Hot Reload (desktop)
./gradlew :composeApp:run
```

### iOS Development

```bash
# Open in Xcode
open iosApp/iosApp.xcodeproj

# Build from command line
xcodebuild -scheme iosApp -configuration Debug

# Run tests
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

### Code Quality

```bash
# Format code
./gradlew ktlintFormat

# Static analysis
./gradlew detekt

# Run all checks
./gradlew ktlintCheck detekt test
```

---

## Common Tasks

### Adding Dependencies

Edit `gradle/libs.versions.toml`:

```toml
[versions]
my-library = "1.0.0"

[libraries]
my-library = { module = "com.example:my-library", version.ref = "my-library" }
```

Then add to `shared/build.gradle.kts`:

```kotlin
commonMain.dependencies {
    implementation(libs.my.library)
}
```

### Database Migrations

Edit `shared/src/commonMain/sqldelight/`:

```sql
-- In schema file
ALTER TABLE summaries ADD COLUMN new_field TEXT;
```

Update models in `shared/src/commonMain/kotlin/domain/model/`.

### Adding API Endpoint

1. Add DTO in `data/remote/dto/`
2. Add method to `ApiClient.kt`
3. Update repository
4. Create/update use case
5. Update ViewModel

---

## IDE Setup

### Android Studio

1. **Install**: Download from [developer.android.com](https://developer.android.com)
2. **Open Project**: File → Open → Select project root
3. **Sync**: File → Sync Project with Gradle Files
4. **Run**: Select `composeApp` configuration → Run

**Recommended Plugins**:
- Kotlin
- Compose Multiplatform IDE Support
- SQLDelight
- ktlint

### Xcode

1. **Open**: `open iosApp/iosApp.xcodeproj`
2. **Select Scheme**: iosApp
3. **Select Device**: iPhone 15 simulator
4. **Run**: Product → Run (⌘R)

**Required Configuration**:
- See `docs/IOS_XCODE_SETUP.md` for Share Extension
- See `docs/WIDGETS_IMPLEMENTATION.md` for Widget setup

---

## Debugging

### Android

```bash
# View logs
adb logcat | grep BiteSizeReader

# View database
adb shell
run-as com.po4yka.bitesizereader.debug
cd databases
sqlite3 bite_size_reader.db
```

### iOS

```bash
# View logs (in Xcode)
# Window → Devices and Simulators → Open Console

# View database (simulator)
cd ~/Library/Developer/CoreSimulator/Devices/[DEVICE_ID]/data/Containers/Data/Application/[APP_ID]/Documents
```

---

## Troubleshooting

### Build Fails

```bash
# Clean build
./gradlew clean

# Clear Gradle cache
rm -rf ~/.gradle/caches

# Invalidate Android Studio caches
# File → Invalidate Caches / Restart
```

### iOS Pod Issues

```bash
cd iosApp
pod deintegrate
pod install
```

---

## Resources

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [SwiftUI](https://developer.apple.com/xcode/swiftui/)
- [Project README](../README.md)

---

**Maintained By**: Development Team
